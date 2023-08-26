package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.CustomEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    private final PlayerEntity player = (PlayerEntity) (Object) this;

    /**
     * 挖掘速度
     * 原作者 @Draylar
     */
    @ModifyVariable(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectUtil;hasHaste(Lnet/minecraft/entity/LivingEntity;)Z"), index = 2)
    private float getBlockBreakingSpeedMixin(float f) {
        EntityAttributeInstance instance = player.getAttributeInstance(CustomEntityAttributes.DIG_SPEED);

        if (instance != null) {
            for (EntityAttributeModifier modifier : instance.getModifiers(EntityAttributeModifier.Operation.ADDITION)) {
                f += modifier.getValue();
            }
            for (EntityAttributeModifier modifier : instance.getModifiers(EntityAttributeModifier.Operation.MULTIPLY_BASE)) {
                f *= (1 + modifier.getValue());
            }
            for (EntityAttributeModifier modifier : instance.getModifiers(EntityAttributeModifier.Operation.MULTIPLY_TOTAL)) {
                f *= (1 + modifier.getValue());
            }
        }

        return f;
    }

    private Entity target;


    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void attackMixin(Entity target, CallbackInfo ci) {
        this.target = target;
    }

    /**
     * 暴击
     */
    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", shift = At.Shift.AFTER), ordinal = 0, require = 0)
    private float critAttack(float f) {
        double chance = EquipmentStandard.CONFIG.baseCritChance;
        // 暴击几率
        EntityAttributeInstance chanceInstance = player.getAttributeInstance(CustomEntityAttributes.CRIT_CHANCE);
        if (chanceInstance != null) {
            for (EntityAttributeModifier modifier : chanceInstance.getModifiers()) {
                chance += modifier.getValue();
            }
        }

        boolean isCrit = chance >= 1 || player.getWorld().random.nextDouble() < chance;
        if (!isCrit) {
            return f;
        }

        double damageMultiplier = EquipmentStandard.CONFIG.baseCritDamageMultiplier;
        // 暴击伤害倍率
        EntityAttributeInstance damageInstance = player.getAttributeInstance(CustomEntityAttributes.CRIT_DAMAGE);
        if (damageInstance != null) {
            for (EntityAttributeModifier modifier : damageInstance.getModifiers()) {
                damageMultiplier += modifier.getValue();
            }
        }
        damageMultiplier = Math.max(damageMultiplier, 1.1);
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
        player.addCritParticles(target);
        return (float) (f * damageMultiplier);
    }
}