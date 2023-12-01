package me.langyue.equipmentstandard.config;

import dev.architectury.platform.Platform;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config {
    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(EquipmentStandard.createResourceLocation("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(Platform.getConfigFolder().resolve(EquipmentStandard.MOD_ID + ".json5"))
                    .setJson5(true)
                    .build())
            .build();

    public static Screen generateScreen(Screen parent) {
        try {
            return Config.HANDLER.generateGui().generateScreen(parent);
        } catch (Throwable e) {
            return null;
        }
    }

    private static final String BASE = "base";
    private static final String MODIFIER = "modifier";

    /**
     * 显示耐久
     */
    @AutoGen(category = BASE)
    @SerialEntry(comment = "显示耐久\nWhether to show equipment durability")
    @TickBox
    public boolean showDurability = true;

    /**
     * 显示品质分
     */
    @AutoGen(category = BASE)
    @SerialEntry(comment = "显示品质分\nWhether to show equipment score")
    @TickBox
    public boolean showScore = true;

    /**
     * 在工具提示里合并同类属性修改器
     */
    @AutoGen(category = BASE)
    @SerialEntry(comment = "合并同类属性修改器\nMerge attribute modifiers of the same type in tooltips")
    @TickBox
    public boolean mergeModifiers = true;

    /**
     * 在工具提示里显示 MULTIPLY_BASE 和 MULTIPLY_TOTAL 的补充说明
     */
    @AutoGen(category = BASE)
    @SerialEntry(comment = "显示 MULTIPLY_BASE 和 MULTIPLY_TOTAL 的补充说明\nShow additional notes for MULTIPLY_BASE and MULTIPLY_TOTAL")
    @TickBox
    public boolean showMultiplyOperationAdditional = true;

    /**
     * 基础暴击率
     */
    @AutoGen(category = BASE)
    @SerialEntry(comment = "基础暴击率\nBase crit chance")
    @DoubleSlider(min = 0d, max = 1d, step = 0.01d)
    @CustomFormat(PercentFormatter.class)
    public double baseCritChance = 0.01;

    /**
     * 跳击暴击率
     */
    @AutoGen(category = BASE)
    @SerialEntry(comment = "跳击暴击率\nBase crit chance")
    @DoubleSlider(min = 0d, max = 1d, step = 0.01d)
    @CustomFormat(PercentFormatter.class)
    public double jumpAttackCritChance = 0.1;

    /**
     * 基础暴击伤害倍率
     */
    @AutoGen(category = BASE)
    @SerialEntry(comment = "基础暴击伤害倍率\nBase crit damage multiplier")
    @DoubleSlider(min = 0d, max = 2d, step = 0.1d)
    @CustomFormat(PercentFormatter.class)
    public double baseCritDamageMultiplier = 1.5;

    @AutoGen(category = MODIFIER)
    @SerialEntry(comment = "属性应用于玩家制作\nAttributeModifier are applied to player crafting")
    @TickBox
    public boolean appliedToCrafting = true;

    @AutoGen(category = MODIFIER)
    @SerialEntry(comment = "属性应用于战利品箱子\nAttributeModifier are applied to loot chests")
    @TickBox
    public boolean appliedToLoot = true;

    @AutoGen(category = MODIFIER)
    @SerialEntry(comment = "属性应用于交易（村民等）\nAttributeModifier applied to merchant (eg. villager)")
    @TickBox
    public boolean appliedToMerchant = true;

    @AutoGen(category = MODIFIER)
    @SerialEntry(comment = "属性应用于怪物装备\nAttributeModifier are applied to equipped items on mobs")
    @TickBox
    public boolean appliedToMob = true;

    @AutoGen(category = MODIFIER)
    @SerialEntry(comment = "属性应用于世界生成（盔甲架、展示框、等）\nAttributeModifier are applied to world generation (Armor stands, Item frames, etc)")
    @TickBox
    public boolean appliedToWorldGeneration = true;

    @AutoGen(category = MODIFIER)
    @SerialEntry(comment = "属性应用于刷怪笼\nAttributeModifier are applied to spawner block")
    @TickBox
    public boolean appliedToSpawnerBlock = true;

    @AutoGen(category = BASE)
    @SerialEntry(comment = "开启 DEBUG 模式，开启可能会有日志刷屏")
    @TickBox
    public boolean debug = false;

    @SerialEntry(comment = """
            重铸卷轴（Reforge Scroll）
            {
                // 卷轴的物品 ID
                // The item ID of the scroll
                id: "reforge_scroll_lv1",
                // 卷轴加成，越高重铸出来的属性越好
                // Scroll bonus, the higher the reforge stat, the better
                bonus: -200,
                // 重铸花费的经验值
                // XP cost to reforge
                cost: 10,
                // 重铸增加的熟练度，不建议大于 1，最多支持两位小数
                // Reforge increases proficiency and is not recommended to be greater than 1, with a maximum of two decimal places
                proficiency: 0.1,
                // 稀有度（Rarity）
                // COMMON UNCOMMON RARE EPIC
                rarity: "COMMON"
            },
            """)
    public List<ReforgeScrollData> reforgeScrolls = new ArrayList<>() {{
        add(new ReforgeScrollData("reforge_scroll_lv1", -200, 10, 0.1f, Rarity.COMMON));
        add(new ReforgeScrollData("reforge_scroll_lv2", 800, 20, 0.5f, Rarity.RARE));
        add(new ReforgeScrollData("reforge_scroll_lv3", 1500, 30, 1f, Rarity.EPIC));
    }};

    public static class ReforgeScrollData implements ListGroup.ValueFactory<ReforgeScrollData> {
        @SerialEntry
        public final String id;
        @SerialEntry
        public final int bonus;
        @SerialEntry
        public final int cost;
        @SerialEntry
        public final float proficiency;
        @SerialEntry
        public final Rarity rarity;

        public ReforgeScrollData(String id, int bonus, int cost, float proficiency, Rarity rarity) {
            this.id = id;
            this.bonus = bonus;
            this.cost = cost;
            this.proficiency = proficiency;
            this.rarity = Objects.requireNonNullElse(rarity, Rarity.COMMON);
        }

        @Override
        public ReforgeScrollData provideNewValue() {
            return new ReforgeScrollData(id, bonus, cost, proficiency, rarity);
        }
    }

    public static final class PercentFormatter implements ValueFormatter<Double> {
        @Override
        public Component format(Double value) {
            return Component.literal(String.format("%.0f%%", value * 100));
        }
    }
}
