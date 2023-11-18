package me.langyue.equipmentstandard.client.gui.screens.inventory;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.client.gui.components.IconButton;
import me.langyue.equipmentstandard.world.inventory.ReforgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ReforgeScreen extends AbstractContainerScreen<ReforgeMenu> {
    private static final ResourceLocation BG = EquipmentStandard.createResourceLocation("textures/gui/container/reforge.png");

    private Button reforgeButton;
    private Button costIcon;
    private StringWidget cost;

    public ReforgeScreen(ReforgeMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.reforgeButton = this.addRenderableWidget(new IconButton(i + 79, j + 54, 18, 18, 18, 166, BG, button -> {
            if (this.menu.clickMenuButton(this.minecraft.player, 0)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
        }));
        this.reforgeButton.setTooltip(Tooltip.create(Component.translatable("container.reforge.tooltip")));
        this.reforgeButton.active = false;
        this.costIcon = this.addRenderableOnly(new IconButton(this.leftPos + 133, this.topPos + 49, 18, 18, 0, 166, BG, button -> {
        }));
        this.costIcon.active = false;
        this.cost = this.addRenderableOnly(new StringWidget(this.leftPos + 133, this.topPos + 49, 18, 30, Component.literal("0"), this.minecraft.font));
        this.cost.alignRight();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.reforgeButton.active = this.menu.canReforge(this.minecraft.player);
        this.costIcon.active = this.menu.getCost() > 0;
        this.cost.setMessage(Component.literal(String.valueOf(this.menu.getCost())));
        this.costIcon.setTooltip(Tooltip.create(Component.translatable("container.reforge.cost.tooltip", this.menu.getCost())));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        guiGraphics.blit(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        f = this.minecraft.getFrameTime();
        super.render(guiGraphics, i, j, f);
        this.renderTooltip(guiGraphics, i, j);
    }
}
