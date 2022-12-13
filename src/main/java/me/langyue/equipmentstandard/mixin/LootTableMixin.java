package me.langyue.equipmentstandard.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin {

    @Inject(method = "supplyInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void supplyInventoryMixin(Inventory inventory, LootContext context, CallbackInfo info,
                                      ObjectArrayList<ItemStack> objectArrayList, Random random, List<Integer> list,
                                      ObjectListIterator<ItemStack> var6, ItemStack itemStack) {
        ModifierUtils.setItemStackAttribute(itemStack, 0, random.nextDouble() * context.getLuck());
    }
}
