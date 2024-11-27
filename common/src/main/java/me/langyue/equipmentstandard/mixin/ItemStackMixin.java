package me.langyue.equipmentstandard.mixin;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.langyue.equipmentstandard.api.*;
import me.langyue.equipmentstandard.api.data.EquipmentComponents;
import me.langyue.equipmentstandard.api.data.ItemRarity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements EquipmentComponentsAccessor, ItemStackAccessor {

    @Unique
    private boolean es$shouldHookGetAttributeModifiers = true;

    @Override
    public Multimap<Attribute, AttributeModifier> es$getOriginalAttributeModifiers(EquipmentSlot equipmentSlot) {
        this.es$shouldHookGetAttributeModifiers = false;
        var attributeModifiers = ((ItemStack) (Object) this).getAttributeModifiers(equipmentSlot);
        this.es$shouldHookGetAttributeModifiers = true;
        return attributeModifiers;
    }

    @ModifyReturnValue(
            method = "getAttributeModifiers",
            at = @At("RETURN")
    )
    private Multimap<Attribute, AttributeModifier> hookGetAttributeModifiers(Multimap<Attribute, AttributeModifier> original, @Local(argsOnly = true) EquipmentSlot equipmentSlot) {
        if (!es$shouldHookGetAttributeModifiers) return original;
        Multimap<Attribute, AttributeModifier> modifierMultimap = LinkedListMultimap.create(original);
        ModifierUtils.modify((ItemStack) (Object) this, equipmentSlot, modifierMultimap);
        return modifierMultimap;
    }

    @ModifyReturnValue(
            method = "getHoverName",
            at = @At("RETURN")
    )
    private Component getHoverNameMixin(Component original) {
        var rarity = this.es$getItemRarity();
        if (rarity != null) {
            MutableComponent name = original.copy();
            if (rarity.getFormatting() != null && rarity.getFormatting().length > 0) {
                name.withStyle(rarity.getFormatting());
            }
            MutableComponent prefix = rarity.getPrefix();
            if (prefix != null) {
                if (rarity.getFormatting() != null && rarity.getFormatting().length > 0) {
                    prefix.withStyle(rarity.getFormatting());
                }
                name = prefix.append(name);
            }
            return name;
        }
        return original;
    }

    @ModifyReturnValue(
            method = "getMaxDamage",
            at = @At("RETURN")
    )
    private int getMaxDamageMixin(int original) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isDamageableItem())
            return ModifierUtils.getMaxDamage(stack, original);
        return original;
    }

    @Override
    public EquipmentComponents es$getComponents() {
        return EquipmentComponents.fromItem((ItemStack) (Object) this);
    }

    @Override
    public String es$getMaker() {
        EquipmentComponents components = es$getComponents();
        return components == null ? null : components.getMaker();
    }

    @Override
    public void es$setMaker(Player player) {
        if (player.level().isClientSide()) return;
        if (es$getComponents() == null) {
            // 仅能设置一次
            ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) player;
            new EquipmentComponents(player.getDisplayName().getString(), proficiencyAccessor.es$getProficiency()).save((ItemStack) (Object) this);
        }
    }

    @Override
    public Integer es$getScore() {
        EquipmentComponents components = this.es$getComponents();
        return components == null ? null : components.getScore();
    }

    @Override
    public void es$updateScore() {
        ItemRarity.Rarity rarity = ItemRarityManager.get((ItemStack) (Object) this);
        if (rarity == null) {
            return;
        }
        EquipmentComponents components = es$getComponents();
        if (components == null) {
            components = new EquipmentComponents();
        }
        components.setScore(rarity.getScore());
        components.setRarity(rarity);
        components.save((ItemStack) (Object) this);
    }

    @Override
    public ItemRarity.Rarity es$getItemRarity() {
        EquipmentComponents components = this.es$getComponents();
        return components == null ? null : components.getRarity();
    }
}
