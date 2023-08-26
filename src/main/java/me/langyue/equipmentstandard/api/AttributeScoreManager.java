package me.langyue.equipmentstandard.api;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.api.data.Attribute;
import me.langyue.equipmentstandard.api.data.AttributeScore;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AttributeScoreManager {
    private static final Multimap<EntityAttribute, AttributeScore.Score> ATTRIBUTE_SCORES = LinkedListMultimap.create();

    public static void clear() {
        ATTRIBUTE_SCORES.clear();
    }

    private static final Map<Attribute.Operation, AttributeScore.Score> _DEFAULT_SCORE = new LinkedHashMap<>() {{
        put(Attribute.Operation.ADDITION, new AttributeScore.Score(Attribute.Operation.ADDITION, 100));
        put(Attribute.Operation.ADDITION_PERCENTAGE, new AttributeScore.Score(Attribute.Operation.ADDITION_PERCENTAGE, 1));
        put(Attribute.Operation.MULTIPLY_BASE, new AttributeScore.Score(Attribute.Operation.MULTIPLY_BASE, 500));
        put(Attribute.Operation.MULTIPLY_TOTAL, new AttributeScore.Score(Attribute.Operation.MULTIPLY_TOTAL, 550));
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

    public static double get(EntityAttribute attribute, Attribute.Operation operation) {
        return get(attribute).stream()
                .filter(it -> it.getOperation() == operation)
                .findFirst()
                .orElse(_DEFAULT_SCORE.getOrDefault(operation, new AttributeScore.Score(null, 100)))
                .getScore();
    }

    public static double get(String attribute, Attribute.Operation operation) {
        return get(Registries.ATTRIBUTE.get(new Identifier(attribute)), operation);
    }
}
