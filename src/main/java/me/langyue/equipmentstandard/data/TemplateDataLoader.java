package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.EquipmentTemplateManager;
import me.langyue.equipmentstandard.gson.ItemVerifierDeserializer;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class TemplateDataLoader extends JsonDataLoader implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ItemVerifier.class, new ItemVerifierDeserializer())
//            .registerTypeAdapter(AttributeModifier.class, new AttributeModifierDeserializer())
            .create();

    public TemplateDataLoader() {
        super(GSON, "template");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier("equipment_standard", "attr");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
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
