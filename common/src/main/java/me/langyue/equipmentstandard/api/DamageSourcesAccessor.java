package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface DamageSourcesAccessor {
    ResourceKey<DamageType> REAL_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, EquipmentStandard.createResourceLocation("real_damage"));

    DamageSource es$createDamageSource(ResourceKey<DamageType> resourceKey, @Nullable Entity entity, @Nullable Entity entity2);

    default DamageSource realDamage(Entity entity) {
        return this.es$createDamageSource(REAL_DAMAGE_KEY, entity, null);
    }
}
