package me.langyue.equipmentstandard.config;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@me.shedaniel.autoconfig.annotation.Config(name = EquipmentStandard.MOD_ID)
public class Config implements ConfigData {

    /**
     * 显示耐久
     */
    @Comment("显示耐久\nWhether to show equipment durability")
    @ConfigEntry.Gui.NoTooltip
    public boolean showDurability = true;

    /**
     * 显示品质分
     */
    @Comment("显示品质分\nWhether to show equipment score")
    @ConfigEntry.Gui.NoTooltip
    public boolean showScore = true;

    /**
     * 在工具提示里合并同类属性修改器
     */
    @Comment("合并同类属性修改器\nMerge attribute modifiers of the same type in tooltips")
    @ConfigEntry.Gui.Tooltip
    public boolean mergeModifiers = true;

    /**
     * 在工具提示里显示 MULTIPLY_BASE 和 MULTIPLY_TOTAL 的补充说明
     */
    @Comment("显示 MULTIPLY_BASE 和 MULTIPLY_TOTAL 的补充说明\nShow additional notes for MULTIPLY_BASE and MULTIPLY_TOTAL")
    @ConfigEntry.Gui.Tooltip
    public boolean showMultiplyOperationAdditional = true;

    /**
     * 基础暴击率
     */
    @Comment("基础暴击率\nBase crit chance")
    @ConfigEntry.Gui.NoTooltip
    public double baseCritChance = 0.01;

    /**
     * 跳击暴击率
     */
    @Comment("跳击暴击率\nBase crit chance")
    @ConfigEntry.Gui.NoTooltip
    public double jumpAttackCritChance = 0.1;

    /**
     * 基础暴击伤害倍率
     */
    @Comment("基础暴击伤害倍率\nBase crit damage multiplier")
    @ConfigEntry.Gui.NoTooltip
    public double baseCritDamageMultiplier = 1.5;

    @Comment("属性应用于玩家制作\nAttributes are applied to player crafting")
    @ConfigEntry.Gui.NoTooltip
    public boolean appliedToCrafting = true;

    @Comment("属性应用于战利品箱子\nAttributes are applied to loot chests")
    @ConfigEntry.Gui.NoTooltip
    public boolean appliedToLoot = true;

    @Comment("属性应用于交易（村民等）\nAttributes applied to merchant (eg. villager)")
    @ConfigEntry.Gui.Tooltip
    public boolean appliedToMerchant = true;

    @Comment("属性应用于怪物装备\nAttributes are applied to equipped items on mobs")
    @ConfigEntry.Gui.NoTooltip
    public boolean appliedToMob = true;

    @Comment("属性应用于世界生成（盔甲架、展示框、等）\nAttributes are applied to world generation (Armor stands, Item frames, etc)")
    @ConfigEntry.Gui.Tooltip
    public boolean appliedToWorldGeneration = true;

    @Comment("开启 DEBUG 模式，开启可能会有日志刷屏")
    @ConfigEntry.Gui.NoTooltip
    public boolean debug = false;

    public static void init() {
        AutoConfig.register(Config.class, JanksonConfigSerializer::new);
        EquipmentStandard.CONFIG = AutoConfig.getConfigHolder(Config.class).getConfig();
    }
}
