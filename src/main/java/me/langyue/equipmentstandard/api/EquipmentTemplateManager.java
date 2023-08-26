package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.data.Attribute;
import me.langyue.equipmentstandard.data.EquipmentTemplate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EquipmentTemplateManager {

    private static final Map<Identifier, EquipmentTemplate> TEMPLATES = new HashMap<>();

    public static void clear() {
        TEMPLATES.clear();
    }

    public static void put(Identifier id, EquipmentTemplate template) {
        template.init();
        TEMPLATES.put(id, template);
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
