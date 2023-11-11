package me.langyue.equipmentstandard.api;

import com.google.common.util.concurrent.AtomicDouble;
import me.langyue.equipmentstandard.api.data.Attribute;
import me.langyue.equipmentstandard.api.data.ItemRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ItemRarityManager {

    private static final Map<ResourceLocation, ItemRarity> ITEM_RARITY_MAP = new HashMap<>();
    private static final Set<ItemRarity.Rarity> DEFAULT_RARITY = new LinkedHashSet<>() {{
        add(new ItemRarity.Rarity("Defective", Integer.MIN_VALUE, Component.translatable("item.es.rarity.defective"), ChatFormatting.GRAY));
        add(new ItemRarity.Rarity("Common", -20, Component.empty(), ChatFormatting.WHITE));
        add(new ItemRarity.Rarity("Uncommon", 100, Component.translatable("item.es.rarity.uncommon"), ChatFormatting.GREEN));
        add(new ItemRarity.Rarity("Rare", 300, Component.translatable("item.es.rarity.rare"), ChatFormatting.BLUE));
        add(new ItemRarity.Rarity("Epic", 600, Component.translatable("item.es.rarity.epic"), ChatFormatting.LIGHT_PURPLE));
        add(new ItemRarity.Rarity("Legendary", 900, Component.translatable("item.es.rarity.legendary"), ChatFormatting.GOLD));
        add(new ItemRarity.Rarity("Unique", 1200, Component.translatable("item.es.rarity.unique"), ChatFormatting.RED));
    }};

    public static void clear() {
        ITEM_RARITY_MAP.clear();
    }

    public static void put(ResourceLocation id, ItemRarity itemRarity) {
        ITEM_RARITY_MAP.put(id, itemRarity);
    }

    public static void put(Map<ResourceLocation, ItemRarity> map) {
        ITEM_RARITY_MAP.putAll(map);
    }

    public static int size() {
        return ITEM_RARITY_MAP.size();
    }

    /**
     * 获取装备品质
     *
     * @param stack 物品
     * @return 装备分
     */
    public static ItemRarity.Rarity get(ItemStack stack) {
//        var templates = EquipmentTemplateManager.get(stack);
//        if (templates == null || templates.isEmpty()) {
//            return null;
//        }
//        double maxScore = 0;
//        for (EquipmentTemplate template : templates) {
//            double templateMaxScore = 0;
//            for (Attribute attribute : template.getAttributes()) {
//                if (attribute.getChance() == 0) {
//                    continue;
//                }
//                double attributeMaxScore = 0;
//                for (Attribute.AttributeModifier modifier : attribute.getModifiers()) {
//                    if (modifier.getMaxAmount() == 0) {
//                        continue;
//                    }
//                    attributeMaxScore = Math.max(attributeMaxScore, modifier.getMaxAmount() * AttributeScoreManager.get(attribute.getType(), modifier.getOperation().convert()));
//                }
//                templateMaxScore += attributeMaxScore;
//            }
//            maxScore += templateMaxScore;
//        }
//        if (maxScore <= 0) {
//            return null;
//        }
        Integer score = getScore(stack);
        if (score == null) return null;
        return ITEM_RARITY_MAP.values().stream()
                .filter(it -> it.isValid(stack))
                .findFirst()
                .orElse(new ItemRarity(null, null, DEFAULT_RARITY.stream().toList()))
                .getRarity(score)
                .create(score);
    }

    /**
     * 计算装备分数
     *
     * @param stack 物品
     * @return 装备分
     */
    private static Integer getScore(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        CompoundTag nbt = stack.getTagElement(ModifierUtils.NBT_KEY);
        if (nbt == null) return null;
        AtomicDouble atomic = new AtomicDouble(0);
        nbt.getAllKeys().stream()
                .map(key -> Attribute.Final.fromNbt(nbt.getCompound(key)))
                .filter(Objects::nonNull)
                .forEach(attribute -> {
                    if (attribute.operation() == Attribute.Operation.MULTIPLY_ADDITION) {
                        // 增加百分比是在原来的基础上增加，所以只需要获取到原有的值，计算后就能获取准确数值
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            Optional<AttributeModifier> first = ((ItemStackAccessor) (Object) stack).es$getOriginalAttributeModifiers(slot)
                                    .get(BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(attribute.type())))
                                    .stream()
                                    .filter(modifier -> modifier.getOperation() == AttributeModifier.Operation.ADDITION)
                                    .findFirst();
                            if (first.isPresent()) {
                                double amount = first.get().getAmount() * attribute.amount();
                                atomic.addAndGet(amount * AttributeScoreManager.get(attribute.type(), AttributeModifier.Operation.ADDITION));
                                break;
                            }
                        }
                    } else {
                        atomic.addAndGet(attribute.amount() * AttributeScoreManager.get(attribute.type(), attribute.operation().convert()));
                    }
                });
        return (int) atomic.get();
    }
}
