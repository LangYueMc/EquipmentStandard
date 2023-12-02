package me.langyue.equipmentstandard.api.data;

import com.google.common.util.concurrent.AtomicDouble;
import io.netty.util.internal.ThreadLocalRandom;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.world.entity.ai.attributes.ESAttributes;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Attribute {
    /**
     * 类型，可选值
     *
     * @see net.minecraft.world.entity.ai.attributes.Attribute
     * @see ESAttributes
     */
    private final String type;
    /**
     * 属性值步进，也可单独在 AttributeModifier 中设置
     *
     * @see AttributeModifier
     */
    private final BigDecimal step;
    private final boolean merge;
    /**
     * 命中这个属性的几率，最大 1
     */
    private final BigDecimal chance;
    private final Bonus bonus;
    private final List<AttributeModifier> modifiers;
    private Set<Slot> slots;

    public Attribute(String type, BigDecimal step, boolean merge, BigDecimal chance, Bonus bonus, List<AttributeModifier> modifiers, Set<Slot> slots) {
        this.type = type;
        this.step = step;
        this.merge = merge;
        this.chance = chance;
        this.bonus = bonus;
        this.modifiers = modifiers;
        this.slots = slots;
    }

    public String getType() {
        return type;
    }

    public void init(EquipmentTemplate template) {
        if (this.slots == null || this.slots.isEmpty()) {
            this.slots = template.getSlots();
        }
        modifiers.forEach(modifier -> {
            if (modifier.step == null) {
                modifier.setStep(step);
            }
        });
    }

    public boolean isMerge() {
        // 耐久默认就是修改原版的，所以耐久固定返回false，看不懂逻辑也别问问什么了，就是这样，千万不要改
        return merge && !type.equalsIgnoreCase(ESAttributes.DURABLE);
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

    public Set<Slot> getSlots() {
        // 耐久不分格子
        return ESAttributes.DURABLE.equals(type) ? null : slots;
    }

    public List<AttributeModifier> getModifiers() {
        return modifiers;
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
        double random = ThreadLocalRandom.current().nextDouble() * totalWeight.get();
        double temp = 0;
        for (Map.Entry<AttributeModifier, Double> entry : weightsMap.entrySet()) {
            temp += entry.getValue();
            if (temp > random) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Final getFinal() {
        return getFinal(0, 0);
    }

    public Final getFinal(int proficiency, double luck) {
        if (this.getChance() <= 0) {
            return null;
        }
        AttributeModifier modifier = this.getNextModifier(proficiency, luck);
        if (modifier == null) {
            return null;
        }
        double amount = modifier.getAmount();
        if (amount == 0) {
            // 如果值为 0 则不存储 NBT
            return null;
        }
        Operation operation = modifier.getOperation();
        try {
            return new Final(
                    this.getType(),
                    !this.getType().equalsIgnoreCase(ESAttributes.DURABLE) && this.isMerge(),
                    amount,
                    operation,
                    this.getSlots()
            );
        } catch (Throwable e) {
            EquipmentStandard.LOGGER.warn("Unable to create attribute: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 尝试从 @param from 合并到 @param nbt
     *
     * @return 是否合并成功
     */
    public static boolean mergeToNbt(Final from, CompoundTag nbt) {
        // 合并同类项
        Final to = Final.fromNbt(nbt);
        if (to == null) return false;
        if (!from.equalsIgnoreAmount(to)) return false;
        nbt.putDouble("Amount", to.amount + from.amount);
        return true;
    }

    public enum Slot {
        ANY(0, null),
        DEFAULT(1, null),
        MAINHAND(2, EquipmentSlot.MAINHAND),
        OFFHAND(3, EquipmentSlot.OFFHAND),
        FEET(4, EquipmentSlot.FEET),
        LEGS(5, EquipmentSlot.LEGS),
        CHEST(6, EquipmentSlot.CHEST),
        HEAD(7, EquipmentSlot.HEAD);

        private final int id;
        private final EquipmentSlot equipmentSlot;

        Slot(int id, EquipmentSlot slot) {
            this.id = id;
            this.equipmentSlot = slot;
        }

        public int getId() {
            return id;
        }

        public EquipmentSlot asEquipmentSlot() {
            return equipmentSlot;
        }

        public static Slot byId(int id) {
            for (Slot slot : values()) {
                if (slot.getId() == id) {
                    return slot;
                }
            }
            throw new IllegalArgumentException("Invalid slot " + id);
        }
    }

    public record Final(
            String type,
            boolean merge,
            double amount,
            Operation operation,
            Set<Slot> slots
    ) {
        public Final(String type, boolean merge, double amount, Operation operation, Set<Slot> slots) {
            if (StringUtils.isBlank(type)) throw new IllegalArgumentException("Attribute type can not be null.");
            if (amount == 0) throw new IllegalArgumentException("Attribute amount can not be 0.");
            this.type = type;
            this.merge = merge;
            this.amount = amount;
            if (type.equalsIgnoreCase(EquipmentStandard.MOD_ID + ":generic.crit_chance"))
                // 暴击和暴击伤害都是累加的，而且都是百分比，为了显示正常，这里固定为 MULTIPLY_BASE
                this.operation = Operation.MULTIPLY_BASE;
            else if (type.equalsIgnoreCase(EquipmentStandard.MOD_ID + ":generic.real_damage"))
                // 真实伤害固定为 ADDITION
                this.operation = Operation.ADDITION;
            else
                this.operation = operation;
            this.slots = slots == null ? Collections.emptySet() : slots;
        }

        public Collection<EquipmentSlot> getEquipmentSlots() {
            return slots.stream().map(Slot::asEquipmentSlot).toList();
        }

        public CompoundTag toNbt() {
            CompoundTag nbtCompound = new CompoundTag();
            nbtCompound.putString("Type", type);
            if (!type.equalsIgnoreCase(ESAttributes.DURABLE) && merge) {
                // 要合并原版的
                nbtCompound.putBoolean("Merge", true);
            }
            nbtCompound.putDouble("Amount", amount);
            nbtCompound.putInt("Operation", operation.getId());
            if (!type.equalsIgnoreCase(ESAttributes.DURABLE) && slots != null && !slots.isEmpty())
                nbtCompound.putIntArray("Slots", slots.stream().mapToInt(Slot::getId).toArray());
            return nbtCompound;
        }

        public static final Set<CompoundTag> INVALID_NBT = new HashSet<>();

        public static Final fromNbt(CompoundTag nbt) {
            try {
                Operation operation = Operation.fromId(nbt.getInt("Operation"));
                Set<Slot> slots = Collections.emptySet();
                if (nbt.contains("Slots")) {
                    slots = Arrays.stream(nbt.getIntArray("Slots")).mapToObj(Slot::byId).collect(Collectors.toSet());
                }
                return new Final(
                        nbt.getString("Type"),
                        nbt.getBoolean("Merge"),
                        nbt.getDouble("Amount"),
                        operation,
                        slots
                );
            } catch (Throwable e) {
                if (INVALID_NBT.add(nbt))
                    EquipmentStandard.LOGGER.warn("Unable to create attribute from NBT({}): {}", nbt, e.getMessage());
                return null;
            }
        }

        public boolean equalsIgnoreAmount(Object obj) {
            if (!(obj instanceof Final f)) return false;
            if (!Objects.equals(f.type, type)) {
                return false;
            }
            if (merge != f.merge) {
                return false;
            }
            if (!Objects.equals(f.operation, operation)) {
                return false;
            }
            return Objects.equals(f.slots, slots);
        }
    }

    public static class AttributeModifier {
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
        private final Operation operation;
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
        /**
         * 范围步进，与 amount 互斥
         */
        private BigDecimal step;

        public AttributeModifier(Integer weights, Bonus bonus, Operation operation, Integer scale, BigDecimal amount, BigDecimal min, BigDecimal max, BigDecimal step) {
            this.weights = weights;
            this.bonus = bonus;
            this.operation = operation;
            this.scale = scale;
            this.amount = amount;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        public Integer getWeights() {
            return weights == null ? 1 : Math.max(weights, 0);
        }

        public Bonus getBonus() {
            return bonus;
        }

        public void setStep(BigDecimal step) {
            this.step = step;
        }

        public double getAmount() {
            if (amount == null) {
                double temp;
                if (min == null || max == null) {
                    temp = min != null ? min.doubleValue() : max != null ? max.doubleValue() : 0;
                } else {
                    double random = ThreadLocalRandom.current().nextDouble();

                    temp = random * (max.doubleValue() - min.doubleValue()) + min.doubleValue();
                    if (step != null) {
                        temp = step.doubleValue() * Math.round(temp / step.doubleValue());
                    }
                }
                return BigDecimal.valueOf(temp)
                        .setScale(scale == null ? 2 : scale, RoundingMode.CEILING)
                        .doubleValue();
            }
            return amount.doubleValue();
        }

        public double getMaxAmount() {
            return (amount == null ? max : amount).doubleValue();
        }

        public Operation getOperation() {
            if (operation == null) return Operation.ADDITION;
            return this.operation;
        }
    }


    public enum Operation {
        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2),
        MULTIPLY_ADDITION(3);
        private final int id;

        Operation(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static Operation fromId(int id) {
            if (id >= 0 && id < Operation.values().length) {
                return Operation.values()[id];
            } else {
                throw new IllegalArgumentException("No operation with value " + id);
            }
        }

        public net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation convert() {
            if (this == MULTIPLY_ADDITION) {
                return net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
            }
            return net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.fromValue(id);
        }
    }
}
