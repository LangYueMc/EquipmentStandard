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
        add(new ItemRarity.Rarity("Defective", Integer.MIN_VALUE, Text.translatable("item.es.rarity.defective"), Formatting.GRAY));
        add(new ItemRarity.Rarity("Common", -20, Text.empty(), Formatting.WHITE));
        add(new ItemRarity.Rarity("Uncommon", 50, Text.empty(), Formatting.GREEN));
        add(new ItemRarity.Rarity("Rare", 350, Text.empty(), Formatting.BLUE));
        add(new ItemRarity.Rarity("Epic", 600, Text.empty(), Formatting.LIGHT_PURPLE));
        add(new ItemRarity.Rarity("Legendary", 1000, Text.translatable("item.es.rarity.legendary"), Formatting.GOLD));
        add(new ItemRarity.Rarity("Unique", 1500, Text.translatable("item.es.rarity.unique"), Formatting.RED));
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
        return get(itemStack, itemStack.getScore());
    }

    public static ItemRarity.Rarity get(ItemStack itemStack, Integer score) {
        return ITEM_RARITY_MAP.values().stream()
                .filter(it -> it.isValid(itemStack))
                .findFirst()
                .orElse(new ItemRarity(null, null, DEFAULT_RARITY.stream().toList()))
                .getRarity(score);
    }
}
