package me.langyue.equipmentstandard.stat;

import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Proficiency {

    private static final String id = "proficiency";
    public static final Identifier ID = EquipmentStandard.createIdentifier(id);
    private static Stat<Identifier> STAT;

    public static void init() {
        Registry.register(Registry.CUSTOM_STAT, id, ID);
        STAT = Stats.CUSTOM.getOrCreateStat(ID, StatFormatter.DEFAULT);
    }

    public static void increment(PlayerEntity player) {
        player.incrementStat(ID);
    }

    public static int get(PlayerEntity player) {
        StatHandler statHandler;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            statHandler = serverPlayer.getStatHandler();
        } else if (player instanceof ClientPlayerEntity clientPlayer) {
            statHandler = clientPlayer.getStatHandler();
        } else {
            return 0;
        }
        return statHandler.getStat(STAT);
    }
}
