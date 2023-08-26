package me.langyue.equipmentstandard.api;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.api.data.AttributeScore;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.*;

public class AttributeScoreManager {
    private static final Multimap<EntityAttribute, AttributeScore.Score> ATTRIBUTE_SCORES = LinkedListMultimap.create();

    public static void clear() {
        ATTRIBUTE_SCORES.clear();
    }

    private static final Map<EntityAttributeModifier.Operation, AttributeScore.Score> _DEFAULT_SCORE = new LinkedHashMap<>() {{
        put(EntityAttributeModifier.Operation.ADDITION, new AttributeScore.Score(EntityAttributeModifier.Operation.ADDITION, 300));
        put(EntityAttributeModifier.Operation.MULTIPLY_BASE, new AttributeScore.Score(EntityAttributeModifier.Operation.MULTIPLY_BASE, 2500));
        put(EntityAttributeModifier.Operation.MULTIPLY_TOTAL, new AttributeScore.Score(EntityAttributeModifier.Operation.MULTIPLY_TOTAL, 3000));
    }};

    public static void put(EntityAttribute attribute, AttributeScore.Score score, boolean overWrite) {
        if (attribute == null || score == null) return;
        if (overWrite) {
            ATTRIBUTE_SCORES.remove(attribute, score);
        } else if (ATTRIBUTE_SCORES.containsEntry(attribute, score)) {
            return;
        }
        ATTRIBUTE_SCORES.put(attribute, score);
    }


    public static Collection<AttributeScore.Score> get(EntityAttribute attribute) {
        if (ATTRIBUTE_SCORES.containsKey(attribute))
            return ATTRIBUTE_SCORES.get(attribute);
        else
            return _DEFAULT_SCORE.values();
    }


    public static Set<EntityAttribute> getAttributes() {
        return ATTRIBUTE_SCORES.keySet();
    }

    public static double get(EntityAttribute attribute, EntityAttributeModifier.Operation operation) {
        return get(attribute).stream()
                .filter(it -> it.getOperation() == operation)
                .findFirst()
                .orElse(_DEFAULT_SCORE.getOrDefault(operation, new AttributeScore.Score(null, 100)))
                .getScore();
    }
}
