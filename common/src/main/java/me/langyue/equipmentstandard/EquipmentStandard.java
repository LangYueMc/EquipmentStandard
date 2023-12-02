package me.langyue.equipmentstandard;

import dev.architectury.registry.ReloadListenerRegistry;
import io.netty.util.internal.ThreadLocalRandom;
import me.langyue.equipmentstandard.config.Config;
import me.langyue.equipmentstandard.data.AttributeScoreLoader;
import me.langyue.equipmentstandard.data.ItemRarityLoader;
import me.langyue.equipmentstandard.data.TemplateDataLoader;
import me.langyue.equipmentstandard.world.entity.ai.attributes.ESAttributes;
import me.langyue.equipmentstandard.world.inventory.ESMenu;
import me.langyue.equipmentstandard.world.item.ESItems;
import me.langyue.equipmentstandard.world.level.block.ESBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EquipmentStandard {
    public static final Logger LOGGER = LoggerFactory.getLogger("EquipmentStandard");

    public static final String MOD_ID = "equipment_standard";

    public static Config CONFIG;

    public static void init() {
        Config.HANDLER.load();
        CONFIG = Config.HANDLER.instance();
        ESAttributes.register();
        ESBlocks.register();
        ESItems.register();
        ESMenu.register();
        registerReloadListener();
    }

    private static void registerReloadListener() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new TemplateDataLoader());
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new AttributeScoreLoader());
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ItemRarityLoader());
    }

    public static void debug(String var1, Object... var2) {
        if (CONFIG.debug) {
            LOGGER.info(var1, var2);
        }
    }

    public static ResourceLocation createResourceLocation(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    public static int nextBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
}
