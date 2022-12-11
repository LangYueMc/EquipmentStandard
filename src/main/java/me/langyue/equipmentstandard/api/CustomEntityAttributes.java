package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.registry.Registry;

public class CustomEntityAttributes {
    public static final String DURABLE = "durable";
    public static final EntityAttribute DIG_SPEED = register("generic.dig_speed");
    public static final EntityAttribute CRIT_CHANCE = register("generic.crit_chance", 0.01, 0D, 1D);
    public static final EntityAttribute CRIT_DAMAGE = register("generic.crit_damage", 1.5, 1D, 2048D);
    public static final EntityAttribute REAL_DAMAGE = register("generic.real_damage");

    private static EntityAttribute register(String id) {
        return register(id, 0D, 0D, 2048D);
    }

    private static EntityAttribute register(String id, double fallback, double min, double max) {
        return Registry.register(Registry.ATTRIBUTE, EquipmentStandard.createIdentifier(id), new ClampedEntityAttribute("attribute.name." + id, fallback, min, max).setTracked(true));
    }
}
