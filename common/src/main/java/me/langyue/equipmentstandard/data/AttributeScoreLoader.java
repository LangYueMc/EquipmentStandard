package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.AttributeScoreManager;
import me.langyue.equipmentstandard.api.data.AttributeScore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class AttributeScoreLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public AttributeScoreLoader() {
        super(GSON, "attr_score");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller filler) {
        AttributeScoreManager.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : prepared.entrySet()) {
            var attributeScore = GSON.fromJson(entry.getValue(), AttributeScore.class);
            attributeScore.register();
        }
        EquipmentStandard.LOGGER.info("Loaded {} attribute scores from {} files", AttributeScoreManager.getAttributes().size(), prepared.size());
    }
}
