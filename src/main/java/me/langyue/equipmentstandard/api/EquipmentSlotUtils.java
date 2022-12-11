package me.langyue.equipmentstandard.api;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class EquipmentSlotUtils {

    /**
     * 获取默认装备槽
     */
    public static Collection<EquipmentSlot> getDefaultEquipmentSlot(ItemStack stack) {
        return getDefaultEquipmentSlot(stack.getItem());
    }

    /**
     * 获取默认装备槽
     */
    public static Collection<EquipmentSlot> getDefaultEquipmentSlot(Item item) {
        if (item instanceof ArmorItem armorItem)
            return Collections.singleton(armorItem.getSlotType());

        if (item instanceof ShieldItem)
            return Set.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);

        return Collections.singleton(EquipmentSlot.MAINHAND);
    }
}
