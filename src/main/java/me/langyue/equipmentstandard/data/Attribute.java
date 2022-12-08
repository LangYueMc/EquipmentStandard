package me.langyue.equipmentstandard.data;

import com.google.common.util.concurrent.AtomicDouble;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.CustomEntityAttributes;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.entity.EquipmentSlot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attribute {
    private final String type;
    private final BigDecimal chance;
    private final Bonus bonus;
    private final List<AttributeModifier> modifiers;
    private final List<EquipmentSlot> slots;

    public Attribute(String type, BigDecimal chance, Bonus bonus, List<AttributeModifier> modifiers, List<EquipmentSlot> slots) {
        this.type = type;
        this.chance = chance;
        this.bonus = bonus;
        this.modifiers = modifiers;
        this.slots = slots;
    }

    public String getType() {
        return type;
    }

    public double getChance() {
        if (chance == null) {
            return 0;
        }
        return chance.setScale(2, RoundingMode.CEILING).doubleValue();
    }

    public Bonus getBonus() {
        return bonus;
    }

    public AttributeModifier getNextModifier() {
        return getNextModifier(0, 0);
    }

    public AttributeModifier getNextModifier(int proficiency, double luck) {
        if (modifiers.size() == 1) {
            return modifiers.get(0);
        }

        Map<AttributeModifier, Double> weightsMap = new HashMap<>();
        AtomicDouble totalWeight = new AtomicDouble();
        modifiers.forEach(modifier -> {
            double weights = modifier.getWeights() * ModifierUtils.getBonus(modifier.getBonus(), proficiency, luck);
            if (weights > 0) {
                weightsMap.put(modifier, weights);
                totalWeight.addAndGet(weights);
            }
        });
        double random = EquipmentStandard.RANDOM.nextDouble() * totalWeight.get();
        double temp = 0;
        for (Map.Entry<AttributeModifier, Double> entry : weightsMap.entrySet()) {
            temp += entry.getValue();
            if (temp > random) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<EquipmentSlot> getSlots() {
        // 耐久不分格子
        return CustomEntityAttributes.DURABLE.equals(type) ? null : slots;
    }
}
