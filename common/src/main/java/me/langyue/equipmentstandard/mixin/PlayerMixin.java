package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.MixinUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Unique
    private final Player es$player = (Player) (Object) this;

    @Unique
    private Entity es$target;

    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void attackMixin(Entity entity, CallbackInfo ci) {
        this.es$target = entity;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", ordinal = 1), require = 0)
    private boolean attackMixin1(Player instance) {
        // 关闭原版跳击暴击
        return true;
    }

    /**
     * 暴击
     */
    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I", shift = At.Shift.AFTER), ordinal = 0)
    private float critAttack(float f) {
        return MixinUtils.critAttackMixin(es$player, es$target, f);
    }

    /**
     * 真伤
     */
    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    private float realDamageAttack(float f) {
        return MixinUtils.realDamageMixin(es$player, es$target, f);
    }
}