package com.misterd.mobflowutilities.gui.custom;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class VoidFilterScreen extends AbstractContainerScreen<VoidFilterMenu> {

    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath("mobflowutilities", "textures/gui/void_filter_gui.png");

    public VoidFilterScreen(VoidFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 208);
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                this.leftPos, this.topPos, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
    }
}