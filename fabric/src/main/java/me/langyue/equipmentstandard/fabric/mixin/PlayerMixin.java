package me.langyue.equipmentstandard.fabric.mixin;

import me.langyue.equipmentstandard.MixinUtils;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    /**
     * 暴伤
     */
    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5F))
    private float getCritDamageMultiplier(float constant) {
        return MixinUtils.getCritDamageMultiplier((Player) (Object) this, constant);
    }

    /**
     * 挖掘速度
     * 原作者 @Draylar
     */
    @ModifyVariable(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;hasDigSpeed(Lnet/minecraft/world/entity/LivingEntity;)Z"), index = 2)
    private float getDestroySpeedMixin(float f) {
        return MixinUtils.getDestroySpeedMixin((Player) (Object) this, f);
    }
}