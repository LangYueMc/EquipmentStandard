package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.api.data.ItemRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ItemRarityManager {

    private static final Map<ResourceLocation, ItemRarity> ITEM_RARITY_MAP = new HashMap<>();
    private static final Set<ItemRarity.Rarity> DEFAULT_RARITY = new LinkedHashSet<>() {{
        add(new ItemRarity.Rarity("Defective", Integer.MIN_VALUE, Component.translatable("item.es.rarity.defective"), ChatFormatting.GRAY));
        add(new ItemRarity.Rarity("Common", -20, Component.empty(), ChatFormatting.WHITE));
        add(new ItemRarity.Rarity("Uncommon", 50, Component.translatable("item.es.rarity.uncommon"), ChatFormatting.GREEN));
        add(new ItemRarity.Rarity("Rare", 350, Component.translatable("item.es.rarity.rare"), ChatFormatting.BLUE));
        add(new ItemRarity.Rarity("Epic", 600, Component.translatable("item.es.rarity.epic"), ChatFormatting.LIGHT_PURPLE));
        add(new ItemRarity.Rarity("Legendary", 1000, Component.translatable("item.es.rarity.legendary"), ChatFormatting.GOLD));
        add(new ItemRarity.Rarity("Unique", 1500, Component.translatable("item.es.rarity.unique"), ChatFormatting.RED));
    }};

    public static void clear() {
        ITEM_RARITY_MAP.clear();
    }

    public static void put(ResourceLocation id, ItemRarity itemRarity) {
        ITEM_RARITY_MAP.put(id, itemRarity);
    }

    public static int size() {
        return ITEM_RARITY_MAP.size();
    }

    public static ItemRarity.Rarity get(ItemStack itemStack) {
        return get(itemStack, ((EquipmentComponentsAccessor) (Object) itemStack).es$getScore());
    }

    public static ItemRarity.Rarity get(ItemStack itemStack, Integer score) {
        return ITEM_RARITY_MAP.values().stream()
                .filter(it -> it.isValid(itemStack))
                .findFirst()
                .orElse(new ItemRarity(null, null, DEFAULT_RARITY.stream().toList()))
                .getRarity(score);
    }
}
