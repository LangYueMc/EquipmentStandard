package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.data.EquipmentTemplate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class EquipmentTemplateManager {

    private static final Map<Identifier, EquipmentTemplate> TEMPLATES = new HashMap<>();

    public static void put(Identifier id, EquipmentTemplate template) {
        TEMPLATES.put(id, template);
    }

    public static int size() {
        return TEMPLATES.size();
    }

    public static EquipmentTemplate get(ItemStack itemStack) {
        return TEMPLATES.values().stream().filter(it -> it.isValid(itemStack)).findFirst().orElse(null);
    }
}
