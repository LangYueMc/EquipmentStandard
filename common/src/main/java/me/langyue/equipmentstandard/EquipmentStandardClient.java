package me.langyue.equipmentstandard;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Timer;
import java.util.TimerTask;

public class EquipmentStandardClient {

    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            NetworkManager.sendToServer(EquipmentStandard.NET_READY, new FriendlyByteBuf(Unpooled.buffer()));
            new Timer().schedule(new TimerTask() {
                // 容错，但也不能绝对容错，等我找到客户端登陆时同步血量的地方再改吧
                public void run() {
                    NetworkManager.sendToServer(EquipmentStandard.NET_READY, new FriendlyByteBuf(Unpooled.buffer()));
                }
            }, 1000);
        });
    }
}
