package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.CustomAttributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Unique
    private final Player es$player = (Player) (Object) this;

    private Entity target;


    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void attackMixin(Entity entity, CallbackInfo ci) {
        this.target = entity;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", ordinal = 1), require = 0)
    private boolean attackMixin1(Player instance) {
        // 关闭原版跳击暴击
        return false;
    }

    /**
     * 暴击
     */
    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", shift = At.Shift.AFTER), ordinal = 0, require = 0)
    private float critAttack(float f) {
        double chance = EquipmentStandard.CONFIG.baseCritChance;
        // 暴击几率
        var chanceInstance = es$player.getAttribute(CustomAttributes.CRIT_CHANCE);
        if (chanceInstance != null) {
            for (AttributeModifier modifier : chanceInstance.getModifiers()) {
                chance += modifier.getAmount();
            }
        }

        boolean isCrit = chance >= 1 || EquipmentStandard.RANDOM.nextDouble() < chance;
        if (!isCrit) {
            return f;
        }

        double damageMultiplier = EquipmentStandard.CONFIG.baseCritDamageMultiplier;
        // 暴击伤害倍率
        var damageInstance = es$player.getAttribute(CustomAttributes.CRIT_DAMAGE);
        if (damageInstance != null) {
            for (AttributeModifier modifier : damageInstance.getModifiers()) {
                damageMultiplier += modifier.getAmount();
            }
        }
        damageMultiplier = Math.max(damageMultiplier, 1.1);
        es$player.level().playSound(null, es$player.getX(), es$player.getY(), es$player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, es$player.getSoundSource(), 1.0F, 1.0F);
        es$player.crit(target);
        return (float) (f * damageMultiplier);
    }
}