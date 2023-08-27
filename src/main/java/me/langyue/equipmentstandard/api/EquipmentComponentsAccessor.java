package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.api.data.EquipmentComponents;
import me.langyue.equipmentstandard.data.ItemRarity;
import net.minecraft.entity.player.PlayerEntity;

public interface EquipmentComponentsAccessor {

    default EquipmentComponents getComponents() {
        return null;
    }

    default String getMaker() {
        return null;
    }

    default void setMaker(PlayerEntity player) {
    }

    /**
     * 获取装备评分
     */
    default Integer getScore() {
        return null;
    }

    /**
     * 重新计算评分
     * <p>
     * 请仅在服务端计算
     */
    default void updateScore() {
    }

    /**
     * 获取装备评分
     */
    default ItemRarity.Rarity getItemRarity() {
        return null;
    }
}
