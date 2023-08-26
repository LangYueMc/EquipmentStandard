package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.AttributeScoreManager;
import me.langyue.equipmentstandard.api.EquipmentTemplateManager;
import me.langyue.equipmentstandard.api.data.AttributeScore;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeScoreLoader extends BaseDataLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public AttributeScoreLoader() {
        super(GSON, "attr_score");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        AttributeScoreManager.clear();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            var attributeScore = GSON.fromJson(entry.getValue(), AttributeScore.class);
            attributeScore.register();
        }
        EquipmentStandard.LOGGER.info("Loaded {} attribute scores from {} files", AttributeScoreManager.getAttributes().size(), prepared.size());
    }

    @Override
    public void reload(ResourceManager manager) {
    }
}
