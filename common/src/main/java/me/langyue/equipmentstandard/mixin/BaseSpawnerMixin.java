package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifyEntity(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci, boolean bl, RandomSource randomSource, SpawnData spawnData, int i, CompoundTag compoundTag, Optional optional, ListTag listTag, int j, double d, double e, double f, BlockPos blockPos2, Entity entity) {
        if (!EquipmentStandard.CONFIG.appliedToSpawnerBlock) return;
        this.es$modifyEntityRecursive(entity);
    }

    @Unique
    private void es$modifyEntityRecursive(Entity entity) {
        entity.getHandSlots().forEach(itemStack -> ModifierUtils.setItemStackAttribute(itemStack, EquipmentStandard.nextBetween(-9999, 2000), 0));
        entity.getArmorSlots().forEach(itemStack -> ModifierUtils.setItemStackAttribute(itemStack, EquipmentStandard.nextBetween(-9999, 2000), 0));
        entity.getPassengers().forEach(this::es$modifyEntityRecursive);
    }
}
