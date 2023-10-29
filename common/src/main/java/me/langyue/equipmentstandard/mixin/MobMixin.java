package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot arg);

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void finalizeSpawnMixin(
            ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
            MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData,
            CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            if (!itemStack.isEmpty()) {
                ModifierUtils.setItemStackAttribute(itemStack, EquipmentStandard.nextBetween(-9999, 2000), 0);
            }
        }
    }
}
