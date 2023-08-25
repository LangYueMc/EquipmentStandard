package me.langyue.equipmentstandard.api;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.api.data.AttributeScore;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.Collection;
import java.util.Set;

public class AttributeScoreManager {
    private static final Multimap<EntityAttribute, AttributeScore.Score> ATTRIBUTE_SCORES = LinkedListMultimap.create();

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
        return ATTRIBUTE_SCORES.get(attribute);
    }


    public static Set<EntityAttribute> getAttributes() {
        return ATTRIBUTE_SCORES.keySet();
    }

    public static double get(EntityAttribute attribute, EntityAttributeModifier.Operation operation) {
        return get(attribute).stream()
                .filter(it -> it.getOperation() == operation)
                .mapToDouble(AttributeScore.Score::getScore)
                .findFirst().orElse(0);
    }
}
