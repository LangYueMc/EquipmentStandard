package me.langyue.equipmentstandard.api.data;

import me.langyue.equipmentstandard.data.ItemRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Objects;

public final class EquipmentComponents {
    public static final String NBT_KEY = "ES:Components";
    private final String maker;
    private final int proficiency;
    private Integer score;
    private ItemRarity.Rarity rarity;

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
        if (rarity != null) {
            nbt.putString("Rarity", rarity.getName());
            if (rarity.getPrefix() != null) {
                nbt.putString("NamePrefix", Text.Serializer.toJson(rarity.getPrefix()));
            }
            if (rarity.getFormattings() != null && rarity.getFormattings().length > 0) {
                nbt.putString("NameFormatting", String.join(" ", Arrays.stream(rarity.getFormattings()).map(Formatting::getName).toList()));
            }
        }
        return nbt;
    }

    public void save(ItemStack itemStack) {
        itemStack.setSubNbt(NBT_KEY, toNbt());
    }

    public static EquipmentComponents fromNbt(NbtCompound nbt) {
        if (nbt == null) return null;
        try {
            String maker = null;
            if (nbt.contains("Maker", NbtElement.STRING_TYPE)) {
                maker = nbt.getString("Maker");
            }
            EquipmentComponents components = new EquipmentComponents(maker, nbt.getInt("Proficiency"));
            if (nbt.contains("Score", NbtElement.INT_TYPE)) {
                components.setScore(nbt.getInt("Score"));
            }
            String rarity = null;
            if (nbt.contains("Rarity", NbtElement.STRING_TYPE)) {
                rarity = nbt.getString("Rarity");
            }
            MutableText namePrefix = null;
            if (nbt.contains("NamePrefix", NbtElement.STRING_TYPE)) {
                namePrefix = Text.Serializer.fromLenientJson(nbt.getString("NamePrefix"));
            }
            Formatting[] nameFormatting = null;
            if (nbt.contains("NameFormatting", NbtElement.STRING_TYPE)) {
                nameFormatting = Arrays.stream(nbt.getString("NameFormatting").split(" ")).map(Formatting::byName).toArray(Formatting[]::new);
            }
            components.setRarity(new ItemRarity.Rarity(rarity, 0, namePrefix, nameFormatting));
            return components;
        } catch (Throwable e) {
            return null;
        }
    }

    public static EquipmentComponents fromItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) return null;
        return fromNbt(itemStack.getSubNbt(NBT_KEY));
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setRarity(ItemRarity.Rarity rarity) {
        this.rarity = rarity;
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

    public ItemRarity.Rarity getRarity() {
        return rarity;
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
