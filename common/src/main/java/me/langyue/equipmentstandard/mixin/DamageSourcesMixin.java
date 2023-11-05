package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.DamageSourcesAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DamageSources.class)
public abstract class DamageSourcesMixin implements DamageSourcesAccessor {

    @Shadow
    protected abstract DamageSource source(ResourceKey<DamageType> arg, Entity arg2, Entity arg3);

    @Override
    public DamageSource es$createDamageSource(ResourceKey<DamageType> resourceKey, @Nullable Entity entity, @Nullable Entity entity2) {
        return this.source(resourceKey, entity, entity2);
    }
}
