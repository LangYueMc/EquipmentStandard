package me.langyue.equipmentstandard.world.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.world.level.block.ESBlocks;
import me.langyue.equipmentstandard.world.level.block.ReforgeTableBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ESItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<BlockItem> REFORGE_TABLE_ITEM = ITEMS.register(ReforgeTableBlock.getID(), () -> new BlockItem(ESBlocks.REFORGE_TABLE_BLOCK.get(), new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
    public static final RegistrySupplier<ReforgeScroll> REFORGE_SCROLL_LV1 = ITEMS.register("reforge_scroll_lv1", () -> new ReforgeScroll(1));
    public static final RegistrySupplier<ReforgeScroll> REFORGE_SCROLL_LV2 = ITEMS.register("reforge_scroll_lv2", () -> new ReforgeScroll(2));
    public static final RegistrySupplier<ReforgeScroll> REFORGE_SCROLL_LV3 = ITEMS.register("reforge_scroll_lv3", () -> new ReforgeScroll(3));

    public static void register() {
        ITEMS.register();
    }
}
