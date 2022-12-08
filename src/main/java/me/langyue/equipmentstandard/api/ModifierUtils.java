package me.langyue.equipmentstandard.api;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.data.AttributeModifier;
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
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ModifierUtils {

    //    private static final UUID[] MODIFIERS = new UUID[]{
//            UUID.fromString("ac25f4f3-5364-5e84-3657-e8b1ced5af5e"),
//            UUID.fromString("eb4f641b-b3a4-518b-e2dc-5bf439880258"),
//            UUID.fromString("0d255166-f605-1ae3-8c76-050154e88fb7"),
//            UUID.fromString("a1aa5343-12b1-720f-0dda-2dff35637959"),
//            UUID.fromString("1a91e81a-60e6-4d4b-bf52-7b2d48ba03bd"),
//            UUID.fromString("9c729cb5-2d87-a640-14f9-3369073bd3fa"),
//            UUID.fromString("eb45d111-47be-302c-5a92-940fa4b8fc54"),
//            UUID.fromString("fcb10176-ceea-7d9b-bae1-ee511b3eeeda"),
//            UUID.fromString("a8417236-bebc-2c1e-0129-e9a2e8eb1de6"),
//            UUID.fromString("31e3a2bd-e17c-4df9-2778-2f68e9562c06")
//    };
    private static final Map<Integer, UUID> MODIFIERS = new HashMap<>() {{
        put(0, UUID.fromString("148cbbf5-fdcc-6373-3ee1-1de1fd04dcd3"));
        put(1, UUID.fromString("cde672f4-d3b8-2d02-7986-b4d4ae9cca34"));
        put(2, UUID.fromString("f126c718-d9a5-0df7-c359-5ee9f767d73f"));
        put(3, UUID.fromString("8c3eec0c-ccea-e528-a1f8-3570b56076a3"));
        put(4, UUID.fromString("a3b41263-b458-e9c3-c4f6-70dd5a6e5854"));
        put(5, UUID.fromString("59762c33-325d-324e-32bf-033a0fea3e6d"));
        put(100, UUID.fromString("3247cf42-0070-d827-591e-a7daf775efb9"));
        put(101, UUID.fromString("2fec55e9-b532-5174-70c7-5bf697b1c540"));
        put(102, UUID.fromString("40847eb6-1557-f7ea-e993-e13be6639a35"));
        put(103, UUID.fromString("e379214f-ba3b-0eae-18cf-e6f4e68e5cba"));
        put(104, UUID.fromString("20e16f73-d949-d628-6ec8-d6ec7a467287"));
        put(105, UUID.fromString("e1172547-654b-4e84-878c-c215e97ebc07"));
        put(200, UUID.fromString("43439918-f5c7-2ab7-34e2-27f3b7a64f2f"));
        put(201, UUID.fromString("fee74168-6d01-58fa-93d7-689984237d87"));
        put(202, UUID.fromString("2477ce2a-a933-fa4f-f551-c3c0be88fba9"));
        put(203, UUID.fromString("4538ea5b-46f8-f3ad-bfb0-295efeac4da3"));
        put(204, UUID.fromString("d8238345-f3c5-1d06-7cfe-df720a8ed0d7"));
        put(205, UUID.fromString("32805ac4-3898-5c26-7188-0e09dd969aa0"));
    }};

    private static NbtCompound getOrCreateSubNbt(NbtCompound nbt, String key) {
        if (nbt == null || StringUtils.isEmpty(key)) {
            return null;
        }
        if (nbt.contains(key, 10)) {
            return nbt.getCompound(key);
        } else {
            NbtCompound nbtCompound = new NbtCompound();
            nbt.put(key, nbtCompound);
            return nbtCompound;
        }
    }

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
        EquipmentTemplate template = EquipmentTemplateManager.get(stack);
        if (template == null) {
            return false;
        }
        NbtCompound nbt = stack.getOrCreateSubNbt(EquipmentStandard.MOD_ID);
        double luck = player != null && proficiencyBonus ? player.getAttributeValue(EntityAttributes.GENERIC_LUCK) : 0;
        int proficiency = player != null && luckyBonus ? Proficiency.get(player) : 0;
        template.getAttributes().stream()
                .filter(attribute -> {
                    double chance = attribute.getChance();
                    if (luck > 0 || proficiency > 0) {
                        chance = attribute.getChance() * getBonus(attribute.getBonus(), proficiency, luck);
                    }
                    if (chance <= 0) {
                        return false;
                    }
                    return EquipmentStandard.RANDOM.nextDouble() < chance;
                }).forEach(attribute -> {
                    AttributeModifier modifier = attribute.getNextModifier(proficiency, luck);
                    if (modifier == null || modifier.getAmount() == 0) {
                        // 如果值为 0 则不存储 NBT
                        return;
                    }
                    String type = attribute.getType();
                    List<EquipmentSlot> slots = attribute.getSlots();
                    // EntityAttributeModifier.Operation
                    // ADDITION(0)
                    // MULTIPLY_BASE(1)
                    // MULTIPLY_TOTAL(2)
                    EntityAttributeModifier.Operation operation = modifier.getOperation();
                    if (operation == null) {
                        // 默认为固定值
                        operation = EntityAttributeModifier.Operation.ADDITION;
                    }
                    int i = 0;
                    NbtCompound attributeNbt;
                    while (true) {
                        // 合并同类项
                        attributeNbt = getOrCreateSubNbt(nbt, type + (i++));
                        if (!attributeNbt.contains("type")) {
                            // 新创建的
                            break;
                        }
                        if (!type.equals(attributeNbt.getString("type"))) {
                            continue;
                        }
                        if (operation.getId() != attributeNbt.getInt("operation")) {
                            continue;
                        }
                        if (!(slots == null || slots.isEmpty() ? "" : slots.stream().map(Enum::name).collect(Collectors.joining(","))).equals(attributeNbt.getString("slots"))) {
                            continue;
                        }
                        break;
                    }
                    if (!attributeNbt.contains("type")) {
                        // 新创建的
                        attributeNbt.putString("type", type);
                        attributeNbt.putInt("operation", operation.getId());
                        attributeNbt.putDouble("amount", modifier.getAmount());
                        if (slots != null) {
                            attributeNbt.putString("slots", slots.stream().map(Enum::name).collect(Collectors.joining(",")));
                        }
                    } else {
                        attributeNbt.putDouble("amount", attributeNbt.getDouble("amount") + modifier.getAmount());
                    }
                });
        return true;
    }

    private static final Set<String> INVALID_ATTRIBUTE = new HashSet<>();

    /**
     * 应用属性
     */
    public static void modify(ItemStack stack, EquipmentSlot slot, Multimap<EntityAttribute, EntityAttributeModifier> multimap) {
        NbtCompound nbt = stack.getSubNbt(EquipmentStandard.MOD_ID);
        if (nbt == null) return;
        nbt.getKeys().stream().filter(key -> !key.startsWith(CustomEntityAttributes.DURABLE)).forEach(key -> {
            NbtCompound attributeNbt = getOrCreateSubNbt(nbt, key);
            String type = attributeNbt.getString("type");
            if (CustomEntityAttributes.DURABLE.equals(type)) {
                return;
            }
            Identifier identifier = new Identifier(type);
            EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(identifier);
            if (entityAttribute == null) {
                if (INVALID_ATTRIBUTE.add(type))
                    EquipmentStandard.LOGGER.warn("{} was referenced as an attribute type, but it does not exist!", identifier);
            } else {
                String slots = attributeNbt.getString("slots");
                if (StringUtils.isBlank(slots)) {
                    // 默认
                    slots = Arrays.stream(EquipmentSlotUtils.getDefaultEquipmentSlot(stack)).map(EquipmentSlot::name).collect(Collectors.joining(","));
                }
                slots = slots.toUpperCase();
                if (slots.contains("ANY") || slots.contains(slot.name())) {
                    EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.fromId(attributeNbt.getInt("operation"));
                    double amount = attributeNbt.getDouble("amount");
                    int uuidKey = operation.getId() * 100 + slot.getArmorStandSlotId();
                    UUID uuid = MODIFIERS.get(uuidKey);
                    if (uuid == null) {
                        uuid = MODIFIERS.put(uuidKey, UUID.randomUUID());
                    }
                    multimap.put(entityAttribute, new EntityAttributeModifier(uuid, identifier + ":" + slot.name(), amount, operation));
//                    multimap.put(entityAttribute, new EntityAttributeModifier(identifier.toString() + ":" + slot.name(), amount, operation));
                }
            }
        });
    }

    public static int getMaxDamage(ItemStack stack, int original) {
        NbtCompound nbt = stack.getSubNbt(EquipmentStandard.MOD_ID);
        if (nbt == null) return original;
        AtomicInteger modified = new AtomicInteger(original);
        nbt.getKeys().stream().filter(key -> key.startsWith(CustomEntityAttributes.DURABLE)).forEach(key -> {
            try {
                NbtCompound attributeNbt = getOrCreateSubNbt(nbt, key);
                String type = attributeNbt.getString("type");
                if (!CustomEntityAttributes.DURABLE.equals(type)) {
                    return;
                }
                EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.fromId(attributeNbt.getInt("operation"));
                double amount = attributeNbt.getDouble("amount");
                modified.addAndGet(switch (operation) {
                    case ADDITION -> (int) amount;
                    case MULTIPLY_BASE -> (int) (stack.getItem().getMaxDamage() * amount / 100);
                    case MULTIPLY_TOTAL -> (int) (original * amount / 100);
                });
            } catch (Throwable e) {
                EquipmentStandard.LOGGER.error("Invalid NBT({}): {}", key, nbt.get(key), e);
            }
        });
        return modified.get();
    }
}
