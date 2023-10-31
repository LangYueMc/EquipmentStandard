package me.langyue.equipmentstandard.api;

import dev.architectury.registry.registries.DeferredRegister;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class CustomAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTE = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.ATTRIBUTE);
    public static final String DURABLE = "durable";
    public static final Attribute DIG_SPEED = register("generic.dig_speed");
    public static final Attribute CRIT_CHANCE = register("generic.crit_chance", 0.01, 0D, 1D);
    public static final Attribute CRIT_DAMAGE = register("generic.crit_damage", 1.5, 1D, 2048D);
    public static final Attribute REAL_DAMAGE = register("generic.real_damage");

    private static Attribute register(String id) {
        return register(id, 0D, 0D, 2048D);
    }

    private static Attribute register(String id, double fallback, double min, double max) {
        Attribute attribute = new RangedAttribute("attribute.name." + id, fallback, min, max).setSyncable(true);
        ATTRIBUTE.register(EquipmentStandard.createResourceLocation(id), () -> attribute);
        return attribute;
    }
}
