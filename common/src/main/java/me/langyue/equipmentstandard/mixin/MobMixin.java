package me.langyue.equipmentstandard.mixin;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.Local;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.MixinUtils;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void finalizeSpawnMixin(
            ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
            MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData,
            CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (!EquipmentStandard.CONFIG.appliedToMob) return;
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            if (!itemStack.isEmpty()) {
                this.getAttributes().removeAttributeModifiers(itemStack.getAttributeModifiers(equipmentSlot));
                ModifierUtils.setItemStackAttribute(itemStack, EquipmentStandard.nextBetween(-9999, 2000), 0);
                this.getAttributes().addTransientAttributeModifiers(itemStack.getAttributeModifiers(equipmentSlot));
            }
        }
        this.setHealth(this.getMaxHealth());
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"))
    private void setItemSlotMixin(EquipmentSlot equipmentSlot, ItemStack itemStack, CallbackInfo ci) {
        if (!EquipmentStandard.CONFIG.appliedToMob) return;
        if (!itemStack.isEmpty()) {
            this.getAttributes().removeAttributeModifiers(itemStack.getAttributeModifiers(equipmentSlot));
            boolean b = ModifierUtils.setItemStackAttribute(itemStack, EquipmentStandard.nextBetween(-9999, 2000), 0);
            Multimap<Attribute, AttributeModifier> attributeModifiers = itemStack.getAttributeModifiers(equipmentSlot);
            this.getAttributes().addTransientAttributeModifiers(attributeModifiers);
            if (b) {
                AtomicReference<Double> heal = new AtomicReference<>(0d);
                attributeModifiers.get(Attributes.MAX_HEALTH).forEach(modifier -> heal.updateAndGet(v -> modifier.getAmount()));
                this.heal(heal.get().floatValue());
            }
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
