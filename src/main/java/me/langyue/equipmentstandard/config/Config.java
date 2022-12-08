package me.langyue.equipmentstandard.config;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@me.shedaniel.autoconfig.annotation.Config(name = "equipment-standard")
public class Config implements ConfigData {

    @Comment("Whether to show equipment durability")
    public boolean showDurability = true;

    public static void init() {
        AutoConfig.register(Config.class, JanksonConfigSerializer::new);
        EquipmentStandard.CONFIG = AutoConfig.getConfigHolder(Config.class).getConfig();
    }
}
