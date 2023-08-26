package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.EquipmentTemplateManager;
import me.langyue.equipmentstandard.gson.ItemVerifierDeserializer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class TemplateDataLoader extends BaseDataLoader {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ItemVerifier.class, new ItemVerifierDeserializer())
            .create();

    public TemplateDataLoader() {
        super(GSON, "template");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        EquipmentTemplateManager.clear();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            EquipmentTemplate template = GSON.fromJson(entry.getValue(), EquipmentTemplate.class);
            EquipmentTemplateManager.put(entry.getKey(), template);
        }
        EquipmentStandard.LOGGER.info("Loaded {} equipment templates", EquipmentTemplateManager.size());
    }

    @Override
    public void reload(ResourceManager manager) {
    }
}
