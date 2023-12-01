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
        if (player.isLocalPlayer()) return;
        // 除了玩家放置，其余都会应用属性，包括发射器，这是可接受的，反正也就应用一次
        ModifierUtils.mark(stack);
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"))
    private void setItemSlotMixin(EquipmentSlot slot, ItemStack stack, CallbackInfo info) {
        if (((ArmorStand) (Object) this).level().isClientSide()) return;
        if (!EquipmentStandard.CONFIG.appliedToWorldGeneration) return;
        ModifierUtils.setItemStackAttribute(stack, EquipmentStandard.nextBetween(-9999, 2000), 0);
    }
}
