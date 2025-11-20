package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.client.renderer.GenesisChamberWireframeRenderer;
import com.misterd.mobflowutilities.network.ConfigPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

public class GenesisChamberScreen extends AbstractContainerScreen<GenesisChamberMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "textures/gui/genesis_chamber_gui.png");

    private static final WidgetSprites REDUCE_OFFSET_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn_hover"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn_hover")
    );

    private static final WidgetSprites INCREASE_OFFSET_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn_hover"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn_hover")
    );

    private static final WidgetSprites RESET_OFFSET_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn_hover"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn_hover")
    );

    private static final WidgetSprites TOGGLE_WIREFRAME_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover"),
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover")
    );

    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;
    private boolean showWireframe = false;

    public GenesisChamberScreen(GenesisChamberMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 189;
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        this.clearWidgets();
        this.addOffsetButtons(leftPos, topPos);
        this.addWireframeButton(leftPos, topPos);

        this.syncFromBlockEntity();
    }

    private void addOffsetButtons(int leftPos, int topPos) {
        ImageButton duDecreaseButton = new ImageButton(
                leftPos + 121, topPos + 17, 10, 10,
                REDUCE_OFFSET_SPRITES,
                button -> this.adjustOffset("downUp", -1)
        );
        duDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.down_up.decrease")));
        this.addRenderableWidget(duDecreaseButton);

        ImageButton duIncreaseButton = new ImageButton(
                leftPos + 153, topPos + 17, 10, 10,
                INCREASE_OFFSET_SPRITES,
                button -> this.adjustOffset("downUp", 1)
        );
        duIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.down_up.increase")));
        this.addRenderableWidget(duIncreaseButton);

        ImageButton nsDecreaseButton = new ImageButton(
                leftPos + 121, topPos + 39, 10, 10,
                REDUCE_OFFSET_SPRITES,
                button -> this.adjustOffset("northSouth", -1)
        );
        nsDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.north_south.decrease")));
        this.addRenderableWidget(nsDecreaseButton);

        ImageButton nsIncreaseButton = new ImageButton(
                leftPos + 153, topPos + 39, 10, 10,
                INCREASE_OFFSET_SPRITES,
                button -> this.adjustOffset("northSouth", 1)
        );
        nsIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.north_south.increase")));
        this.addRenderableWidget(nsIncreaseButton);

        ImageButton ewDecreaseButton = new ImageButton(
                leftPos + 121, topPos + 61, 10, 10,
                REDUCE_OFFSET_SPRITES,
                button -> this.adjustOffset("eastWest", -1)
        );
        ewDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.east_west.decrease")));
        this.addRenderableWidget(ewDecreaseButton);

        ImageButton ewIncreaseButton = new ImageButton(
                leftPos + 153, topPos + 61, 10, 10,
                INCREASE_OFFSET_SPRITES,
                button -> this.adjustOffset("eastWest", 1)
        );
        ewIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.east_west.increase")));
        this.addRenderableWidget(ewIncreaseButton);

        ImageButton resetOffsetButton = new ImageButton(
                leftPos + 136, topPos + 73, 12, 12,
                RESET_OFFSET_SPRITES,
                button -> this.resetAllOffsets()
        );
        resetOffsetButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.reset_all")));
        this.addRenderableWidget(resetOffsetButton);
    }

    private void addWireframeButton(int leftPos, int topPos) {
        ImageButton wireframeButton = new ImageButton(
                leftPos + 56, topPos + 74, 10, 10,
                this.createConditionalSprites(TOGGLE_WIREFRAME_SPRITES, () -> this.showWireframe),
                button -> this.toggleWireframe()
        );
        wireframeButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.genesis_chamber.wireframe_toggle")));
        this.addRenderableWidget(wireframeButton);
    }

    private WidgetSprites createConditionalSprites(WidgetSprites baseSprites, Supplier<Boolean> condition) {
        return condition.get()
                ? new WidgetSprites(
                ResourceLocation.fromNamespaceAndPath("mobflowutilities", baseSprites.enabled().getPath() + "_active"),
                baseSprites.disabled(),
                ResourceLocation.fromNamespaceAndPath("mobflowutilities", baseSprites.enabledFocused().getPath().replace("_hover", "_active")),
                baseSprites.disabledFocused()
        )
                : baseSprites;
    }

    private void adjustOffset(String axis, int delta) {
        ConfigPacket.ConfigType configType;
        int newValue;

        switch (axis) {
            case "downUp" -> {
                configType = ConfigPacket.ConfigType.GENESIS_CHAMBER_DOWN_UP_OFFSET;
                newValue = Math.max(-10, Math.min(10, this.downUpOffset + delta));
                this.downUpOffset = newValue;
            }
            case "northSouth" -> {
                configType = ConfigPacket.ConfigType.GENESIS_CHAMBER_NORTH_SOUTH_OFFSET;
                newValue = Math.max(-10, Math.min(10, this.northSouthOffset + delta));
                this.northSouthOffset = newValue;
            }
            case "eastWest" -> {
                configType = ConfigPacket.ConfigType.GENESIS_CHAMBER_EAST_WEST_OFFSET;
                newValue = Math.max(-10, Math.min(10, this.eastWestOffset + delta));
                this.eastWestOffset = newValue;
            }
            default -> {
                return;
            }
        }

        PacketDistributor.sendToServer(
                new ConfigPacket(
                        ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK,
                        this.menu.blockEntity.getBlockPos(),
                        configType,
                        newValue,
                        false
                )
        );
    }

    private void toggleWireframe() {
        this.showWireframe = !this.showWireframe;
        GenesisChamberWireframeRenderer.toggleWireframe(this.menu.blockEntity.getBlockPos());
        this.init();
    }

    private void resetAllOffsets() {
        this.downUpOffset = 0;
        this.northSouthOffset = 0;
        this.eastWestOffset = 0;

        PacketDistributor.sendToServer(new ConfigPacket(
                ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK,
                this.menu.blockEntity.getBlockPos(),
                ConfigPacket.ConfigType.GENESIS_CHAMBER_DOWN_UP_OFFSET,
                0,
                false
        ));

        PacketDistributor.sendToServer(new ConfigPacket(
                ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK,
                this.menu.blockEntity.getBlockPos(),
                ConfigPacket.ConfigType.GENESIS_CHAMBER_NORTH_SOUTH_OFFSET,
                0,
                false
        ));

        PacketDistributor.sendToServer(new ConfigPacket(
                ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK,
                this.menu.blockEntity.getBlockPos(),
                ConfigPacket.ConfigType.GENESIS_CHAMBER_EAST_WEST_OFFSET,
                0,
                false
        ));
    }

    private void syncFromBlockEntity() {
        if (this.menu.blockEntity != null) {
            this.downUpOffset = this.menu.blockEntity.getDownUpOffset();
            this.northSouthOffset = this.menu.blockEntity.getNorthSouthOffset();
            this.eastWestOffset = this.menu.blockEntity.getEastWestOffset();
            this.showWireframe = GenesisChamberWireframeRenderer.isWireframeActive(this.menu.blockEntity.getBlockPos());
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

        renderBurnProgress(guiGraphics, x, y);
        renderMobPreview(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderBurnProgress(GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.blockEntity != null) {
            int burnTime = this.menu.blockEntity.getBurnTime();
            int maxBurnTime = this.menu.blockEntity.getMaxBurnTime();

            if (maxBurnTime > 0) {
                int progress = (int) (13.0 * burnTime / maxBurnTime);

                if (progress > 0) {
                    guiGraphics.blit(GUI_TEXTURE,
                            x + 81, y + 50 + (13 - progress),
                            176, 13 - progress,
                            14, progress
                    );
                }
            }
        }
    }

    private void renderMobPreview(GuiGraphics guiGraphics, int screenX, int screenY, int mouseX, int mouseY) {
        if (this.menu.blockEntity == null) return;

        ItemStack eggStack = this.menu.blockEntity.inventory.getStackInSlot(0);
        if (!(eggStack.getItem() instanceof SpawnEggItem spawnEgg)) return;

        EntityType<?> entityType = spawnEgg.getType(eggStack);
        if (entityType == null) return;

        Entity entity = this.menu.blockEntity.getOrCreateRenderedEntity(entityType);
        if (entity == null) return;

        int centerX = screenX + 25;
        int centerY = screenY + 26;

        int scale = 17;

        if (entity instanceof LivingEntity livingEntity) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    guiGraphics,
                    centerX - 17,
                    centerY - 8,
                    centerX + 17,
                    centerY + 38,
                    scale,
                    0.0625F,
                    mouseX,
                    mouseY,
                    livingEntity
            );
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.syncFromBlockEntity();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(
                this.font,
                this.title,
                this.titleLabelX,
                this.titleLabelY,
                0xF2F2F2,
                false
        );

        guiGraphics.drawString(
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                0xF2F2F2,
                false
        );

        this.renderOffsetValues(guiGraphics, mouseX, mouseY);
    }

    private void renderOffsetValues(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        PoseStack poseStack = guiGraphics.pose();
        float scale = 0.7F;

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);

        guiGraphics.drawString(this.font,
                (this.downUpOffset >= 0 ? "+" : "") + this.downUpOffset,
                (int) (137 / scale), (int) (20 / scale), 0, false);

        guiGraphics.drawString(this.font,
                (this.northSouthOffset >= 0 ? "+" : "") + this.northSouthOffset,
                (int) (137 / scale), (int) (42 / scale), 0, false);

        guiGraphics.drawString(this.font,
                (this.eastWestOffset >= 0 ? "+" : "") + this.eastWestOffset,
                (int) (137 / scale), (int) (64 / scale), 0, false);

        guiGraphics.drawString(this.font, Component.translatable("gui.mobflowutilities.collector.offset.down_up").getString(),
                (int) ((124) / scale), (int) ((10) / scale), 0, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.mobflowutilities.collector.offset.north_south").getString(),
                (int) ((124) / scale), (int) ((32) / scale), 0, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.mobflowutilities.collector.offset.east_west").getString(),
                (int) ((124) / scale), (int) ((54) / scale), 0, false);

        poseStack.popPose();
    }
}