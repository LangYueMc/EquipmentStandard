package me.langyue.equipmentstandard.mixin.client;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.CustomAttributes;
import me.langyue.equipmentstandard.api.EquipmentComponentsAccessor;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(ItemStack.class)
@Environment(EnvType.CLIENT)
public abstract class ItemStackClientMixin {

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract boolean isDamageableItem();

    @Shadow
    public abstract int getDamageValue();

    /**
     * 是否显示详情
     */
    @Unique
    private static boolean es$hideModifierDetails() {
        return EquipmentStandard.CONFIG.mergeModifiers && !Screen.hasShiftDown();
    }

    @ModifyVariable(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"))
    private Multimap<Attribute, AttributeModifier> mergeModifier(
            Multimap<Attribute, AttributeModifier> modifiers,
            @Share("merged") LocalBooleanRef merged,
            @Share("modifiers") LocalRef<Map<UUID, Double>> mergedModifiersRef
    ) {
        if (modifiers.isEmpty() || !es$hideModifierDetails()) return modifiers;
        Multimap<Attribute, AttributeModifier> tempMap = LinkedListMultimap.create();
        Multimap<Attribute, AttributeModifier> processed = LinkedListMultimap.create();
        Map<UUID, Double> mergedModifiers = new HashMap<>();
        for (var entry : modifiers.entries()) {
            var attribute = entry.getKey();
            var modifier = entry.getValue();
            if (processed.containsEntry(attribute, modifier)) {
                continue;
            }
            Collection<AttributeModifier> collection = modifiers.get(attribute);
            double count = modifier.getAmount();
            for (AttributeModifier temp : collection) {
                if (temp.equals(modifier) || temp.getOperation() != modifier.getOperation()) {
                    continue;
                }
                switch (modifier.getOperation()) {
                    case ADDITION -> count += temp.getAmount();
                    case MULTIPLY_BASE, MULTIPLY_TOTAL ->
                        // 应该不可能会有多个百分比加成的, 但万一有多个，MC 的算法就是这样的
                        // 比如 10 攻击力，第一个加 10% ，会先计算得出攻击力为 11，然后又有一个加成 20%，则会用11*(1+0.2)=13.2
                            count += (count + 1) * temp.getAmount();
                }
                processed.put(attribute, temp);
            }
            modifier = new AttributeModifier(modifier.getId(), modifier.getName(), count, modifier.getOperation());
            if (!tempMap.containsEntry(attribute, modifier))
                tempMap.put(attribute, modifier);
        }
        processed.clear();
        for (var entry : tempMap.entries()) {
            // 将攻击和攻速的 MULTIPLY_BASE 抽出与 ADDITION 合并显示
            var attribute = entry.getKey();
            var modifier = entry.getValue();
            if (!ModifierUtils.ATTACK_DAMAGE_MODIFIER_ID.equals(modifier.getId())
                    || ModifierUtils.ATTACK_SPEED_MODIFIER_ID.equals(modifier.getId())
                    || !AttributeModifier.Operation.ADDITION.equals(modifier.getOperation())) {
                continue;
            }
            Collection<AttributeModifier> collection = modifiers.get(attribute);
            for (AttributeModifier temp : collection) {
                if (AttributeModifier.Operation.MULTIPLY_BASE.equals(temp.getOperation())) {
                    mergedModifiers.put(modifier.getId(), temp.getAmount());
                    processed.put(attribute, temp);
                }
            }
        }
        processed.forEach(tempMap::remove);
        // 标记是否显示额外提示
        merged.set(merged.get() || tempMap.size() != modifiers.size());
        mergedModifiersRef.set(mergedModifiers);
        return tempMap;
    }

    @Redirect(
            method = "getTooltipLines",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;getOperation()Lnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z", ordinal = 1)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;")
    )
    private MutableComponent modifyTooltip(
            MutableComponent component, ChatFormatting formatting,
            @Local Map.Entry<Attribute, AttributeModifier> entry,
            @Share("merged") LocalBooleanRef merged,
            @Share("modifiers") LocalRef<Map<UUID, Double>> mergedModifiersRef
    ) {
        var mergedModifiers = mergedModifiersRef.get();
        if (es$hideModifierDetails() && mergedModifiers.containsKey(entry.getValue().getId())) {
            Double amount = mergedModifiers.get(entry.getValue().getId());
            for (var sibling : component.getSiblings()) {
                // 仅固定值的是这种结构，这里也只修改固定值的，比如武器的攻击和攻速
                if (sibling.getContents() instanceof TranslatableContents content && content.getKey().startsWith("attribute.modifier.")) {
                    content.getArgs()[0] += String.format(" §7(%s%s%%§7)§r", amount < 0 ? "§c" : "§9+", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount * 100));
                    merged.set(true);
                    break;
                }
            }
        }
        if (EquipmentStandard.CONFIG.showMultiplyOperationAdditional
                && !entry.getKey().equals(CustomAttributes.CRIT_CHANCE)
                && !entry.getKey().equals(CustomAttributes.CRIT_DAMAGE)) {
            component.getSiblings().add(Component.translatable("attribute.modifier.additional." + entry.getValue().getOperation().toValue()).withStyle(ChatFormatting.DARK_GRAY));
        }
        return component.withStyle(formatting);
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void showDetails(
            Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, List<Component> list,
            @Share("merged") LocalBooleanRef merged
    ) {
        if (list != null && merged.get() && es$hideModifierDetails()) {
            list.add(Component.empty());
            list.add(Component.translatable("item.modifiers.show_details").withStyle(ChatFormatting.GRAY));
        }
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/TooltipFlag;isAdvanced()Z", ordinal = 2), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void showDurability(Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        if (!tooltipFlag.isAdvanced() && EquipmentStandard.CONFIG.showDurability && isDamageableItem()) {
            list.add(Component.translatable("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
        }
    }

    @Inject(method = "getTooltipLines", at = @At(value = "TAIL"))
    private void getTooltipMixin(Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        List<Component> list = cir.getReturnValue();
        if (ModifierUtils.isEs((ItemStack) (Object) this)) {
            EquipmentComponentsAccessor accessor = (EquipmentComponentsAccessor) this;
            String maker = accessor.es$getMaker();
            if (maker != null) {
                list.add(Component.translatable("item.maker", maker));
            }
            if (EquipmentStandard.CONFIG.showScore) {
                Integer score = accessor.es$getScore();
                list.add(Component.translatable("item.score", score == null ? 0 : score));
            }
        }
    }
}
