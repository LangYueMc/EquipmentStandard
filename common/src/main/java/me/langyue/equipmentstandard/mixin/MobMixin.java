package me.langyue.equipmentstandard.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "setItemSlot", at = @At("HEAD"))
    private void setItemSlotMixin(EquipmentSlot equipmentSlot, ItemStack itemStack, CallbackInfo ci) {
        if (!itemStack.isEmpty()) {
            ModifierUtils.setItemStackAttribute(itemStack, EquipmentStandard.nextBetween(0, 2000), 0);
        }
    }

    @ModifyVariable(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 2)
    private float critAttackMixin(float f, @Local(ordinal = 0) Entity target) {
        var critDamageMultiplier = 1.0f;
        try {
            // 暴击后
            if (MixinUtils.isCrit((Mob) (Object) this)) {
                critDamageMultiplier = MixinUtils.getCritDamageMultiplier((Mob) (Object) this, 1.5f);
            }
        } catch (Throwable ignored) {
        }
        return f * critDamageMultiplier;
    }

    @Inject(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void realDamageMixin(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // 真伤
        MixinUtils.realDamageMixin((Mob) (Object) this, entity);
    }

}
