package me.langyue.equipmentstandard.fabric;

import me.langyue.equipmentstandard.EquipmentStandard;
import net.fabricmc.api.ModInitializer;

public class EquipmentStandardFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EquipmentStandard.init();
    }
}