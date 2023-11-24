package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.MixinUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Unique
    protected Float es$critDamageMultiplier;

    @Inject(method = "setOwner", at = @At("HEAD"))
    private void getOwner(Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide() && MixinUtils.isCrit(livingEntity)) {
            es$critDamageMultiplier = MixinUtils.getCritDamageMultiplier(livingEntity, 1.5f);
        }
    }

    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    private float crit(float f) {
        return getCritDamage(f);
    }

    @Unique
    protected float getCritDamage(float f) {
        if (es$critDamageMultiplier != null) {
            return es$critDamageMultiplier * f;
        }
        return f;
    }
}
