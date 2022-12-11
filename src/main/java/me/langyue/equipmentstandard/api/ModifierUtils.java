package me.langyue.equipmentstandard.api;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicDouble;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.data.Attribute;
import me.langyue.equipmentstandard.data.Bonus;
import me.langyue.equipmentstandard.data.EquipmentTemplate;
import me.langyue.equipmentstandard.stat.Proficiency;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ModifierUtils {
    public static final String MODIFIER_NAME = "ES modifier";

    public static final Set<String> INVALID_ATTRIBUTE = new HashSet<>();
    public static final Map<Integer, UUID> MODIFIERS = new HashMap<>() {{
        // EntityAttributeModifier.Operation
        // ADDITION(0)
        put(0, UUID.fromString("148cbbf5-fdcc-6373-3ee1-1de1fd04dcd3"));
        put(1, UUID.fromString("cde672f4-d3b8-2d02-7986-b4d4ae9cca34"));
        put(2, UUID.fromString("f126c718-d9a5-0df7-c359-5ee9f767d73f"));
        put(3, UUID.fromString("8c3eec0c-ccea-e528-a1f8-3570b56076a3"));
        put(4, UUID.fromString("a3b41263-b458-e9c3-c4f6-70dd5a6e5854"));
        put(5, UUID.fromString("59762c33-325d-324e-32bf-033a0fea3e6d"));
        // MULTIPLY_BASE(1)
        put(100, UUID.fromString("3247cf42-0070-d827-591e-a7daf775efb9"));
        put(101, UUID.fromString("2fec55e9-b532-5174-70c7-5bf697b1c540"));
        put(102, UUID.fromString("40847eb6-1557-f7ea-e993-e13be6639a35"));
        put(103, UUID.fromString("e379214f-ba3b-0eae-18cf-e6f4e68e5cba"));
        put(104, UUID.fromString("20e16f73-d949-d628-6ec8-d6ec7a467287"));
        put(105, UUID.fromString("e1172547-654b-4e84-878c-c215e97ebc07"));
        // MULTIPLY_TOTAL(2)
        put(200, UUID.fromString("43439918-f5c7-2ab7-34e2-27f3b7a64f2f"));
        put(201, UUID.fromString("fee74168-6d01-58fa-93d7-689984237d87"));
        put(202, UUID.fromString("2477ce2a-a933-fa4f-f551-c3c0be88fba9"));
        put(203, UUID.fromString("4538ea5b-46f8-f3ad-bfb0-295efeac4da3"));
        put(204, UUID.fromString("d8238345-f3c5-1d06-7cfe-df720a8ed0d7"));
        put(205, UUID.fromString("32805ac4-3898-5c26-7188-0e09dd969aa0"));
    }};

    public static boolean setItemStackAttribute(ItemStack stack) {
        return setItemStackAttribute(stack, null, false, false);
    }

    public static boolean setItemStackAttribute(ItemStack stack, PlayerEntity player) {
        return setItemStackAttribute(stack, player, true, true);
    }

    public static double getBonus(Bonus bonus, int proficiency, double lucky) {
        double magnification = 1.0;
        if (bonus != null) {
            if (proficiency > 0) {
                // 每一点熟练度加成百分比
                magnification += bonus.getProficiency() * proficiency;
            }
            if (lucky > 0) {
                // 每一点幸运值加成百分比
                magnification += bonus.getLuck() * lucky;
            }
        }
        return magnification;
    }

    public static boolean setItemStackAttribute(ItemStack stack, PlayerEntity player, boolean proficiencyBonus, boolean luckyBonus) {
        var templates = EquipmentTemplateManager.get(stack);
        if (templates == null || templates.isEmpty()) {
            return false;
        }
        double luck = player != null && proficiencyBonus ? player.getAttributeValue(EntityAttributes.GENERIC_LUCK) : 0;
        int proficiency = player != null && luckyBonus ? Proficiency.get(player) : 0;
        Multimap<EntityAttributeModifier.Operation, Attribute.Final> nbtMultimap = LinkedListMultimap.create();
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
                        return EquipmentStandard.RANDOM.nextDouble() < chance;
                    }).forEach(attribute -> {
                        Attribute.Final attributeFinal = attribute.getFinal();
                        if (attributeFinal != null) {
                            var operation = attributeFinal.operation();
                            nbtMultimap.put(operation, attributeFinal);
                        }
                    });
        }
        NbtCompound nbt = stack.getOrCreateSubNbt(EquipmentStandard.MOD_ID);
        for (var operation : EntityAttributeModifier.Operation.values()) {
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
        NbtCompound nbt = stack.getSubNbt(EquipmentStandard.MOD_ID);
        if (nbt == null) return;
        nbt.getKeys().stream()
                .filter(key -> !key.startsWith(CustomEntityAttributes.DURABLE))
                .map(key -> Attribute.Final.fromNbt(nbt.getCompound(key)))
                .filter(af -> af != null && !CustomEntityAttributes.DURABLE.equals(af.type()))
                .sorted(Comparator.comparing(Attribute.Final::operation))
                .forEach(attribute -> {
                    Identifier identifier = new Identifier(attribute.type());
                    EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(identifier);
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
                            var uuidKey = attribute.operation().getId() * 100 + slot.getArmorStandSlotId();
                            UUID uuid = attribute.merge() ? UUID.randomUUID() : MODIFIERS.get(uuidKey);
                            String name = attribute.merge() ? "es:merge" : MODIFIER_NAME;
                            if (uuid == null) {
                                uuid = MODIFIERS.put(uuidKey, UUID.randomUUID());
                            }
                            multimap.put(entityAttribute, new EntityAttributeModifier(uuid, name, attribute.amount(), attribute.operation()));
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
        NbtCompound nbt = stack.getSubNbt(EquipmentStandard.MOD_ID);
        if (nbt == null) return original;
        AtomicInteger modified = new AtomicInteger(original);
        nbt.getKeys().stream()
                .filter(key -> key.startsWith(CustomEntityAttributes.DURABLE))
                .map(key -> Attribute.Final.fromNbt(nbt.getCompound(key)))
                .filter(af -> af != null && CustomEntityAttributes.DURABLE.equals(af.type()))
                .sorted(Comparator.comparing(Attribute.Final::operation))
                .forEach(attribute -> modified.addAndGet(switch (attribute.operation()) {
                    case ADDITION -> (int) attribute.amount();
                    case MULTIPLY_BASE -> (int) (stack.getItem().getMaxDamage() * attribute.amount() / 100);
                    case MULTIPLY_TOTAL -> (int) (original * attribute.amount() / 100);
                }));
        return modified.get();
    }
}
