package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.MixinUtils;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot arg);

    @Unique
    private Entity es$target;

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

    @Inject(method = "doHurtTarget", at = @At("HEAD"))
    private void finalizeSpawnMixin(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        this.es$target = entity;
    }

    @ModifyArg(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private float finalizeSpawnMixin(float f) {
        try {
            return MixinUtils.critAttackMixin((Mob) (Object) this, es$target, f);
        } catch (Throwable e) {
            return f;
        }
    }

}
