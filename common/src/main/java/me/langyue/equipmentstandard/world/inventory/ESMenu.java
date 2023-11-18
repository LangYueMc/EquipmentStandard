package me.langyue.equipmentstandard.world.inventory;

import dev.architectury.registry.registries.DeferredRegister;
import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.client.gui.screens.inventory.ReforgeScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ESMenu {
    private static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(EquipmentStandard.MOD_ID, Registries.MENU);
    public static final MenuType<ReforgeMenu> REFORGE_TABLE_MENU = register("reforge_menu", () -> ReforgeMenu::create);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String id, Supplier<MenuType.MenuSupplier<T>> entry) {
        MenuType<ReforgeMenu> menuType = new MenuType<>(ReforgeMenu::create, FeatureFlags.DEFAULT_FLAGS);
        MENU_TYPE.register(id, () -> menuType);
        return (MenuType<T>) menuType;
    }

    public static void register() {
        MENU_TYPE.register();
    }

    @Environment(value = EnvType.CLIENT)
    public static void registerClient() {
        MenuScreens.register(REFORGE_TABLE_MENU, ReforgeScreen::new);
    }
}
