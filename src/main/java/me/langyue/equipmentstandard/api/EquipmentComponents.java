package me.langyue.equipmentstandard.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class EquipmentComponents {
    public static final String NBT_KEY = "ES:Components";
    /**
     * 制作者
     */
    private final String maker;
    /**
     * 制作时的熟练度，方便鉴定时使用
     */
    private final int proficiency;

    public EquipmentComponents(String maker, int proficiency) {
        this.maker = maker;
        this.proficiency = proficiency;
    }

    public String getMaker() {
        return maker;
    }

    public int getProficiency() {
        return proficiency;
    }

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
