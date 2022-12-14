package me.langyue.equipmentstandard.api.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

/**
 * @param maker       制作者
 * @param proficiency 制作时的熟练度，方便鉴定时使用
 */
public record EquipmentComponents(String maker, int proficiency) {
    public static final String NBT_KEY = "ES:Components";

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        if (maker != null) {
            nbt.putString("Maker", maker);
        }
        if (proficiency != 0) {
            nbt.putInt("Proficiency", proficiency);
        }
        return nbt;
    }

    public void save(ItemStack itemStack) {
        itemStack.setSubNbt(NBT_KEY, toNbt());
    }

    public static EquipmentComponents fromNbt(NbtCompound nbt) {
        try {
            if (!nbt.contains("Maker", NbtElement.STRING_TYPE)) {
                return null;
            }
            return new EquipmentComponents(nbt.getString("Maker"), nbt.getInt("Proficiency"));
        } catch (Throwable e) {
            return null;
        }
    }

    public static EquipmentComponents fromItem(ItemStack itemStack) {
        return fromNbt(itemStack.getSubNbt(NBT_KEY));
    }
}
