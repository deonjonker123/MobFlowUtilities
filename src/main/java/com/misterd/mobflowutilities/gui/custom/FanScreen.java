package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.client.renderer.FanWireframeRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Supplier;

public class FanScreen extends AbstractContainerScreen<FanMenu> {

    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(MobFlowUtilities.MODID, "textures/gui/fan_gui.png");
    private static final WidgetSprites TOGGLE_WIREFRAME_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover")
    );

    private boolean showWireframe = false;

    public FanScreen(FanMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 136);
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.syncFromBlockEntity();
        this.addWireframeButton(this.leftPos, this.topPos);
    }

    private void addWireframeButton(int leftPos, int topPos) {
        ImageButton wireframeButton = new ImageButton(
                leftPos + 155, topPos + 21, 12, 12,
                this.createConditionalSprites(TOGGLE_WIREFRAME_SPRITES, () -> this.showWireframe),
                button -> this.toggleWireframe()
        );
        wireframeButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.fan.wireframe_toggle")));
        this.addRenderableWidget(wireframeButton);
    }

    private WidgetSprites createConditionalSprites(WidgetSprites baseSprites, Supplier<Boolean> isActive) {
        return isActive.get()
                ? new WidgetSprites(
                Identifier.fromNamespaceAndPath("mobflowutilities", baseSprites.enabled().getPath() + "_active"),
                baseSprites.disabled(),
                Identifier.fromNamespaceAndPath("mobflowutilities", baseSprites.enabledFocused().getPath().replace("_hover", "_active")),
                baseSprites.disabledFocused())
                : baseSprites;
    }

    private void toggleWireframe() {
        this.showWireframe = !this.showWireframe;
        FanWireframeRenderer.toggleWireframe(this.menu.blockEntity.getBlockPos());
        this.init();
    }

    private void syncFromBlockEntity() {
        if (this.menu.blockEntity != null)
            this.showWireframe = FanWireframeRenderer.isWireframeActive(this.menu.blockEntity.getBlockPos());
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.syncFromBlockEntity();
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                this.leftPos, this.topPos, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
    }
}