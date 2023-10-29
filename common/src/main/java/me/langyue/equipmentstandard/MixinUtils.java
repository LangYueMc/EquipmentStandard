package me.langyue.equipmentstandard;

import me.langyue.equipmentstandard.api.CustomAttributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

/**
 * 提取重复 Mixin 代码，便于直接调用
 */
public class MixinUtils {

    /**
     * 挖掘速度
     */
    public static float getDestroySpeedMixin(Player player, float f) {
        var attribute = player.getAttribute(CustomAttributes.DIG_SPEED);
        double speed = f;
        if (attribute != null) {
            for (var modifier : attribute.getModifiers(AttributeModifier.Operation.ADDITION)) {
                speed += modifier.getAmount();
            }
            for (var modifier : attribute.getModifiers(AttributeModifier.Operation.MULTIPLY_BASE)) {
                speed *= (1 + modifier.getAmount());
            }
            for (var modifier : attribute.getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
                speed *= (1 + modifier.getAmount());
            }
        }

        return (float) speed;
    }

    /**
     * 暴击
     */
    public static float critAttackMixin(LivingEntity entity, Entity target, float f) {
        if (entity.level().isClientSide()) return f;
        // 暴击几率
        double chance = entity.fallDistance > 0.0F
                && !entity.onGround()
                && !entity.onClimbable()
                && !entity.isInWater()
                && !entity.hasEffect(MobEffects.BLINDNESS)
                && !entity.isPassenger()
                && target instanceof LivingEntity
                ? EquipmentStandard.CONFIG.jumpAttackCritChance : EquipmentStandard.CONFIG.baseCritChance;
        if (chance < 1) {
            var chanceInstance = entity.getAttribute(CustomAttributes.CRIT_CHANCE);
            if (chanceInstance != null) {
                for (AttributeModifier modifier : chanceInstance.getModifiers()) {
                    chance += modifier.getAmount();
                }
            }
            chance = Math.max(EquipmentStandard.CONFIG.baseCritChance, chance);
        }
        boolean isCrit = false;
        try {
            isCrit = chance >= 1 || EquipmentStandard.RANDOM.nextDouble() < chance;
        } catch (Exception e) {
            EquipmentStandard.debug(e.getMessage());
        }
        if (!isCrit) {
            return f;
        }

        double damageMultiplier = EquipmentStandard.CONFIG.baseCritDamageMultiplier;
        // 暴击伤害倍率
        var damageInstance = entity.getAttribute(CustomAttributes.CRIT_DAMAGE);
        if (damageInstance != null) {
            for (AttributeModifier modifier : damageInstance.getModifiers()) {
                damageMultiplier += modifier.getAmount();
            }
        }
        damageMultiplier = Math.max(damageMultiplier, 1.1);
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, entity.getSoundSource(), 1.0F, 1.0F);
        if (entity instanceof Player player) {
            player.crit(target);
        }
        return (float) (f * damageMultiplier);
    }
}
