package me.langyue.equipmentstandard.api.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Objects;

public final class EquipmentComponents {
    public static final String NBT_KEY = "ES:Components";
    private final String maker;
    private final int proficiency;
    private Integer score;

    public EquipmentComponents() {
        this(null, 0);
    }

    /**
     * @param maker       制作者
     * @param proficiency 制作时的熟练度，方便鉴定时使用
     */
    public EquipmentComponents(String maker, int proficiency) {
        this.maker = maker;
        this.proficiency = proficiency;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        if (maker != null) {
            nbt.putString("Maker", maker);
        }
        if (proficiency != 0) {
            nbt.putInt("Proficiency", proficiency);
        }
        if (score != null) {
            nbt.putInt("Score", score);
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
            EquipmentComponents components = new EquipmentComponents(nbt.getString("Maker"), nbt.getInt("Proficiency"));
            if (nbt.contains("Score", NbtElement.INT_TYPE)) {
                components.setScore(nbt.getInt("Score"));
            }
            return components;
        } catch (Throwable e) {
            return null;
        }
    }

    public static EquipmentComponents fromItem(ItemStack itemStack) {
        return fromNbt(itemStack.getSubNbt(NBT_KEY));
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getMaker() {
        return maker;
    }

    public int getProficiency() {
        return proficiency;
    }

    public Integer getScore() {
        return score;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EquipmentComponents) obj;
        return Objects.equals(this.maker, that.maker) &&
                this.proficiency == that.proficiency &&
                Objects.equals(this.score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maker, proficiency, score);
    }

    @Override
    public String toString() {
        return "EquipmentComponents[" +
                "maker=" + maker + ", " +
                "proficiency=" + proficiency + ", " +
                "score=" + score + ']';
    }

}
