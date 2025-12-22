package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.client.renderer.FanWireframeRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Supplier;

public class FanScreen extends AbstractContainerScreen<FanMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "textures/gui/fan_gui.png");

    private static final WidgetSprites TOGGLE_WIREFRAME_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover")
    );

    private boolean showWireframe = false;

    public FanScreen(FanMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 136;
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        this.clearWidgets();
        this.addWireframeButton(leftPos, topPos);
        this.syncFromBlockEntity();
    }

    private void addWireframeButton(int leftPos, int topPos) {
        ImageButton wireframeButton = new ImageButton(
                leftPos + 155,
                topPos + 21,
                12,
                12,
                this.createConditionalSprites(TOGGLE_WIREFRAME_SPRITES, () -> this.showWireframe),
                button -> this.toggleWireframe()
        );
        wireframeButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.fan.wireframe_toggle")));
        this.addRenderableWidget(wireframeButton);
    }

    private WidgetSprites createConditionalSprites(WidgetSprites baseSprites, Supplier<Boolean> isActive) {
        return isActive.get() ?
                new WidgetSprites(
                        ResourceLocation.fromNamespaceAndPath("mobflowutilities", baseSprites.enabled().getPath() + "_active"),
                        baseSprites.disabled(),
                        ResourceLocation.fromNamespaceAndPath("mobflowutilities", baseSprites.enabledFocused().getPath().replace("_hover", "_active")),
                        baseSprites.disabledFocused()
                ) : baseSprites;
    }

    private void toggleWireframe() {
        this.showWireframe = !this.showWireframe;
        FanWireframeRenderer.toggleWireframe(this.menu.blockEntity.getBlockPos());
        this.init();
    }

    private void syncFromBlockEntity() {
        if (this.menu.blockEntity != null) {
            this.showWireframe = FanWireframeRenderer.isWireframeActive(this.menu.blockEntity.getBlockPos());
        }
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.syncFromBlockEntity();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}