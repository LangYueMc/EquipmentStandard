package me.langyue.equipmentstandard.api;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicDouble;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.data.Attribute;
import me.langyue.equipmentstandard.data.Bonus;
import me.langyue.equipmentstandard.data.EquipmentTemplate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ModifierUtils {
    public static final String MODIFIER_NAME = "ES modifier";
    public static final String NBT_KEY = "ES:modifier";

    public static final Set<String> INVALID_ATTRIBUTE = new HashSet<>();
    /**
     * Item.ATTACK_DAMAGE_MODIFIER_ID
     */
    public static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    /**
     * Item.ATTACK_SPEED_MODIFIER_ID
     */
    public static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static final Map<String, UUID> _MODIFIERS = new HashMap<>();

    public static UUID getModifierId(String key) {
        if (!_MODIFIERS.containsKey(key)) {
            _MODIFIERS.put(key, UUID.randomUUID());
        }
        return _MODIFIERS.get(key);
    }

    public static UUID getModifierId(Attribute.Final f, EquipmentSlot slot) {
        return getModifierId(f.type() + "_" + f.operation().getId() + "_" + slot.getName());
    }

    public static boolean setItemStackAttribute(ItemStack stack) {
        return setItemStackAttribute(stack, 0, 0);
    }

    public static boolean setItemStackAttribute(ItemStack stack, PlayerEntity player) {
        return setItemStackAttribute(stack, player, true, true);
    }

    public static boolean setItemStackAttribute(ItemStack stack, PlayerEntity player, boolean proficiencyBonus, boolean luckyBonus) {
        double luck = player != null && luckyBonus ? player.getAttributeValue(EntityAttributes.GENERIC_LUCK) : 0;
        int proficiency = player != null && proficiencyBonus ? ((ProficiencyAccessor) player).getProficiency() : 0;
        return setItemStackAttribute(stack, proficiency, luck);
    }

    public static double getBonus(Bonus bonus, int proficiency, double luck) {
        double magnification = 1.0;
        if (bonus != null) {
            if (proficiency > 0) {
                // 每一点熟练度加成百分比
                magnification += bonus.getProficiency() * proficiency;
            }
            if (luck > 0) {
                // 每一点幸运值加成百分比
                magnification += bonus.getLuck() * luck;
            }
        }
        return magnification;
    }

    public static boolean setItemStackAttribute(ItemStack stack, int proficiency, double luck) {
        if (stack.getCount() > 1) return false; // 堆叠的物品不添加
        if (stack.getSubNbt(NBT_KEY) != null) return false;
        var templates = EquipmentTemplateManager.get(stack);
        if (templates == null || templates.isEmpty()) {
            return false;
        }
        Multimap<Attribute.Operation, Attribute.Final> nbtMultimap = LinkedListMultimap.create();
        for (EquipmentTemplate template : templates) {
            template.getAttributes().stream()
                    .filter(attribute -> {
                        if (attribute.getType().equalsIgnoreCase(CustomEntityAttributes.DURABLE) && !stack.isDamageable()) {
                            // 如果物品是没有耐久的就不需要存储了
                            return false;
                        }
                        double chance = attribute.getChance();
                        if (luck > 0 || proficiency > 0) {
                            chance = attribute.getChance() * getBonus(attribute.getBonus(), proficiency, luck);
                        }
                        if (chance <= 0) {
                            return false;
                        }
                        if (chance >= 1) {
                            return true;
                        }
                        return EquipmentStandard.RANDOM.nextDouble() < chance;
                    }).forEach(attribute -> {
                        Attribute.Final attributeFinal = attribute.getFinal(proficiency, luck);
                        if (attributeFinal != null) {
                            var operation = attributeFinal.operation();
                            nbtMultimap.put(operation, attributeFinal);
                        }
                    });
        }
        NbtCompound nbt = stack.getOrCreateSubNbt(NBT_KEY);
        for (var operation : Attribute.Operation.values()) {
            for (Attribute.Final attribute : nbtMultimap.get(operation)) {
                int i = 0;
                String key;
                while (true) {
                    key = attribute.type() + operation.getId() + i++;
                    if (!nbt.contains(key))
                        break;
                    if (Attribute.mergeToNbt(attribute, nbt.getCompound(key)))
                        break;
                }
                nbt.put(key, attribute.toNbt());
            }
        }
        return true;
    }

    /**
     * 应用属性
     */
    public static void modify(ItemStack stack, EquipmentSlot slot, Multimap<EntityAttribute, EntityAttributeModifier> multimap) {
        NbtCompound nbt = stack.getSubNbt(NBT_KEY);
        if (nbt == null) return;
        nbt.getKeys().stream()
                .filter(key -> !key.startsWith(CustomEntityAttributes.DURABLE))
                .map(key -> Attribute.Final.fromNbt(nbt.getCompound(key)))
                .filter(af -> af != null && !CustomEntityAttributes.DURABLE.equals(af.type()))
                .sorted(Comparator.comparing(Attribute.Final::operation))
                .forEach(attribute -> {
                    Identifier identifier = new Identifier(attribute.type());
                    EntityAttribute entityAttribute = Registries.ATTRIBUTE.get(identifier);
                    if (entityAttribute == null) {
                        if (INVALID_ATTRIBUTE.add(attribute.type()))
                            EquipmentStandard.LOGGER.warn("{} was referenced as an attribute type, but it does not exist!", attribute.type());
                    } else {
                        Set<EquipmentSlot> slots = new HashSet<>(attribute.getEquipmentSlots());
                        if (attribute.slots().isEmpty() || attribute.slots().contains(Attribute.Slot.DEFAULT)) {
                            // 未设置或者有 DEFAULT
                            slots.addAll(EquipmentSlotUtils.getDefaultEquipmentSlot(stack));
                        }
                        if (attribute.slots().contains(Attribute.Slot.ANY) || slots.contains(slot)) {
                            var operation = attribute.operation().convert();
                            AtomicReference<Double> amount = new AtomicReference<>(attribute.amount());
                            if (attribute.operation() == Attribute.Operation.ADDITION_PERCENTAGE) {
                                // 增加百分比
                                multimap.get(entityAttribute).stream()
                                        .filter(modifier -> modifier.getOperation() == operation)
                                        .findFirst()
                                        .ifPresentOrElse(modifier -> amount.set(modifier.getValue() * amount.get() / 100), () -> amount.set(0d));
                            }
                            UUID uuid = attribute.merge() ? UUID.randomUUID() : getModifierId(attribute, slot);
                            String name = attribute.merge() ? "es:merge" : MODIFIER_NAME;
                            if (amount.get() != 0){
                                multimap.put(entityAttribute, new EntityAttributeModifier(uuid, name, amount.get(), operation));
                            }
                        }
                    }
                });
        var temp = LinkedListMultimap.create(multimap);
        multimap.clear();
        temp.forEach((k, v) -> {
            if (v.getName().equals(MODIFIER_NAME)) {
                multimap.put(k, v);
                return;
            }
            if (v.getName().equals("es:merge")) {
                return;
            }
            AtomicDouble atomic = new AtomicDouble(v.getValue());
            temp.get(k).stream().filter(it -> it.getName().equals("es:merge") && !it.getId().equals(v.getId())).forEach(it -> {
                switch (it.getOperation()) {
                    case ADDITION -> atomic.addAndGet(it.getValue());
                    case MULTIPLY_TOTAL, MULTIPLY_BASE -> atomic.addAndGet(it.getValue() * v.getValue());
                }
            });
            var value = BigDecimal.valueOf(atomic.get()).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            multimap.put(k, new EntityAttributeModifier(v.getId(), v.getName(), value, v.getOperation()));
        });
    }

    public static int getMaxDamage(ItemStack stack, int original) {
        if (!stack.isDamageable()) return original;
        NbtCompound nbt = stack.getSubNbt(NBT_KEY);
        if (nbt == null) return original;
        AtomicInteger modified = new AtomicInteger(original);
        nbt.getKeys().stream()
                .filter(key -> key.startsWith(CustomEntityAttributes.DURABLE))
                .map(key -> Attribute.Final.fromNbt(nbt.getCompound(key)))
                .filter(af -> af != null && CustomEntityAttributes.DURABLE.equals(af.type()))
                .sorted(Comparator.comparing(Attribute.Final::operation))
                .forEach(attribute -> modified.addAndGet(switch (attribute.operation()) {
                    case ADDITION -> (int) attribute.amount();
                    case ADDITION_PERCENTAGE -> (int) (stack.getItem().getMaxDamage() * attribute.amount() / 100);
                    case MULTIPLY_BASE -> (int) (stack.getItem().getMaxDamage() * attribute.amount());
                    case MULTIPLY_TOTAL -> (int) (original * attribute.amount());
                }));
        return modified.get();
    }

    /**
     * 计算装备分数
     *
     * @param stack 物品
     * @return 装备分
     */
    public static Map<EquipmentSlot, Double> getScore(ItemStack stack) {
        if (stack == null) return null;
        Map<EquipmentSlot, Double> score = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            var multimap = stack.getAttributeModifiers(slot);
            AtomicDouble atomic = new AtomicDouble(0);
            multimap.forEach((attribute, modifier) -> atomic.addAndGet(modifier.getValue() * AttributeScoreManager.get(attribute, modifier.getOperation())));
            score.put(slot, atomic.get());
        }
        return score;
    }
}
