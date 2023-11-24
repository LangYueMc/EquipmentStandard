package me.langyue.equipmentstandard.mixin;

import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThrownTrident.class)
public class ThrownTridentMixin extends AbstractArrowMixin {
    @ModifyConstant(method = "onHitEntity", constant = @Constant(floatValue = 8.0F))
    protected float crit(float f) {
        return getCritDamage(f);
    }
}
