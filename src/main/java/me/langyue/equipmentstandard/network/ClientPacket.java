package me.langyue.equipmentstandard.network;

import io.netty.buffer.Unpooled;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientPacket {
    public static final Identifier READY = EquipmentStandard.createIdentifier("ready");

    public static void init() {
    }

    public static void onReady() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(READY, buf);
        if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().getNetworkHandler() != null)
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }
}
