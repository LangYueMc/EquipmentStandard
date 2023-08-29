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
    @ConfigEntry.Gui.Tooltip(count = 0)
    public boolean showDurability = true;

    /**
     * 显示品质分
     */
    @Comment("显示耐久\nWhether to show equipment score")
    @ConfigEntry.Gui.Tooltip(count = 0)
    public boolean showScore = false;

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
    @ConfigEntry.Gui.Tooltip(count = 0)
    public double baseCritChance = 0.01;

    /**
     * 基础暴击伤害倍率
     */
    @Comment("基础暴击伤害倍率\nBase crit damage multiplier")
    @ConfigEntry.Gui.Tooltip(count = 0)
    public double baseCritDamageMultiplier = 1.5;

    public static void init() {
        AutoConfig.register(Config.class, JanksonConfigSerializer::new);
        EquipmentStandard.CONFIG = AutoConfig.getConfigHolder(Config.class).getConfig();
    }
}
