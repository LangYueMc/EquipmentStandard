package me.langyue.equipmentstandard;

import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.autotag.AutoItemTags;
import me.langyue.equipmentstandard.config.Config;
import me.langyue.equipmentstandard.data.TemplateDataLoader;
import me.langyue.equipmentstandard.network.ServerPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EquipmentStandard implements ModInitializer {

    public static final String MOD_ID = "equipment_standard";
    public static final Logger LOGGER = LogManager.getLogger("EquipmentStandard");
    public static final Random RANDOM = Random.create();

    public static Config CONFIG;

    @Override
    public void onInitialize() {
        Config.init();
        ServerPacket.init();
        if (!FabricLoader.getInstance().isModLoaded("autotag")) AutoItemTags.register();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TemplateDataLoader());
        ModifyItemAttributeModifiersCallback.EVENT.register(ModifierUtils::modify);
    }

    public static Identifier createIdentifier(String id) {
        return new Identifier(MOD_ID, id);
    }
}
