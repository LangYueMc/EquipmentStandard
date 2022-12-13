package me.langyue.equipmentstandard.mixin.client;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.CustomEntityAttributes;
import me.langyue.equipmentstandard.api.EquipmentComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private final Multimap<EntityAttribute, EntityAttributeModifier> hide = LinkedListMultimap.create();

    /**
     * 是否显示详情
     */
    private static boolean hideModifierDetails() {
        return EquipmentStandard.CONFIG.mergeModifiers && !Screen.hasShiftDown();
    }

    @Inject(method = "getTooltip", at = @At("HEAD"))
    private void head(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        modifiers.clear();
        hide.clear();
        entry = null;
        merged = false;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"))
    private Multimap<EntityAttribute, EntityAttributeModifier> mergeModifier(Multimap<EntityAttribute, EntityAttributeModifier> modifiers) {
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
            this.modifiers.put(attribute, modifier);
        }
        // 标记是否显示额外提示
        merged |= !processed.isEmpty();
        return this.modifiers;
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;"))
    private Object getEntry(Map.Entry<EntityAttribute, EntityAttributeModifier> entry) {
        this.entry = entry;
        return entry.getValue();
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 7))
    private boolean modifyTooltipEquals(List<Text> list, Object object) {
        Text text = (Text) object;
        if (hideModifierDetails() && entry.getValue().getOperation() == EntityAttributeModifier.Operation.ADDITION) {
            // 合并 MULTIPLY_BASE，忽略 MULTIPLY_TOTAL
            // 在这之前已经合并了所有同类项，所以这里每个属性最多三种，只处理 MULTIPLY_BASE 的, 也只有一个
            Optional<EntityAttributeModifier> first = this.modifiers.get(entry.getKey()).stream()
                    .filter(it -> it.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE).findFirst();
            if (first.isPresent()) {
                EntityAttributeModifier modifier = first.get();
                for (Text sibling : text.getSiblings()) {
                    // 仅固定值的是这种结构，这里也只修改固定值的，比如武器的攻击和攻速
                    if (sibling.getContent() instanceof TranslatableTextContent content && content.getKey().startsWith("attribute.modifier.")) {
                        content.getArgs()[0] += String.format(" §7(%s%s%%§7)§r", modifier.getValue() < 0 ? "§c-" : "§9+", ItemStack.MODIFIER_FORMAT.format(modifier.getValue() * 100));
                        hide.put(entry.getKey(), modifier);
                        merged = true;
                        break;
                    }
                }
            }
        }
        return addTooltip(list, text);
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 8))
    private boolean modifyTooltipPlus(List<Text> list, Object object) {
        return addTooltip(list, (Text) object);
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 9))
    private boolean modifyTooltipTake(List<Text> list, Object object) {
        return addTooltip(list, (Text) object);
    }

    private boolean addTooltip(List<Text> list, Text text) {
        if (hide.containsEntry(entry.getKey(), entry.getValue())) return false;
        if (EquipmentStandard.CONFIG.showMultiplyOperationAdditional
                && entry.getValue().getOperation() != EntityAttributeModifier.Operation.ADDITION
                && !entry.getKey().equals(CustomEntityAttributes.CRIT_CHANCE)
                && !entry.getKey().equals(CustomEntityAttributes.CRIT_DAMAGE)
        ) {
            text.getSiblings().add(Text.translatable("attribute.modifier.additional." + entry.getValue().getOperation().getId()).formatted(Formatting.DARK_GRAY));
        }
        return list.add(text);
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void showDetails(
            @Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir,
            List<Text> list
    ) {
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
        EquipmentComponents components = EquipmentComponents.fromItem((ItemStack) (Object) this);
        if (components == null) {
            return;
        }
        list.add(Text.translatable("item.maker", components.getMaker()));
    }
}
