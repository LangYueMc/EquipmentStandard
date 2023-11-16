package me.langyue.equipmentstandard.world.item;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class ReforgeScroll extends Item {
    private final int level;

    public ReforgeScroll(int level) {
        super(new Properties().rarity(switch (level) {
            case 1 -> Rarity.UNCOMMON;
            case 2 -> Rarity.RARE;
            default -> Rarity.EPIC;
        }).arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES));
        this.level = level;
    }

    public int getBonus() {
        return (int) (Math.pow(this.level, 2.2) * 100 - 300);
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    public boolean isFoil(ItemStack arg) {
        return true;
    }
}
