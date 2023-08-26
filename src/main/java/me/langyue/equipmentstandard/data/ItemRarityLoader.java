package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ItemRarityManager;
import me.langyue.equipmentstandard.gson.ItemVerifierDeserializer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class ItemRarityLoader extends BaseDataLoader {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ItemVerifier.class, new ItemVerifierDeserializer())
            .create();

    public ItemRarityLoader() {
        super(GSON, "rarity");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ItemRarityManager.clear();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            ItemRarity itemRarity = GSON.fromJson(entry.getValue(), ItemRarity.class);
            ItemRarityManager.put(entry.getKey(), itemRarity);
        }
        EquipmentStandard.LOGGER.info("Loaded {} item rarity", ItemRarityManager.size());
    }

    @Override
    public void reload(ResourceManager manager) {
    }
}
