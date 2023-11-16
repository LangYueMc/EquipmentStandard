package me.langyue.equipmentstandard.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconButton extends Button {
    protected ResourceLocation texture;
    protected int posX;
    protected int posY;


    public IconButton(int x, int y, int width, int height, int posX, int posY, ResourceLocation texture, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float d) {
        int posY = this.posY;
        if (this.isActive()) {
            posY += this.height;
            if (this.isHoveredOrFocused()) {
                posY += this.height;
            }
        }
        RenderSystem.enableDepthTest();
        guiGraphics.blit(texture, this.getX(), this.getY(), this.posX, posY, this.width, this.height);
    }
}
