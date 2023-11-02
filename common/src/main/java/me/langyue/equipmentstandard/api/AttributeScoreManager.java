package me.langyue.equipmentstandard.api;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.api.data.AttributeScore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AttributeScoreManager {
    private static final Multimap<Attribute, AttributeScore.Score> ATTRIBUTE_SCORES = LinkedListMultimap.create();

    public static void clear() {
        ATTRIBUTE_SCORES.clear();
    }

    private static final Map<AttributeModifier.Operation, AttributeScore.Score> _DEFAULT_SCORE = new LinkedHashMap<>() {{
        put(AttributeModifier.Operation.ADDITION, new AttributeScore.Score(AttributeModifier.Operation.ADDITION, 100));
//        put(Attribute.Operation.MULTIPLY_ADDITION, new AttributeScore.Score(Attribute.Operation.MULTIPLY_ADDITION, 3));
        put(AttributeModifier.Operation.MULTIPLY_BASE, new AttributeScore.Score(AttributeModifier.Operation.MULTIPLY_BASE, 500));
        put(AttributeModifier.Operation.MULTIPLY_TOTAL, new AttributeScore.Score(AttributeModifier.Operation.MULTIPLY_TOTAL, 550));
    }};

    public static void put(Attribute attribute, AttributeScore.Score score, boolean overWrite) {
        if (attribute == null || score == null) return;
        if (overWrite) {
            ATTRIBUTE_SCORES.remove(attribute, score);
        } else if (ATTRIBUTE_SCORES.containsEntry(attribute, score)) {
            return;
        }
        ATTRIBUTE_SCORES.put(attribute, score);
    }

    public static void setDefault(AttributeScore.Score score) {
        _DEFAULT_SCORE.put(score.getOperation(), score);
    }

    public static Collection<AttributeScore.Score> get(Attribute attribute) {
        if (ATTRIBUTE_SCORES.containsKey(attribute))
            return ATTRIBUTE_SCORES.get(attribute);
        else
            return _DEFAULT_SCORE.values();
    }


    public static Set<Attribute> getAttributes() {
        return ATTRIBUTE_SCORES.keySet();
    }

    public static double get(Attribute attribute, AttributeModifier.Operation operation) {
        return get(attribute).stream()
                .filter(it -> it.getOperation() == operation)
                .findFirst()
                .orElse(_DEFAULT_SCORE.getOrDefault(operation, new AttributeScore.Score(null, 100)))
                .getScore();
    }

    public static double get(String attribute, AttributeModifier.Operation operation) {
        return get(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(attribute)), operation);
    }
}
