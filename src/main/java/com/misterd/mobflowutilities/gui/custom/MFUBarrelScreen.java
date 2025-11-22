package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MFUBarrelScreen extends AbstractContainerScreen<MFUBarrelMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "textures/gui/mfu_barrel_gui.png");

    public MFUBarrelScreen(MFUBarrelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 256;
        this.imageWidth = 256;
        this.inventoryLabelY = this.imageHeight - 92;
        this.inventoryLabelX = this.imageWidth - 209;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, pMouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, pMouseX, mouseY);
    }
}
