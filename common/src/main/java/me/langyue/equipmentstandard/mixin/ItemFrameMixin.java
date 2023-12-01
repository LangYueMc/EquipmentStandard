package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrame.class)
public class ItemFrameMixin {

    @ModifyVariable(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ItemFrame;setItem(Lnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack interactMixin(ItemStack itemStack) {
        ModifierUtils.mark(itemStack);
        return itemStack;
    }

    @Inject(method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ItemFrame;onItemChanged(Lnet/minecraft/world/item/ItemStack;)V"))
    private void setItemMixin(ItemStack value, boolean update, CallbackInfo info) {
        if (((ItemFrame) (Object) this).level().isClientSide()) return;
        if (!EquipmentStandard.CONFIG.appliedToWorldGeneration) return;
        ModifierUtils.setItemStackAttribute(value, EquipmentStandard.nextBetween(-9999, 2000), 0);
    }
}
