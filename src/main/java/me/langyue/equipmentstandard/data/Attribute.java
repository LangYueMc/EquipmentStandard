package me.langyue.equipmentstandard.data;

import com.google.common.util.concurrent.AtomicDouble;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.CustomEntityAttributes;
import me.langyue.equipmentstandard.api.ModifierUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Attribute {
    private final String type;
    private final boolean merge;
    private final BigDecimal chance;
    private final Bonus bonus;
    private final List<AttributeModifier> modifiers;
    private final Set<Slot> slots;

    public Attribute(String type, boolean merge, BigDecimal chance, Bonus bonus, List<AttributeModifier> modifiers, Set<Slot> slots) {
        this.type = type;
        this.merge = merge;
        this.chance = chance;
        this.bonus = bonus;
        this.modifiers = modifiers;
        this.slots = slots;
    }

    public String getType() {
        return type;
    }

    public boolean isMerge() {
        // 耐久默认就是修改原版的，所以耐久固定返回false，看不懂逻辑也别问问什么了，就是这样，千万不要改
        return merge && !type.equalsIgnoreCase(CustomEntityAttributes.DURABLE);
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
        return CustomEntityAttributes.DURABLE.equals(type) ? null : slots;
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

    public Final getFinal() {
        return getFinal(0, 0);
    }

    public Final getFinal(int proficiency, double luck) {
        if (this.getChance() <= 0) {
            return null;
        }
        AttributeModifier modifier = this.getNextModifier(proficiency, luck);
        if (modifier == null || modifier.getAmount() == 0) {
            // 如果值为 0 则不存储 NBT
            return null;
        }
        EntityAttributeModifier.Operation operation = modifier.getOperation();
        try {
            return new Final(
                    this.getType(),
                    !this.getType().equalsIgnoreCase(CustomEntityAttributes.DURABLE) && this.isMerge(),
                    modifier.getAmount(),
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
    public static boolean mergeToNbt(Final from, NbtCompound nbt) {
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
            EntityAttributeModifier.Operation operation,
            Set<Slot> slots
    ) {
        public Final(String type, boolean merge, double amount, EntityAttributeModifier.Operation operation, Set<Slot> slots) {
            if (StringUtils.isBlank(type)) throw new IllegalArgumentException("Attribute type can not be null.");
            if (amount == 0) throw new IllegalArgumentException("Attribute amount can not be 0.");
            if (operation == null) throw new IllegalArgumentException("Operation can not be null.");
            this.type = type;
            this.merge = merge;
            this.amount = amount;
            if (type.equalsIgnoreCase(EquipmentStandard.MOD_ID + ":generic.crit_chance")
                    || type.equalsIgnoreCase(EquipmentStandard.MOD_ID + ":generic.crit_damage"))
                // 暴击和暴击伤害都是累加的，而且都是百分比，为了显示正常，这里固定为 MULTIPLY_BASE
                this.operation = EntityAttributeModifier.Operation.MULTIPLY_BASE;
            else if (type.equalsIgnoreCase(EquipmentStandard.MOD_ID + ":generic.real_damage"))
                // 真实伤害固定为 ADDITION
                this.operation = EntityAttributeModifier.Operation.ADDITION;
            else
                this.operation = operation;
            this.slots = slots == null ? Collections.emptySet() : slots;
        }

        public Collection<EquipmentSlot> getEquipmentSlots() {
            return slots.stream().map(Slot::asEquipmentSlot).toList();
        }

        public NbtCompound toNbt() {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("Type", type);
            if (!type.equalsIgnoreCase(CustomEntityAttributes.DURABLE) && merge) {
                // 要合并原版的
                nbtCompound.putBoolean("Merge", true);
            }
            nbtCompound.putDouble("Amount", amount);
            nbtCompound.putInt("Operation", operation.getId());
            if (!type.equalsIgnoreCase(CustomEntityAttributes.DURABLE) && slots != null && !slots.isEmpty())
                nbtCompound.putIntArray("Slots", slots.stream().mapToInt(Slot::getId).toArray());
            return nbtCompound;
        }

        public static final Set<NbtCompound> INVALID_NBT = new HashSet<>();

        public static Final fromNbt(NbtCompound nbt) {
            try {
                EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.fromId(nbt.getInt("Operation"));
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
}
