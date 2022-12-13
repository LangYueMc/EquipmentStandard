package me.langyue.equipmentstandard.client;

import me.langyue.equipmentstandard.network.ClientPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EquipmentStandardClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPacket.init();
    }
}
