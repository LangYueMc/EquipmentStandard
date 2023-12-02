package me.langyue.equipmentstandard.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.langyue.equipmentstandard.MixinUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    /**
     * 关闭原版暴击
     */
    @ModifyVariable(method = "attack", at = @At(value = "STORE"), ordinal = 2)
    private boolean disableCrit(boolean b) {
        return false;
    }

    /**
     * 自定义暴击
     */
    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I"), ordinal = 0)
    private float customCrit(float f, @Share("crit") LocalBooleanRef crit) {
        if (MixinUtils.isCrit((Player) (Object) this)) {
            crit.set(true);
            return f * MixinUtils.getCritDamageMultiplier((Player) (Object) this);
        }
        return f;
    }

    /**
     * 应用伤害前修改暴击，主要是为了效果
     */
    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), ordinal = 2)
    private boolean setCrit(boolean b, @Share("crit") LocalBooleanRef crit) {
        return crit.get();
    }

    /**
     * 真伤
     */
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER, ordinal = 0))
    private void realDamageAttack(Entity target, CallbackInfo ci) {
        MixinUtils.realDamageMixin((Player) (Object) this, target);
    }
}