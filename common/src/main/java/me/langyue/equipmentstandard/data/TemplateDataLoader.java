package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.EquipmentTemplateManager;
import me.langyue.equipmentstandard.api.data.EquipmentTemplate;
import me.langyue.equipmentstandard.api.data.ItemVerifier;
import me.langyue.equipmentstandard.gson.ItemVerifierDeserializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class TemplateDataLoader extends BaseDataLoader<EquipmentTemplate> {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ItemVerifier.class, new ItemVerifierDeserializer())
            .create();

    public TemplateDataLoader() {
        super(GSON, "template");
    }

    @Override
    protected void apply(Map<ResourceLocation, EquipmentTemplate> prepared, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        EquipmentTemplateManager.clear();
        prepared.forEach(EquipmentTemplateManager::put);
        EquipmentStandard.LOGGER.info("Loaded {} equipment templates", EquipmentTemplateManager.size());
    }
}
