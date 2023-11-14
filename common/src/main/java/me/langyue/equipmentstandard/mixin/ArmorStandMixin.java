package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin {

    @Inject(method = "swapItem", at = @At("HEAD"))
    private void swapItemMixin(Player player, EquipmentSlot equipmentSlot, ItemStack stack, InteractionHand interactionHand, CallbackInfoReturnable<Boolean> cir) {
        if (player == null || player.level().isClientSide()) return;
        ModifierUtils.mark(stack);
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"))
    private void setItemSlotMixin(EquipmentSlot slot, ItemStack stack, CallbackInfo info) {
        if (((ArmorStand) (Object) this).level().isClientSide()) return;
        ModifierUtils.setItemStackAttribute(stack, EquipmentStandard.nextBetween(-9999, 2000), 0);
    }
}
