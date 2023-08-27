package me.langyue.equipmentstandard.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin {

    @Inject(method = "method_331", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0))
    private static void processStacksMixin(ServerWorld world, Consumer<ItemStack> lootConsumer, ItemStack itemStack, CallbackInfo info) {
        if (world.isClient) return;
        ModifierUtils.setItemStackAttribute(itemStack);
    }

    @Inject(method = "method_331", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void processStacksMixin(ServerWorld world, Consumer<ItemStack> lootConsumer, ItemStack itemStack, CallbackInfo info, int i, ItemStack itemStack2) {
        if (world.isClient) return;
        ModifierUtils.setItemStackAttribute(itemStack);
    }

    @Inject(method = "supplyInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void supplyInventoryMixin(Inventory inventory, LootContextParameterSet parameters, long seed,
                                      CallbackInfo ci,
                                      LootContext lootContext,
                                      ObjectArrayList<ItemStack> objectArrayList,
                                      Random random,
                                      List<Integer> list,
                                      ObjectListIterator var9,
                                      ItemStack itemStack) {
        if (lootContext.getWorld().isClient) return;
        ModifierUtils.setItemStackAttribute(itemStack, 0, lootContext.getLuck());
    }
}
