package me.langyue.equipmentstandard.fabric;

import me.langyue.equipmentstandard.client.EquipmentStandardClient;
import net.fabricmc.api.ClientModInitializer;

public class EquipmentStandardFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EquipmentStandardClient.init();
    }
}