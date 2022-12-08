package me.langyue.equipmentstandard.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EquipmentStandardClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
//        Proficiency.init();
    }
}
