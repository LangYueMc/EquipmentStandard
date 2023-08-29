package me.langyue.equipmentstandard.mixin.client;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.CustomEntityAttributes;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(ItemStack.class)
@Environment(EnvType.CLIENT)
public abstract class ItemStackClientMixin {

    @Shadow
    public abstract boolean isDamageable();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract int getDamage();

    private boolean merged;

    private final Multimap<EntityAttribute, EntityAttributeModifier> modifiers = LinkedListMultimap.create();

    private Map.Entry<EntityAttribute, EntityAttributeModifier> entry;

    private final Map<UUID, Double> mergeText = new HashMap<>();

    private final ItemStack _this = (ItemStack) (Object) this;

    /**
     * 是否显示详情
     */
    private static boolean hideModifierDetails() {
        return EquipmentStandard.CONFIG.mergeModifiers && !Screen.hasShiftDown();
    }

    @Inject(method = "getTooltip", at = @At("HEAD"))
    private void head(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        modifiers.clear();
        entry = null;
        merged = false;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"))
    private Multimap<EntityAttribute, EntityAttributeModifier> mergeModifier(Multimap<EntityAttribute, EntityAttributeModifier> modifiers) {
        this.modifiers.clear();
        if (modifiers.isEmpty() || !hideModifierDetails()) return modifiers;
        Multimap<EntityAttribute, EntityAttributeModifier> processed = LinkedListMultimap.create();
        for (var entry : modifiers.entries()) {
            var attribute = entry.getKey();
            var modifier = entry.getValue();
            if (processed.containsEntry(attribute, modifier)) {
                continue;
            }
            Collection<EntityAttributeModifier> collection = modifiers.get(attribute);
            double count = modifier.getValue();
            for (EntityAttributeModifier temp : collection) {
                if (temp.equals(modifier) || temp.getOperation() != modifier.getOperation()) {
                    continue;
                }
                switch (modifier.getOperation()) {
                    case ADDITION -> count += temp.getValue();
                    case MULTIPLY_BASE, MULTIPLY_TOTAL ->
                        // 应该不可能会有多个百分比加成的, 但万一有多个，MC 的算法就是这样的
                        // 比如 10 攻击力，第一个加 10% ，会先计算得出攻击力为 11，然后又有一个加成 20%，则会用11*(1+0.2)=13.2
                            count += (count + 1) * temp.getValue();
                }
                processed.put(attribute, temp);
            }
            modifier = new EntityAttributeModifier(modifier.getId(), modifier.getName(), count, modifier.getOperation());
            if (!this.modifiers.containsEntry(attribute, modifier))
                this.modifiers.put(attribute, modifier);
        }
        processed.clear();
        for (var entry : this.modifiers.entries()) {
            // 将攻击和攻速的 MULTIPLY_BASE 抽出与 ADDITION 合并显示
            var attribute = entry.getKey();
            var modifier = entry.getValue();
            if (!ModifierUtils.ATTACK_DAMAGE_MODIFIER_ID.equals(modifier.getId())
                    || ModifierUtils.ATTACK_SPEED_MODIFIER_ID.equals(modifier.getId())
                    || !EntityAttributeModifier.Operation.ADDITION.equals(modifier.getOperation())) {
                continue;
            }
            Collection<EntityAttributeModifier> collection = modifiers.get(attribute);
            for (EntityAttributeModifier temp : collection) {
                if (EntityAttributeModifier.Operation.MULTIPLY_BASE.equals(temp.getOperation())) {
                    mergeText.put(modifier.getId(), temp.getValue());
                    processed.put(attribute, temp);
                }
            }
        }
        processed.forEach(this.modifiers::remove);
        // 标记是否显示额外提示
        merged |= this.modifiers.size() != modifiers.size();
        return this.modifiers;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;"))
    private Map.Entry<EntityAttribute, EntityAttributeModifier> getEntry(Map.Entry<EntityAttribute, EntityAttributeModifier> entry) {
        this.entry = entry;
        return entry;
    }

    @Redirect(
            method = "getTooltip",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getOperation()Lnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 1)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;")
    )
    private MutableText modifyTooltip(MutableText text, Formatting formatting) {
        if (hideModifierDetails() && mergeText.containsKey(entry.getValue().getId())) {
            Double amount = mergeText.get(entry.getValue().getId());
            for (Text sibling : text.getSiblings()) {
                // 仅固定值的是这种结构，这里也只修改固定值的，比如武器的攻击和攻速
                if (sibling.getContent() instanceof TranslatableTextContent content && content.getKey().startsWith("attribute.modifier.")) {
                    content.getArgs()[0] += String.format(" §7(%s%s%%§7)§r", amount < 0 ? "§c" : "§9+", ItemStack.MODIFIER_FORMAT.format(amount * 100));
                    merged = true;
                    break;
                }
            }
        }
        if (!entry.getKey().equals(CustomEntityAttributes.CRIT_CHANCE)
                && !entry.getKey().equals(CustomEntityAttributes.CRIT_DAMAGE)) {
            text.getSiblings().add(Text.translatable("attribute.modifier.additional." + entry.getValue().getOperation().getId()).formatted(Formatting.DARK_GRAY));
        }
        return text.formatted(formatting);
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void showDetails(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        if (list != null && merged && hideModifierDetails()) {
            list.add(ScreenTexts.EMPTY);
            list.add(Text.translatable("item.modifiers.show_details").formatted(Formatting.GRAY));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/item/TooltipContext;isAdvanced()Z", ordinal = 2), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void showDurability(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        if (!context.isAdvanced() && EquipmentStandard.CONFIG.showDurability && isDamageable()) {
            list.add(Text.translatable("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "TAIL"))
    private void getTooltipMixin(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> list = cir.getReturnValue();
        if (ModifierUtils.isEs(_this)) {
            String maker = _this.getMaker();
            if (maker != null)
                list.add(Text.translatable("item.maker", maker));
            Integer score = _this.getScore();
            list.add(Text.translatable("item.score", score == null ? 0 : score));
        }
    }
}
