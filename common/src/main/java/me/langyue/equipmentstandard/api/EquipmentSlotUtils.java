package me.langyue.equipmentstandard.api;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

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
            return Collections.singleton(armorItem.getEquipmentSlot());
        if (item instanceof ElytraItem elytraItem)
            return Collections.singleton(elytraItem.getEquipmentSlot());
        if (item instanceof ShieldItem)
            return Set.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
        if (item instanceof HorseArmorItem || item instanceof SaddleItem)
            return Collections.singleton(EquipmentSlot.CHEST);

        return Collections.singleton(EquipmentSlot.MAINHAND);
    }
}
