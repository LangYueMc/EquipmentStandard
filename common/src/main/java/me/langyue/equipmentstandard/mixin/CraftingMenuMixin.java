package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin extends AbstractContainerMenu {

    private CraftingMenuMixin(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER), ordinal = 1)
    private ItemStack quickMoveStack1Mixin(ItemStack itemStack) {
        Slot slot;
        for (int i = 10; i < 46; i++) {
            slot = this.slots.get(i);
            if (slot.getItem().isEmpty()) {
                return itemStack;
            }
        }
        // 背包满了，没地方放，这时候合成结果会保留在结果格子里，这里就不修改属性了
        ModifierUtils.mark(itemStack);
        return itemStack;
    }
}
