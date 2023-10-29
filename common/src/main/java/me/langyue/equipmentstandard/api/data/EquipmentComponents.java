package me.langyue.equipmentstandard.api.data;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

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

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
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
                nbt.putString("NamePrefix", Component.Serializer.toJson(rarity.getPrefix()));
            }
            if (rarity.getFormatting() != null && rarity.getFormatting().length > 0) {
                nbt.putString("NameFormatting", String.join(" ", Arrays.stream(rarity.getFormatting()).map(ChatFormatting::getName).toList()));
            }
        }
        return nbt;
    }

    public void save(ItemStack itemStack) {
        itemStack.addTagElement(NBT_KEY, toNbt());
    }

    public static EquipmentComponents fromNbt(CompoundTag nbt) {
        if (nbt == null) return null;
        try {
            String maker = null;
            if (nbt.contains("Maker", CompoundTag.TAG_STRING)) {
                maker = nbt.getString("Maker");
            }
            EquipmentComponents components = new EquipmentComponents(maker, nbt.getInt("Proficiency"));
            if (nbt.contains("Score", CompoundTag.TAG_INT)) {
                components.setScore(nbt.getInt("Score"));
            }
            String rarity = null;
            if (nbt.contains("Rarity", CompoundTag.TAG_STRING)) {
                rarity = nbt.getString("Rarity");
            }
            MutableComponent namePrefix = null;
            if (nbt.contains("NamePrefix", CompoundTag.TAG_STRING)) {
                namePrefix = Component.Serializer.fromJsonLenient(nbt.getString("NamePrefix"));
            }
            ChatFormatting[] nameFormatting = null;
            if (nbt.contains("NameFormatting", CompoundTag.TAG_STRING)) {
                nameFormatting = Arrays.stream(nbt.getString("NameFormatting").split(" ")).map(ChatFormatting::getByName).toArray(ChatFormatting[]::new);
            }
            components.setRarity(new ItemRarity.Rarity(rarity, 0, namePrefix, nameFormatting));
            return components;
        } catch (Throwable e) {
            return null;
        }
    }

    public static EquipmentComponents fromItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) return null;
        return fromNbt(itemStack.getTagElement(NBT_KEY));
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
