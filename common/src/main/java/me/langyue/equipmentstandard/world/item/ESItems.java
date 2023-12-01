package me.langyue.equipmentstandard.world.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.config.Config;
import me.langyue.equipmentstandard.world.level.block.ESBlocks;
import me.langyue.equipmentstandard.world.level.block.ReforgeTableBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ESItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<BlockItem> REFORGE_TABLE_ITEM = ITEMS.register(ReforgeTableBlock.getID(), () -> new BlockItem(ESBlocks.REFORGE_TABLE_BLOCK.get(), new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
//    public static final RegistrySupplier<ReforgeScroll> REFORGE_SCROLL_LV1 = ITEMS.register("reforge_scroll_lv1", () -> new ReforgeScroll(bonus, cost, rarity));
//    public static final RegistrySupplier<ReforgeScroll> REFORGE_SCROLL_LV2 = ITEMS.register("reforge_scroll_lv2", () -> new ReforgeScroll(bonus, cost, rarity));
//    public static final RegistrySupplier<ReforgeScroll> REFORGE_SCROLL_LV3 = ITEMS.register("reforge_scroll_lv3", () -> new ReforgeScroll(bonus, cost, rarity));


    private static void registerReforgeScroll(Config.ReforgeScrollData data) {
        registerReforgeScroll(data.id, data.bonus, data.cost, data.proficiency, data.rarity);
    }

    private static void registerReforgeScroll(String id, int bonus, int cost, float proficiency, Rarity rarity) {
        ITEMS.register(id, () -> new ReforgeScroll(bonus, cost, proficiency, rarity));
    }

    public static void register() {
        EquipmentStandard.CONFIG.reforgeScrolls.forEach(ESItems::registerReforgeScroll);
        ITEMS.register();
    }
}
