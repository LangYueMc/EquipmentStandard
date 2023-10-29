package me.langyue.equipmentstandard.forge.mixin;

import me.langyue.equipmentstandard.api.CustomAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Unique
    private final Player es$player = (Player) (Object) this;

    /**
     * 挖掘速度
     * 原作者 @Draylar
     */
    @ModifyVariable(method = "getDigSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;hasDigSpeed(Lnet/minecraft/world/entity/LivingEntity;)Z"), index = 2)
    private float getDestroySpeedMixin(float f) {
        var instance = es$player.getAttribute(CustomAttributes.DIG_SPEED);
        double speed = f;
        if (instance != null) {
            for (var modifier : instance.getModifiers(AttributeModifier.Operation.ADDITION)) {
                speed += modifier.getAmount();
            }
            for (var modifier : instance.getModifiers(AttributeModifier.Operation.MULTIPLY_BASE)) {
                speed *= (1 + modifier.getAmount());
            }
            for (var modifier : instance.getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
                speed *= (1 + modifier.getAmount());
            }
        }

        return (float) speed;
    }
}