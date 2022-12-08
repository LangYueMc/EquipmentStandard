package me.langyue.equipmentstandard.api;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;

public class EquipmentSlotUtils {

    /**
     * 获取默认装备槽
     */
    public static EquipmentSlot[] getDefaultEquipmentSlot(ItemStack stack) {
        return getDefaultEquipmentSlot(stack.getItem());
    }

    /**
     * 获取默认装备槽
     */
    public static EquipmentSlot[] getDefaultEquipmentSlot(Item item) {
        if (item instanceof ArmorItem armorItem)
            return new EquipmentSlot[]{armorItem.getSlotType()};

        if (item instanceof ShieldItem)
            return new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

        return new EquipmentSlot[]{EquipmentSlot.MAINHAND};
    }
}
