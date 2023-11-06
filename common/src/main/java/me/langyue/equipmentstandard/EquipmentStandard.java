package me.langyue.equipmentstandard;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.ReloadListenerRegistry;
import io.netty.buffer.Unpooled;
import me.langyue.equipmentstandard.api.CustomAttributes;
import me.langyue.equipmentstandard.config.Config;
import me.langyue.equipmentstandard.data.AttributeScoreLoader;
import me.langyue.equipmentstandard.data.ItemRarityLoader;
import me.langyue.equipmentstandard.data.TemplateDataLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class EquipmentStandard {
    public static final Logger LOGGER = LoggerFactory.getLogger("EquipmentStandard");

    public static final String MOD_ID = "equipment_standard";

    public static final RandomSource RANDOM = RandomSource.create();

    public static Config CONFIG;

    public static final ResourceLocation NET_READY = createResourceLocation("ready");

    public static void init() {
        Config.init();
        CustomAttributes.ATTRIBUTE.register();
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new TemplateDataLoader());
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new AttributeScoreLoader());
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ItemRarityLoader());
        // 服务端收到通知开始同步血量
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NET_READY, (buf, context) -> {
            if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
                debug("NET_READY");
                serverPlayer.resetSentInfo();
            }
        });
    }

    public static void debug(String var1, Object... var2) {
        if (CONFIG.debug) {
            LOGGER.info(var1, var2);
        }
    }

    public static ResourceLocation createResourceLocation(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    public static int nextBetween(Integer min, Integer max) {
        if (min == null) min = 0;
        if (max == null || max <= 0 || max == Integer.MAX_VALUE) max = Integer.MAX_VALUE - Math.abs(min);
        synchronized (RANDOM) {
            int nexted = RANDOM.nextIntBetweenInclusive(0, max - min);
            return nexted + min;
        }
    }
}
