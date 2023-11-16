package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.data.EquipmentTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EquipmentTemplateManager {

    private static final Map<ResourceLocation, EquipmentTemplate> TEMPLATES = new HashMap<>();

    public static void clear() {
        TEMPLATES.clear();
    }

    public static void put(ResourceLocation id, EquipmentTemplate template) {
        if (template.init()) {
            TEMPLATES.put(id, template);
        }
    }

    public static int size() {
        return TEMPLATES.size();
    }

    public static EquipmentTemplate getRandom(ItemStack itemStack) {
        return TEMPLATES.values().stream().filter(it -> it.isValid(itemStack))
                .min(Comparator.comparing(it -> EquipmentStandard.RANDOM.nextInt()))
                .orElse(null);
    }

    public static List<EquipmentTemplate> get(ItemStack itemStack) {
        return TEMPLATES.values().stream().filter(it -> it.isValid(itemStack)).collect(Collectors.toList());
    }
}
