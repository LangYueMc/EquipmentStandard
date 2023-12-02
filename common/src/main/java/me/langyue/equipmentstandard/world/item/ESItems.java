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

public class ESItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<BlockItem> REFORGE_TABLE_ITEM = ITEMS.register(ReforgeTableBlock.getID(), () -> new BlockItem(ESBlocks.REFORGE_TABLE_BLOCK.get(), new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));

    private static void register(Config.ReforgeScrollData data) {
        ITEMS.register(data.id(), () -> new ReforgeScroll(data.bonus(), data.cost(), data.proficiency(), data.rarity()));
    }

    public static void register() {
        EquipmentStandard.CONFIG.reforgeScrolls.forEach(ESItems::register);
        ITEMS.register();
    }
}
