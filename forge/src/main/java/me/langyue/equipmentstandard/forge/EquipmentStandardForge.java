package me.langyue.equipmentstandard.forge;

import dev.architectury.platform.forge.EventBuses;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EquipmentStandard.MOD_ID)
public class EquipmentStandardForge {
    public EquipmentStandardForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(EquipmentStandard.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        EquipmentStandard.init();
    }
}