package me.langyue.equipmentstandard.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin {

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At(value = "HEAD"))
    private void getRandomItemsRawHeadMixin(
            LootContext lootContext, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir,
            @Share("luck") LocalFloatRef luck
    ) {
        luck.set(lootContext.getLuck());
    }

    @Inject(method = "method_331", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0))
    private static void processStacksMixin(
            ServerLevel serverLevel, Consumer<ItemStack> consumer, ItemStack itemStack, CallbackInfo ci,
            @Share("luck") LocalFloatRef luck
    ) {
        if (serverLevel.isClientSide()) return;
        if (!EquipmentStandard.CONFIG.appliedToLoot) return;
        ModifierUtils.setItemStackAttribute(itemStack, EquipmentStandard.nextBetween(-9999, 2000), luck.get());
    }

    @Inject(method = "method_331", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void processStacksMixin(
            ServerLevel serverLevel, Consumer<ItemStack> consumer, ItemStack itemStack, CallbackInfo ci,
            int i, ItemStack itemStack2, @Share("luck") LocalFloatRef luck
    ) {
        if (serverLevel.isClientSide()) return;
        if (!EquipmentStandard.CONFIG.appliedToLoot) return;
        ModifierUtils.setItemStackAttribute(itemStack2, EquipmentStandard.nextBetween(-9999, 2000), luck.get());
    }

    @Inject(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void fillMixin(Container container, LootParams lootParams, long seed,
                           CallbackInfo ci,
                           LootContext lootContext,
                           ObjectArrayList<ItemStack> objectArrayList,
                           RandomSource random,
                           List<Integer> list,
                           ObjectListIterator<ItemStack> var9,
                           ItemStack itemStack) {
        if (lootContext.getLevel().isClientSide()) return;
        if (!EquipmentStandard.CONFIG.appliedToLoot) return;
        ModifierUtils.setItemStackAttribute(itemStack, 0, lootContext.getLuck());
    }
}
