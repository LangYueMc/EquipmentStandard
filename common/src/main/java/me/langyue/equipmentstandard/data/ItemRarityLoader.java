package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ItemRarityManager;
import me.langyue.equipmentstandard.api.data.ItemRarity;
import me.langyue.equipmentstandard.api.data.ItemVerifier;
import me.langyue.equipmentstandard.gson.ItemVerifierDeserializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class ItemRarityLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ItemVerifier.class, new ItemVerifierDeserializer())
            .create();

    public ItemRarityLoader() {
        super(GSON, "rarity");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller filler) {
        ItemRarityManager.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : prepared.entrySet()) {
            ItemRarity itemRarity = GSON.fromJson(entry.getValue(), ItemRarity.class);
            ItemRarityManager.put(entry.getKey(), itemRarity);
        }
        EquipmentStandard.LOGGER.info("Loaded {} item rarity", ItemRarityManager.size());
    }
}
