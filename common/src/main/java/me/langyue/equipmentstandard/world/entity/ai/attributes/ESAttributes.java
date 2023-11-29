package me.langyue.equipmentstandard.world.entity.ai.attributes;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.*;

public class ESAttributes {
    private static final DeferredRegister<Attribute> ATTRIBUTE = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.ATTRIBUTE);
    private static final Set<Attribute> ALL = new HashSet<>();
    private static final Map<String, String> REPEAT = new HashMap<>();
    public static final String DURABLE = "durable";
    public static final Attribute DIG_SPEED = register("generic.dig_speed", "additionalentityattributes:dig_speed");
    public static final Attribute CRIT_CHANCE = register("generic.crit_chance", 0.01, 0D, 1D);
    public static final Attribute CRIT_DAMAGE = register("generic.crit_damage", "additionalentityattributes:critical_bonus_damage", 1.5, 1D, 2048D);
    public static final Attribute REAL_DAMAGE = register("generic.real_damage");

    private static Attribute register(String id) {
        return register(id, 0D, 0D, 2048D);
    }

    private static Attribute register(String id, String repeat) {
        return register(id, repeat, 0D, 0D, 2048D);
    }

    private static Attribute register(String id, double fallback, double min, double max) {
        return register(id, null, fallback, min, max);
    }

    private static Attribute register(String id, String repeat, double fallback, double min, double max) {
        try {
            if (repeat != null && Platform.isModLoaded(repeat.split(":")[0])) {
                REPEAT.put(EquipmentStandard.MOD_ID + ":" + id, repeat);
//                return null;
            }
        } catch (Throwable e) {
            // net.minecraftforge.fml.ModList.get() == null 时
            // 但为什么会为 null 呢？
            EquipmentStandard.LOGGER.warn("Attribute compatibility setting failed", e);
            REPEAT.put(EquipmentStandard.MOD_ID + ":" + id, repeat);
        }
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

    public static Attribute getAttribute(String id) {
        var i = id;
        if (REPEAT.containsKey(id)) {
            i = REPEAT.get(id);
        }
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(i));
        if (attribute == null) {
            attribute = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(id));
        }
        return attribute;
    }
}
