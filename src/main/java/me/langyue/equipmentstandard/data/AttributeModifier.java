package me.langyue.equipmentstandard.data;

import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AttributeModifier {
    /**
     * 权重，默认: 1
     */
    private final Integer weights;
    /**
     * 加成
     */
    private final Bonus bonus;
    /**
     * EntityAttributeModifier.Operation
     */
    private final String operation;
    /**
     * 保留小数位， 默认 2
     */
    private final Integer scale;
    /**
     * 固定值，与 min 和 max 互斥，若同时有值，以此为准
     */
    private final BigDecimal amount;
    /**
     * 范围最小值，与 amount 互斥
     */
    private final BigDecimal min;
    /**
     * 范围最大值，与 amount 互斥
     */
    private final BigDecimal max;

    public AttributeModifier(Integer weights, Bonus bonus, String operation, Integer scale, BigDecimal amount, BigDecimal min, BigDecimal max) {
        this.weights = weights;
        this.bonus = bonus;
        this.operation = operation;
        this.scale = scale;
        this.amount = amount;
        this.min = min;
        this.max = max;
    }

    public Integer getWeights() {
        return weights == null ? 1 : Math.max(weights, 0);
    }

    public Bonus getBonus() {
        return bonus;
    }

    public double getAmount() {
        if (amount == null) {
            double temp;
            if (min == null || max == null) {
                temp = min != null ? min.doubleValue() : max != null ? max.doubleValue() : 0;
            } else {
                double random = EquipmentStandard.RANDOM.nextDouble();

                temp = random * (max.doubleValue() - min.doubleValue()) + min.doubleValue();
            }
            return BigDecimal.valueOf(temp)
                    .setScale(scale == null ? 2 : scale, RoundingMode.CEILING)
                    .doubleValue();
        }
        return amount.doubleValue();
    }

    public EntityAttributeModifier.Operation getOperation() {
        return EntityAttributeModifier.Operation.valueOf(this.operation.toUpperCase());
    }
}
