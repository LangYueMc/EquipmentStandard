package me.langyue.equipmentstandard.world.item;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class ReforgeScroll extends Item {
    private final int bonus;
    private final int cost;
    private final float proficiency;

    public ReforgeScroll(int bonus, int cost, float proficiency, Rarity rarity) {
        super(new Properties().rarity(rarity).arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES));
        this.bonus = bonus;
        this.cost = cost;
        this.proficiency = proficiency;
    }

    public int getBonus() {
        return bonus;
    }

    public int getCost() {
        return cost;
    }

    public float getProficiency() {
        return proficiency;
    }

    @Override
    public boolean isFoil(ItemStack arg) {
        return true;
    }
}
