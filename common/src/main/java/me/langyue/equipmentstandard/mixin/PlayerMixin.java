package me.langyue.equipmentstandard.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.langyue.equipmentstandard.MixinUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow public abstract boolean isLocalPlayer();

    /**
     * 暴击
     */
    @ModifyVariable(method = "attack", at = @At(value = "STORE"), index = 8)
    private boolean critAttack(boolean b, @Share("arg") LocalRef<Boolean> crit) {
        if (this.isLocalPlayer()) return b;
        if (crit.get() == null) {
            crit.set(MixinUtils.isCrit((Player) (Object) this));
        }
        return crit.get();
    }

    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5F))
    private float getCritDamageMultiplier(float constant) {
        return MixinUtils.getCritDamageMultiplier((Player) (Object) this, constant);
    }

    /**
     * 真伤
     */
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void realDamageAttack(Entity target, CallbackInfo ci) {
        MixinUtils.realDamageMixin((Player) (Object) this, target);
    }
}