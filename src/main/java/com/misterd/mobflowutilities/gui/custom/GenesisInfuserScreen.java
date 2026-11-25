package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.blockentity.custom.GenesisInfuserBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GenesisInfuserScreen extends AbstractContainerScreen<GenesisInfuserMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "textures/gui/genesis_infuser_gui.png");

    private static final int GUI_W = 176;
    private static final int GUI_H = 172;

    private static final int TANK_X = 8;
    private static final int TANK_Y = 19;
    private static final int TANK_W = 16;
    private static final int TANK_H = 52;

    private static final int PROGRESS_X = 85;
    private static final int PROGRESS_Y = 18;
    private static final int PROGRESS_W = 6;
    private static final int PROGRESS_H = 54;
    private static final int PROGRESS_TEX_X = 176;
    private static final int PROGRESS_TEX_Y = 0;

    public GenesisInfuserScreen(GenesisInfuserMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_W;
        this.imageHeight = GUI_H;
        this.inventoryLabelY = GUI_H - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        guiGraphics.blit(GUI_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        renderTank(guiGraphics);
        renderProgress(guiGraphics);
    }

    private void renderProgress(GuiGraphics guiGraphics) {
        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (maxProgress <= 0 || progress <= 0) return;

        int filled = (int) ((float) PROGRESS_H * progress / maxProgress);
        if (filled <= 0) return;

        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y + PROGRESS_H - filled,
                PROGRESS_TEX_X, PROGRESS_TEX_Y + PROGRESS_H - filled,
                PROGRESS_W, filled);
    }

    private void renderTank(GuiGraphics guiGraphics) {
        if (this.menu.blockEntity == null) return;
        FluidStack fluid = this.menu.blockEntity.getFluid();
        int amount = fluid.getAmount();
        if (fluid.isEmpty() || amount <= 0) return;

        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(extensions.getStillTexture(fluid));

        int tint = extensions.getTintColor(fluid);
        float red = ((tint >> 16) & 0xFF) / 255f;
        float green = ((tint >> 8) & 0xFF) / 255f;
        float blue = (tint & 0xFF) / 255f;

        int filled = (int) ((float) TANK_H * amount / GenesisInfuserBlockEntity.TANK_CAPACITY);
        if (filled <= 0) filled = 1;
        if (filled > TANK_H) filled = TANK_H;

        int x = this.leftPos + TANK_X;
        int y = this.topPos + TANK_Y + TANK_H;

        RenderSystem.setShaderColor(red, green, blue, 1.0F);
        int remaining = filled;
        while (remaining > 0) {
            int drawHeight = Math.min(16, remaining);
            y -= drawHeight;
            guiGraphics.blit(x, y, 0, TANK_W, drawHeight, sprite);
            remaining -= drawHeight;
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderCustomTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        NumberFormat fmt = NumberFormat.getInstance(Locale.US);

        if (isOver(TANK_X, TANK_Y, TANK_W, TANK_H, mouseX, mouseY)) {
            FluidStack fluid = this.menu.blockEntity != null ? this.menu.blockEntity.getFluid() : FluidStack.EMPTY;
            String capacity = fmt.format(GenesisInfuserBlockEntity.TANK_CAPACITY);
            if (!fluid.isEmpty()) {
                guiGraphics.renderComponentTooltip(this.font, List.of(
                        fluid.getHoverName(),
                        Component.literal(fmt.format(fluid.getAmount()) + " / " + capacity + " mB").withStyle(ChatFormatting.AQUA)
                ), mouseX, mouseY);
            } else {
                guiGraphics.renderComponentTooltip(this.font, List.of(
                        Component.translatable("tooltip.mobflowutilities.genesis_infuser.tank_empty").withStyle(ChatFormatting.GRAY),
                        Component.literal("0 / " + capacity + " mB").withStyle(ChatFormatting.GRAY)
                ), mouseX, mouseY);
            }
            return;
        }

        if (isOver(PROGRESS_X, PROGRESS_Y, PROGRESS_W, PROGRESS_H, mouseX, mouseY)) {
            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();
            float pct = maxProgress > 0 ? (float) progress / maxProgress * 100.0F : 0.0F;
            guiGraphics.renderComponentTooltip(this.font, List.of(
                    Component.translatable("tooltip.mobflowutilities.genesis_infuser.progress"),
                    Component.literal(String.format("%.1f%%", pct)).withStyle(ChatFormatting.GREEN)
            ), mouseX, mouseY);
        }
    }

    private boolean isOver(int wx, int wy, int ww, int wh, int mx, int my) {
        return mx >= this.leftPos + wx && mx <= this.leftPos + wx + ww
                && my >= this.topPos + wy && my <= this.topPos + wy + wh;
    }
}
