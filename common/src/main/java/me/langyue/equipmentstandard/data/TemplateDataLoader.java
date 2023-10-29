package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.EquipmentTemplateManager;
import me.langyue.equipmentstandard.api.data.EquipmentTemplate;
import me.langyue.equipmentstandard.api.data.ItemVerifier;
import me.langyue.equipmentstandard.gson.ItemVerifierDeserializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class TemplateDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ItemVerifier.class, new ItemVerifierDeserializer())
            .create();

    public TemplateDataLoader() {
        super(GSON, "template");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller filler) {
        EquipmentTemplateManager.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : prepared.entrySet()) {
            EquipmentTemplate template = GSON.fromJson(entry.getValue(), EquipmentTemplate.class);
            EquipmentTemplateManager.put(entry.getKey(), template);
        }
        EquipmentStandard.LOGGER.info("Loaded {} equipment templates", EquipmentTemplateManager.size());
    }
}
