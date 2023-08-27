package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {

    private final ItemFrameEntity _this = (ItemFrameEntity) (Object) this;


    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setHeldItemStack(Lnet/minecraft/item/ItemStack;)V"))
    private void interactHeadMixin(ItemFrameEntity value, ItemStack itemStack) {
        if (_this.getWorld().isClient) return;
        ModifierUtils.mark(itemStack);
        value.setHeldItemStack(itemStack);
    }

    @Inject(method = "setHeldItemStack(Lnet/minecraft/item/ItemStack;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setAsStackHolder(Lnet/minecraft/item/ItemStack;)V"))
    private void setHeldItemStackMixin(ItemStack value, boolean update, CallbackInfo info) {
        if (_this.getWorld().isClient) return;
        ModifierUtils.setItemStackAttribute(value);
    }
}
