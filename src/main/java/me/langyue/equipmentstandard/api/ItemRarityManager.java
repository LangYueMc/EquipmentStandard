package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.data.ItemRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

public class ItemRarityManager {

    private static final Map<Identifier, ItemRarity> ITEM_RARITY_MAP = new HashMap<>();
    private static final Set<ItemRarity.Rarity> DEFAULT_RARITY = new LinkedHashSet<>(){{
        add(new ItemRarity.Rarity(0, Text.empty(), Formatting.WHITE));
        add(new ItemRarity.Rarity(1000, Text.empty(), Formatting.GREEN));
        add(new ItemRarity.Rarity(2000, Text.empty(), Formatting.BLUE));
        add(new ItemRarity.Rarity(3000, Text.empty(), Formatting.LIGHT_PURPLE));
        add(new ItemRarity.Rarity(4000, Text.empty(), Formatting.RED));
        add(new ItemRarity.Rarity(5000, Text.empty(), Formatting.GOLD));
    }};

    public static void clear() {
        ITEM_RARITY_MAP.clear();
    }

    public static void put(Identifier id, ItemRarity itemRarity) {
        ITEM_RARITY_MAP.put(id, itemRarity);
    }

    public static int size() {
        return ITEM_RARITY_MAP.size();
    }

    public static ItemRarity.Rarity get(ItemStack itemStack) {
        return ITEM_RARITY_MAP.values().stream()
                .filter(it -> it.isValid(itemStack))
                .findFirst()
                .orElse(new ItemRarity(null, null, DEFAULT_RARITY.stream().toList()))
                .getRarity(itemStack.getScore());
    }
}
