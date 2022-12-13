package me.langyue.equipmentstandard.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacket {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ClientPacket.READY, (server, player, handler, buffer, sender) -> {
            server.execute(player::markHealthDirty);
        });
    }

}
