package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GenesisInfuserScreen extends AbstractContainerScreen<GenesisInfuserMenu> {

    private static final Identifier GUI_TEXTURE =
            Identifier.fromNamespaceAndPath(MobFlowUtilities.MODID, "textures/gui/genesis_infuser_gui.png");

    private static final int GUI_W = 176;
    private static final int GUI_H = 172;

    private static final int TANK_X = 8;
    private static final int TANK_Y = 19;
    private static final int TANK_W = 16;
    private static final int TANK_H = 52;
    private static final int TANK_CAPACITY = 32000;

    private static final int PROGRESS_X = 85;
    private static final int PROGRESS_Y = 18;
    private static final int PROGRESS_W = 6;
    private static final int PROGRESS_H = 54;
    private static final int PROGRESS_TEX_X = 176;
    private static final int PROGRESS_TEX_Y = 0;

    public GenesisInfuserScreen(GenesisInfuserMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, GUI_W, GUI_H);
        this.inventoryLabelY = GUI_H - 94;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                this.leftPos, this.topPos, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);
        this.renderTank(graphics);
        this.renderProgress(graphics);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
    }

    private void renderProgress(GuiGraphicsExtractor graphics) {
        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (maxProgress <= 0 || progress <= 0) return;

        int filled = (int) ((float) PROGRESS_H * progress / maxProgress);
        if (filled <= 0) return;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y + PROGRESS_H - filled,
                (float) PROGRESS_TEX_X, (float) (PROGRESS_TEX_Y + PROGRESS_H - filled),
                PROGRESS_W, filled, 256, 256);
    }

    private void renderTank(GuiGraphicsExtractor graphics) {
        if (this.menu.blockEntity == null) return;
        int amount = this.menu.blockEntity.getFluidAmount();
        if (amount <= 0) return;

        FluidResource resource = this.menu.blockEntity.getFluidResource();
        if (resource.isEmpty()) return;

        FluidStack fluidStack = resource.toStack(amount);
        FluidState fluidState = resource.getFluid().defaultFluidState();
        FluidModel fluidModel = Minecraft.getInstance().getModelManager()
                .getFluidStateModelSet().get(fluidState);
        TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();

        FluidTintSource tintSource = fluidModel.fluidTintSource();
        int tint = tintSource != null ? tintSource.colorAsStack(fluidStack) : -1;
        int colorARGB = tint | 0xFF000000;

        int filled = (int) ((float) TANK_H * amount / TANK_CAPACITY);
        if (filled <= 0) filled = 1;
        if (filled > TANK_H) filled = TANK_H;

        int x = this.leftPos + TANK_X;
        int bottom = this.topPos + TANK_Y + TANK_H;

        int tileSize = 16;
        int remaining = filled;
        int y = bottom;
        while (remaining > 0) {
            int drawHeight = Math.min(tileSize, remaining);
            int drawY = y - drawHeight;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite,
                    x, drawY, TANK_W, drawHeight, colorARGB);
            remaining -= drawHeight;
            y -= drawHeight;
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (isOver(TANK_X, TANK_Y, TANK_W, TANK_H, mouseX, mouseY)) {
            int amount = this.menu.blockEntity != null ? this.menu.blockEntity.getFluidAmount() : 0;
            FluidResource resource = this.menu.blockEntity != null ? this.menu.blockEntity.getFluidResource() : FluidResource.EMPTY;
            NumberFormat fmt = NumberFormat.getInstance(Locale.US);
            if (amount > 0 && !resource.isEmpty()) {
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        resource.toStack(amount).getHoverName(),
                        Component.literal(fmt.format(amount) + " / " + fmt.format(TANK_CAPACITY) + " mB")
                                .withStyle(ChatFormatting.AQUA)
                ), mouseX, mouseY);
            } else {
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.translatable("tooltip.mobflowutilities.genesis_infuser.tank_empty")
                                .withStyle(ChatFormatting.GRAY),
                        Component.literal("0 / " + fmt.format(TANK_CAPACITY) + " mB")
                                .withStyle(ChatFormatting.GRAY)
                ), mouseX, mouseY);
            }
            return;
        }

        if (isOver(PROGRESS_X, PROGRESS_Y, PROGRESS_W, PROGRESS_H, mouseX, mouseY)) {
            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();
            float pct = maxProgress > 0 ? (float) progress / maxProgress * 100.0F : 0.0F;
            graphics.setComponentTooltipForNextFrame(this.font, List.of(
                    Component.translatable("tooltip.mobflowutilities.genesis_infuser.progress"),
                    Component.literal(String.format("%.1f%%", pct)).withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
            return;
        }

        super.extractTooltip(graphics, mouseX, mouseY);
    }

    private boolean isOver(int wx, int wy, int ww, int wh, int mx, int my) {
        return mx >= this.leftPos + wx && mx <= this.leftPos + wx + ww
                && my >= this.topPos + wy && my <= this.topPos + wy + wh;
    }
}