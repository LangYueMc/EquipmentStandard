package me.langyue.equipmentstandard.api.data;

import me.langyue.equipmentstandard.api.AttributeScoreManager;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Set;

public class AttributeScore {
    private final String type;
    private final Set<Score> scores;
    private final boolean overWrite;

    public AttributeScore(String type, Set<Score> scores, boolean overWrite) {
        this.type = type;
        this.scores = scores;
        this.overWrite = overWrite;
    }

    public void register() {
        EntityAttribute entityAttribute = Registries.ATTRIBUTE.get(new Identifier(type));
        if (entityAttribute == null) return;
        for (var operation : EntityAttributeModifier.Operation.values()) {
            AttributeScoreManager.put(entityAttribute, getScore(operation), overWrite);
        }
    }

    public String getType() {
        return type;
    }

    public Score getScore(EntityAttributeModifier.Operation operation) {
        if (scores == null || operation == null) return null;
        return scores.stream()
                .filter(it -> it.operation == operation)
                .findFirst().orElse(null);
    }

    public static class Score {
        private final EntityAttributeModifier.Operation operation;
        private final double score;

        public Score(EntityAttributeModifier.Operation operation, float score) {
            this.operation = operation;
            this.score = score;
        }

        public EntityAttributeModifier.Operation getOperation() {
            return operation;
        }

        public double getScore() {
            return score;
        }

        @Override
        public int hashCode() {
            return operation.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Score s)) return false;
            return s.operation == operation;
        }
    }
}
