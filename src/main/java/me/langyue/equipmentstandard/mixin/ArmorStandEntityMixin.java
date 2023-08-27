package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin {

    private final ArmorStandEntity _this = (ArmorStandEntity) (Object) this;

    @Inject(method = "equip", at = @At("HEAD"))
    private void equip(PlayerEntity player, EquipmentSlot slot, ItemStack stack, Hand hand, CallbackInfoReturnable<Boolean> cir) {
        if (player == null || player.getWorld().isClient) return;
        ModifierUtils.mark(stack);
    }

    @Inject(method = "equipStack", at = @At("HEAD"))
    private void equipStackMixin(EquipmentSlot slot, ItemStack stack, CallbackInfo info) {
        if (_this.getWorld().isClient) return;
        ModifierUtils.setItemStackAttribute(stack);
    }
}
