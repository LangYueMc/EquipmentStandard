package me.langyue.equipmentstandard.forge;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.client.EquipmentStandardClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = EquipmentStandard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod(EquipmentStandard.MOD_ID)
public class EquipmentStandardForgeClient {
    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        EquipmentStandardClient.init();
    }
}