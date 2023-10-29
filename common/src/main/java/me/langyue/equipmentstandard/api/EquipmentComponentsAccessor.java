package me.langyue.equipmentstandard.api;

import me.langyue.equipmentstandard.api.data.EquipmentComponents;
import me.langyue.equipmentstandard.api.data.ItemRarity;
import net.minecraft.world.entity.player.Player;

public interface EquipmentComponentsAccessor {

    default EquipmentComponents es$getComponents() {
        return null;
    }

    default String es$getMaker() {
        return null;
    }

    default void es$setMaker(Player player) {
    }

    /**
     * 获取装备评分
     */
    default Integer es$getScore() {
        return null;
    }

    /**
     * 重新计算评分
     * <p>
     * 请仅在服务端计算
     */
    default void es$updateScore() {
    }

    /**
     * 获取装备评分
     */
    default ItemRarity.Rarity es$getItemRarity() {
        return null;
    }
}
