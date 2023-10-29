package me.langyue.equipmentstandard.api.data;

import me.langyue.equipmentstandard.api.AttributeScoreManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

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
        var attribute = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(type));
        if (attribute == null) return;
        for (var operation : Attribute.Operation.values()) {
            AttributeScoreManager.put(attribute, getScore(operation), overWrite);
        }
    }

    public String getType() {
        return type;
    }

    public Score getScore(Attribute.Operation operation) {
        if (scores == null || operation == null) return null;
        return scores.stream()
                .filter(it -> it.operation == operation)
                .findFirst().orElse(null);
    }

    public static class Score {
        private final Attribute.Operation operation;
        private final double score;

        public Score(Attribute.Operation operation, float score) {
            this.operation = operation;
            this.score = score;
        }

        public Attribute.Operation getOperation() {
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
