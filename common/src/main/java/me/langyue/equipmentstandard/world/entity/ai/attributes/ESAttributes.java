package me.langyue.equipmentstandard.world.entity.ai.attributes;

import dev.architectury.registry.registries.DeferredRegister;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ESAttributes {
    private static final DeferredRegister<Attribute> ATTRIBUTE = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.ATTRIBUTE);
    private static final Set<Attribute> ALL = new HashSet<>();
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
        ALL.add(attribute);
        return attribute;
    }

    public static void register() {
        ATTRIBUTE.register();
    }

    public static void createLivingAttributes(AttributeSupplier.Builder builder) {
        ALL.stream().filter(Objects::nonNull).forEach(builder::add);
    }
}
