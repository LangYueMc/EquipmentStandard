package me.langyue.equipmentstandard.forge;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.client.EquipmentStandardClient;
import me.langyue.equipmentstandard.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = EquipmentStandard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EquipmentStandardForgeClient {
    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        EquipmentStandardClient.init();
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, screen) -> Config.generateScreen(screen)
                )
        );
    }
}