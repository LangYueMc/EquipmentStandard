package me.langyue.equipmentstandard.data;

import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Comparator;
import java.util.List;

public class ItemRarity {
    private final List<ItemVerifier> verifiers;
    private final List<ItemVerifier> exclude;
    private final List<Rarity> rarities;

    public ItemRarity(List<ItemVerifier> verifiers, List<ItemVerifier> exclude, List<Rarity> rarities) {
        this.verifiers = verifiers;
        this.exclude = exclude;
        this.rarities = rarities;
    }

    public boolean isValid(ItemStack itemStack) {
        if (exclude != null &&exclude.stream().anyMatch(it -> it.isValid(itemStack))) {
            return false;
        }
        return verifiers.stream().anyMatch(it -> it.isValid(itemStack));
    }

    public Rarity getRarity(Integer score) {
        if (score == null || score == 0d) return null;
        return rarities.stream()
                .sorted(Comparator.comparing(Rarity::getScore).reversed())
                .filter(rarity -> rarity.score <= score)
                .findFirst().orElse(null);
    }

    public static class Rarity {
        private final String name;
        private final Integer score;
        private MutableText prefix;
        private final Formatting[] formattings;

        public Rarity(String name, Integer score, MutableText prefix, Formatting... formattings) {
            this.name = name;
            this.score = score;
            this.prefix = prefix;
            this.formattings = formattings;
        }

        public String getName() {
            return name;
        }

        public double getScore() {
            return score;
        }

        public MutableText getPrefix() {
            if (prefix == null) {
                prefix = Text.empty();
            }
            if (formattings != null && formattings.length > 0) {
                prefix.formatted(formattings);
            }
            return prefix;
        }

        public Formatting[] getFormattings() {
            return formattings;
        }
    }
}
