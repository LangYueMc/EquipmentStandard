package me.langyue.equipmentstandard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.util.internal.ThreadLocalRandom;
import me.langyue.equipmentstandard.api.CustomTag;
import me.langyue.equipmentstandard.api.DamageSourcesAccessor;
import me.langyue.equipmentstandard.world.entity.ai.attributes.ESAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 提取重复 Mixin 代码，便于直接调用
 */
public class MixinUtils {

    private static double getFinalAttr(double value, AttributeInstance instance) {
        if (instance != null) {
            for (var modifier : instance.getModifiers(AttributeModifier.Operation.ADDITION)) {
                value += modifier.getAmount();
            }
            for (var modifier : instance.getModifiers(AttributeModifier.Operation.MULTIPLY_BASE)) {
                value *= (1 + modifier.getAmount());
            }
            for (var modifier : instance.getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
                value *= (1 + modifier.getAmount());
            }
        }
        return value;
    }

    /**
     * 挖掘速度
     */
    public static float getDestroySpeedMixin(Player player, float f) {
        var attribute = player.getAttribute(ESAttributes.DIG_SPEED);
        double speed = getFinalAttr(f, attribute);

        return (float) speed;
    }

    /**
     * 暴击
     */
    public static boolean isCrit(LivingEntity entity) {
        if (entity == null) return false;
        if (entity.level().isClientSide()) return false;
        // 暴击几率
        double chance = entity.fallDistance > 0.0F
                && !entity.onGround()
                && !entity.onClimbable()
                && !entity.isInWater()
                && !entity.hasEffect(MobEffects.BLINDNESS)
                && !entity.isPassenger()
                && !entity.isSprinting()
                ? EquipmentStandard.CONFIG.jumpAttackCritChance : EquipmentStandard.CONFIG.baseCritChance;
        if (chance < 1) {
            var chanceInstance = entity.getAttribute(ESAttributes.CRIT_CHANCE);
            if (chanceInstance != null) {
                for (AttributeModifier modifier : chanceInstance.getModifiers()) {
                    chance += modifier.getAmount();
                }
            }
            chance = Math.max(EquipmentStandard.CONFIG.baseCritChance, chance);
        } else {
            return true;
        }
        try {
            return ThreadLocalRandom.current().nextDouble() < chance;
        } catch (Throwable e) {
            EquipmentStandard.debug(e.getMessage());
        }
        return false;
    }

    /**
     * 暴伤
     */
    public static float getCritDamageMultiplier(LivingEntity entity) {
        if (entity.level().isClientSide()) return (float) EquipmentStandard.CONFIG.baseCritDamageMultiplier;
        // 暴击伤害倍率
        var damageInstance = entity.getAttribute(ESAttributes.CRIT_DAMAGE);
        double damageMultiplier = getFinalAttr(EquipmentStandard.CONFIG.baseCritDamageMultiplier - 1, damageInstance);
        try {
            // 兼容 AdditionalEntityAttributes
            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation("additionalentityattributes:critical_bonus_damage"));
            if (attribute != null) {
                // additionalentityattributes:critical_bonus_damage 基础是 0.5
                damageMultiplier += getFinalAttr(0.5, entity.getAttribute(attribute)) - 0.5;
            }
        } catch (Throwable ignored) {
        }
        return (float) Math.max(damageMultiplier + 1, 1.1);
    }

    /**
     * 真伤
     */
    public static void realDamageMixin(LivingEntity entity, Entity target) {
        if (entity.level().isClientSide()) return;
        if (target instanceof LivingEntity) {
            target.hurt(((DamageSourcesAccessor) target.damageSources()).realDamage(entity), (float) entity.getAttributeValue(ESAttributes.REAL_DAMAGE));
        }
    }

    public static <T> Map<TagKey<T>, List<Holder<T>>> bindTagsMixin(Map<TagKey<T>, List<Holder<T>>> map, Stream<Holder.Reference<T>> holders) {
        if (map instanceof HashMap<TagKey<T>, List<Holder<T>>>) {
            // 客户端是 HashMap，源码 ClientCommonPacketListenerImpl.updateTagsForRegistry
            // 服务端 ImmutableCollections.MapN，源码 ReloadableServerResources.updateRegistryTags
            // 这里如果是客户端直接返回
            return map;
        }
        Map<TagKey<T>, List<Holder<T>>> result = Maps.newHashMap(map);
        holders.forEach(entry -> {
            if (entry.value() instanceof Item item) {
                CustomTag.getAll().forEach(customTag -> {
                    if (customTag.test(item)) {
                        TagKey tagKey = customTag.getTagKey();
                        List<Holder<T>> entryList;
                        if (result.containsKey(tagKey)) {
                            entryList = result.get(tagKey);
                            if (!(entryList instanceof LinkedList<Holder<T>>)) {
                                entryList = Lists.newLinkedList(entryList);
                            }
                        } else {
                            entryList = new LinkedList<>();
                            result.put(tagKey, entryList);
                        }
                        entryList.add(entry);
                    }
                });
            }
        });
        return result;
    }
}
