package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.client.renderer.CollectorWireframeRenderer;
import com.misterd.mobflowutilities.network.CollectorXpPacket;
import com.misterd.mobflowutilities.network.ConfigPacket;
import com.misterd.mobflowutilities.network.OpenFilterPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

public class CollectorScreen extends AbstractContainerScreen<CollectorMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("mobflowutilities", "textures/gui/collector_gui.png");
    private static final WidgetSprites REDUCE_OFFSET_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn_hover"));
    private static final WidgetSprites INCREASE_OFFSET_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn_hover"));
    private static final WidgetSprites TOGGLE_WIREFRAME_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover"));
    private static final WidgetSprites WITHDRAW_XP_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "withdraw_xp_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "withdraw_xp_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "withdraw_xp_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "withdraw_xp_btn_hover"));
    private static final WidgetSprites DEPOSIT_XP_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "deposit_xp_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "deposit_xp_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "deposit_xp_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "deposit_xp_btn_hover"));
    private static final WidgetSprites WITHDRAW_ALL_XP_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "withdraw_all_xp_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "withdraw_all_xp_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "withdraw_all_xp_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "withdraw_all_xp_btn_hover"));
    private static final WidgetSprites DEPOSIT_ALL_XP_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "deposit_all_xp_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "deposit_all_xp_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "deposit_all_xp_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "deposit_all_xp_btn_hover"));
    private static final WidgetSprites RESET_OFFSET_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn_hover"));
    private static final WidgetSprites EDIT_FILTER_SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", "edit_filter_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "edit_filter_btn"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "edit_filter_btn_hover"), ResourceLocation.fromNamespaceAndPath("mobflowutilities", "edit_filter_btn_hover"));

    private EditBox xpInputField;
    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;
    private boolean showWireframe = false;
    private boolean xpCollectionEnabled = false;
    private int storedXP = 0;
    private int maxStoredXP = Integer.MAX_VALUE;

    public CollectorScreen(CollectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 244;
        this.imageWidth = 234;
        this.inventoryLabelY = this.imageHeight - 96;
        this.inventoryLabelX = 36;
        this.syncFromBlockEntity();
    }

    private void syncFromBlockEntity() {
        if ((this.menu).blockEntity != null) {
            this.downUpOffset = (this.menu).blockEntity.getDownUpOffset();
            this.northSouthOffset = (this.menu).blockEntity.getNorthSouthOffset();
            this.eastWestOffset = (this.menu).blockEntity.getEastWestOffset();
            this.xpCollectionEnabled = (this.menu).blockEntity.isXpCollectionEnabled();
            this.storedXP = (this.menu).blockEntity.getStoredXP();
        }
    }

    private int xpToLevel(int xp) {
        if (xp < 0) {
            return 0;
        } else {
            int level = 0;

            for(int remaining = xp; remaining > 0; ++level) {
                int xpForNextLevel = this.getXpNeededForLevel(level);
                if (remaining < xpForNextLevel) {
                    break;
                }

                remaining -= xpForNextLevel;
            }

            return level;
        }
    }

    private int getXpNeededForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 16 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    private int levelToXp(int level) {
        if (level <= 0) {
            return 0;
        } else {
            int totalXp = 0;

            for(int i = 0; i < level; ++i) {
                totalXp += this.getXpNeededForLevel(i);
            }

            return totalXp;
        }
    }

    protected void init() {
        super.init();
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;
        this.clearWidgets();
        this.xpInputField = new EditBox(this.font, leftPos + 9, topPos + 113, 39, 8, Component.translatable("gui.mobflowutilities.collector.xp_input_levels"));
        this.xpInputField.setMaxLength(4);
        this.xpInputField.setValue("0");
        this.xpInputField.setBordered(false);
        this.xpInputField.setTextColor(16777215);
        this.xpInputField.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.xp_input_levels")));
        this.addRenderableWidget(this.xpInputField);
        this.addOffsetButtons(leftPos, topPos);
        this.addWireframeButton(leftPos, topPos);
        this.addXpButtons(leftPos, topPos);
        this.addFilterEditButtons(leftPos, topPos);
    }

    private void addOffsetButtons(int leftPos, int topPos) {
        ImageButton duDecreaseButton = new ImageButton(leftPos + 179, topPos + 68, 10, 10, REDUCE_OFFSET_SPRITES, button -> this.adjustOffset("downUp", -1));
        duDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.down_up.decrease")));
        this.addRenderableWidget(duDecreaseButton);

        ImageButton duIncreaseButton = new ImageButton(leftPos + 211, topPos + 68, 10, 10, INCREASE_OFFSET_SPRITES, button -> this.adjustOffset("downUp", 1));
        duIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.down_up.increase")));
        this.addRenderableWidget(duIncreaseButton);

        ImageButton nsDecreaseButton = new ImageButton(leftPos + 179, topPos + 90, 10, 10, REDUCE_OFFSET_SPRITES, button -> this.adjustOffset("northSouth", -1));
        nsDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.north_south.decrease")));
        this.addRenderableWidget(nsDecreaseButton);

        ImageButton nsIncreaseButton = new ImageButton(leftPos + 211, topPos + 90, 10, 10, INCREASE_OFFSET_SPRITES, button -> this.adjustOffset("northSouth", 1));
        nsIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.north_south.increase")));
        this.addRenderableWidget(nsIncreaseButton);

        ImageButton ewDecreaseButton = new ImageButton(leftPos + 179, topPos + 112, 10, 10, REDUCE_OFFSET_SPRITES, button -> this.adjustOffset("eastWest", -1));
        ewDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.east_west.decrease")));
        this.addRenderableWidget(ewDecreaseButton);

        ImageButton ewIncreaseButton = new ImageButton(leftPos + 211, topPos + 112, 10, 10, INCREASE_OFFSET_SPRITES, button -> this.adjustOffset("eastWest", 1));
        ewIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.east_west.increase")));
        this.addRenderableWidget(ewIncreaseButton);

        ImageButton resetOffsetButton = new ImageButton(leftPos + 194, topPos + 129, 12, 12, RESET_OFFSET_SPRITES, button -> this.resetAllOffsets());
        resetOffsetButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.reset_all")));
        this.addRenderableWidget(resetOffsetButton);
    }

    private void addWireframeButton(int leftPos, int topPos) {
        ImageButton wireframeButton = new ImageButton(
                leftPos + 155,
                topPos + 112,
                10,
                10,
                this.createConditionalSprites(TOGGLE_WIREFRAME_SPRITES, () -> this.showWireframe),
                button -> this.toggleWireframe()
        );
        wireframeButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.wireframe_toggle")));
        this.addRenderableWidget(wireframeButton);
    }

    private void addXpButtons(int leftPos, int topPos) {
        ImageButton withdrawButton = new ImageButton(leftPos + 49, topPos + 112, 10, 10, WITHDRAW_XP_SPRITES, button -> this.withdrawXP());
        withdrawButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.xp.withdraw")));
        this.addRenderableWidget(withdrawButton);

        ImageButton depositButton = new ImageButton(leftPos + 60, topPos + 112, 10, 10, DEPOSIT_XP_SPRITES, button -> this.depositXP());
        depositButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.xp.deposit")));
        this.addRenderableWidget(depositButton);

        ImageButton withdrawAllButton = new ImageButton(leftPos + 71, topPos + 112, 10, 10, WITHDRAW_ALL_XP_SPRITES, button -> this.withdrawAllXP());
        withdrawAllButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.xp.withdraw_all")));
        this.addRenderableWidget(withdrawAllButton);

        ImageButton depositAllButton = new ImageButton(leftPos + 82, topPos + 112, 10, 10, DEPOSIT_ALL_XP_SPRITES, button -> this.depositAllXP());
        depositAllButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.xp.deposit_all")));
        this.addRenderableWidget(depositAllButton);
    }

    private void addFilterEditButtons(int leftPos, int topPos) {
        // Filter slot 1 edit button (module slot index 1)
        ImageButton editFilter1Button = new ImageButton(
                leftPos + 177,
                topPos + 5,
                10,
                10,
                EDIT_FILTER_SPRITES,
                button -> this.openFilterEditor(1)
        );
        editFilter1Button.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.edit_filter")));
        this.addRenderableWidget(editFilter1Button);

        // Filter slot 2 edit button (module slot index 2)
        ImageButton editFilter2Button = new ImageButton(
                leftPos + 195,
                topPos + 5,
                10,
                10,
                EDIT_FILTER_SPRITES,
                button -> this.openFilterEditor(2)
        );
        editFilter2Button.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.edit_filter")));
        this.addRenderableWidget(editFilter2Button);

        // Filter slot 3 edit button (module slot index 3)
        ImageButton editFilter3Button = new ImageButton(
                leftPos + 213,
                topPos + 5,
                10,
                10,
                EDIT_FILTER_SPRITES,
                button -> this.openFilterEditor(3)
        );
        editFilter3Button.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.edit_filter")));
        this.addRenderableWidget(editFilter3Button);
    }

    private void openFilterEditor(int filterSlotIndex) {
        PacketDistributor.sendToServer(
                new OpenFilterPacket(this.menu.blockEntity.getBlockPos(), filterSlotIndex)
        );
    }

    private WidgetSprites createConditionalSprites(WidgetSprites baseSprites, Supplier<Boolean> isActive) {
        return isActive.get() ? new WidgetSprites(ResourceLocation.fromNamespaceAndPath("mobflowutilities", baseSprites.enabled().getPath() + "_active"), baseSprites.disabled(), ResourceLocation.fromNamespaceAndPath("mobflowutilities", baseSprites.enabledFocused().getPath().replace("_hover", "_active")), baseSprites.disabledFocused()) : baseSprites;
    }

    private void adjustOffset(String axis, int delta) {
        ConfigPacket.ConfigType configType;
        int newValue;

        switch (axis) {
            case "downUp" -> {
                configType = ConfigPacket.ConfigType.COLLECTOR_DOWN_UP_OFFSET;
                newValue = Math.max(-10, Math.min(10, this.downUpOffset + delta));
                this.downUpOffset = newValue;
            }
            case "northSouth" -> {
                configType = ConfigPacket.ConfigType.COLLECTOR_NORTH_SOUTH_OFFSET;
                newValue = Math.max(-10, Math.min(10, this.northSouthOffset + delta));
                this.northSouthOffset = newValue;
            }
            case "eastWest" -> {
                configType = ConfigPacket.ConfigType.COLLECTOR_EAST_WEST_OFFSET;
                newValue = Math.max(-10, Math.min(10, this.eastWestOffset + delta));
                this.eastWestOffset = newValue;
            }
            default -> {
                return;
            }
        }

        PacketDistributor.sendToServer(
                new ConfigPacket(
                        ConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                        this.menu.blockEntity.getBlockPos(),
                        configType,
                        newValue,
                        false
                )
        );
    }

    private void toggleWireframe() {
        this.showWireframe = !this.showWireframe;
        CollectorWireframeRenderer.toggleWireframe((this.menu).blockEntity.getBlockPos());
        this.init();
    }

    private void resetAllOffsets() {
        this.downUpOffset = 0;
        this.northSouthOffset = 0;
        this.eastWestOffset = 0;

        PacketDistributor.sendToServer(new ConfigPacket(
                ConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                this.menu.blockEntity.getBlockPos(),
                ConfigPacket.ConfigType.COLLECTOR_DOWN_UP_OFFSET,
                0,
                false
        ));

        PacketDistributor.sendToServer(new ConfigPacket(
                ConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                this.menu.blockEntity.getBlockPos(),
                ConfigPacket.ConfigType.COLLECTOR_NORTH_SOUTH_OFFSET,
                0,
                false
        ));

        PacketDistributor.sendToServer(new ConfigPacket(
                ConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                this.menu.blockEntity.getBlockPos(),
                ConfigPacket.ConfigType.COLLECTOR_EAST_WEST_OFFSET,
                0,
                false
        ));
    }

    private void withdrawXP() {
        try {
            int levels = Integer.parseInt(this.xpInputField.getValue());
            if (levels > 0) {
                int xpAmount = this.levelToXp(levels);
                if (xpAmount <= this.storedXP) {
                    PacketDistributor.sendToServer(
                            new CollectorXpPacket(this.menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.WITHDRAW, xpAmount)
                    );
                    this.storedXP -= xpAmount;
                    this.xpInputField.setValue("0");
                }
            }
        } catch (NumberFormatException e) {
            this.xpInputField.setValue("0");
        }
    }

    private void depositXP() {
        try {
            int levels = Integer.parseInt(this.xpInputField.getValue());
            if (levels > 0) {
                int xpAmount = this.levelToXp(levels);
                PacketDistributor.sendToServer(
                        new CollectorXpPacket(this.menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.DEPOSIT, xpAmount)
                );
                this.storedXP = Math.min(this.maxStoredXP, this.storedXP + xpAmount);
                this.xpInputField.setValue("0");
            }
        } catch (NumberFormatException e) {
            this.xpInputField.setValue("0");
        }
    }

    private void withdrawAllXP() {
        if (this.storedXP > 0) {
            PacketDistributor.sendToServer(
                    new CollectorXpPacket(this.menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.WITHDRAW, this.storedXP)
            );
            this.storedXP = 0;
            this.xpInputField.setValue("0");
        }
    }

    private void depositAllXP() {
        int playerXP = this.minecraft.player.totalExperience;
        if (playerXP > 0) {
            PacketDistributor.sendToServer(
                    new CollectorXpPacket(this.menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.DEPOSIT, playerXP)
            );
            this.storedXP = Math.min(this.maxStoredXP, this.storedXP + playerXP);
            this.xpInputField.setValue("0");
        }
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        this.renderOffsetValues(guiGraphics, x, y);
        this.renderXPCollectionToggle(guiGraphics, x, y);
        this.renderXPDisplay(guiGraphics, x, y);
    }

    private void renderOffsetValues(GuiGraphics guiGraphics, int x, int y) {
        PoseStack poseStack = guiGraphics.pose();
        float scale = 0.65F;

        // Draw offset values
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);

        guiGraphics.drawString(this.font, (this.downUpOffset >= 0 ? "+" : "") + this.downUpOffset,
                (int) ((x + 194) / scale), (int) ((y + 71) / scale), 0, false);
        guiGraphics.drawString(this.font, (this.northSouthOffset >= 0 ? "+" : "") + this.northSouthOffset,
                (int) ((x + 194) / scale), (int) ((y + 93) / scale), 0, false);
        guiGraphics.drawString(this.font, (this.eastWestOffset >= 0 ? "+" : "") + this.eastWestOffset,
                (int) ((x + 194) / scale), (int) ((y + 115) / scale), 0, false);

        poseStack.popPose();

        // Draw offset labels
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);

        guiGraphics.drawString(this.font, Component.translatable("gui.mobflowutilities.collector.offset.down_up").getString(),
                (int) ((x + 182) / scale), (int) ((y + 60) / scale), 0, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.mobflowutilities.collector.offset.north_south").getString(),
                (int) ((x + 182) / scale), (int) ((y + 82) / scale), 0, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.mobflowutilities.collector.offset.east_west").getString(),
                (int) ((x + 182) / scale), (int) ((y + 104) / scale), 0, false);

        poseStack.popPose();
    }

    private void renderXPCollectionToggle(GuiGraphics guiGraphics, int x, int y) {
        ResourceLocation toggleHandle = ResourceLocation.fromNamespaceAndPath("mobflowutilities", "toggle_scroller_handle");
        int handleX = this.xpCollectionEnabled ? x + 125 : x + 117;
        int handleY = y + 112;
        guiGraphics.blitSprite(toggleHandle, handleX, handleY, 6, 10);
    }

    private void renderXPDisplay(GuiGraphics guiGraphics, int x, int y) {
        PoseStack poseStack = guiGraphics.pose();
        float scale = 0.65F;
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);
        int storedLevels = this.xpToLevel(this.storedXP);
        String xpDisplayText = String.format(Component.translatable("gui.mobflowutilities.collector.xp_levels_stored").getString(), storedLevels);
        guiGraphics.drawString(this.font, xpDisplayText, (int)((float)(x + 12) / scale), (int)((float)(y + 132) / scale), 2338116, false);
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (mouseX >= x + 118 && mouseX <= x + 134 && mouseY >= y + 113 && mouseY <= y + 121) {
            this.xpCollectionEnabled = !this.xpCollectionEnabled;
            PacketDistributor.sendToServer(
                    new ConfigPacket(
                            ConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                            this.menu.blockEntity.getBlockPos(),
                            ConfigPacket.ConfigType.COLLECTOR_XP_COLLECTION_TOGGLE,
                            0,
                            this.xpCollectionEnabled
                    )
            );
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        this.syncFromBlockEntity();
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        this.renderCustomTooltips(pGuiGraphics, mouseX, mouseY);
        this.renderTooltip(pGuiGraphics, mouseX, mouseY);
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Tooltip for XP collection toggle
        if (mouseX >= x + 117 && mouseX <= x + 131 && mouseY >= y + 113 && mouseY <= y + 130) {
            Component tooltipText = this.xpCollectionEnabled
                    ? Component.translatable("tooltip.mobflowutilities.collector.xp_collection.enabled")
                    : Component.translatable("tooltip.mobflowutilities.collector.xp_collection.disabled");
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        // Tooltip for XP stored display
        float scale = 0.8F;
        int displayWidth = (int) (this.font.width(this.getXpDisplayText()) * scale);
        int displayHeight = (int) (9.0F * scale);
        int actualX = x + 9;
        int actualY = y + 129;
        if (mouseX >= actualX && mouseX <= actualX + displayWidth && mouseY >= actualY && mouseY <= actualY + displayHeight) {
            MutableComponent tooltipText = Component.translatable(
                    "tooltip.mobflowutilities.collector.xp_display",
                    String.format("%,d", this.storedXP)
            );
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        // Tooltip for Down/Up offset
        if (mouseX >= x + 192 && mouseX <= x + 204 && mouseY >= y + 68 && mouseY <= y + 76) {
            MutableComponent tooltipText = Component.translatable(
                    "tooltip.mobflowutilities.collector.offset.down_up.value",
                    this.downUpOffset
            );
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        // Tooltip for North/South offset
        if (mouseX >= x + 192 && mouseX <= x + 204 && mouseY >= y + 90 && mouseY <= y + 98) {
            MutableComponent tooltipText = Component.translatable(
                    "tooltip.mobflowutilities.collector.offset.north_south.value",
                    this.northSouthOffset
            );
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        // Tooltip for East/West offset
        if (mouseX >= x + 192 && mouseX <= x + 204 && mouseY >= y + 112 && mouseY <= y + 120) {
            MutableComponent tooltipText = Component.translatable(
                    "tooltip.mobflowutilities.collector.offset.east_west.value",
                    this.eastWestOffset
            );
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }
    }

    private String getXpDisplayText() {
        int storedLevels = this.xpToLevel(this.storedXP);
        return String.format("Levels Stored: %,d", storedLevels);
    }

    protected void containerTick() {
        super.containerTick();
        this.syncFromBlockEntity();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Custom color for your screen's title
        guiGraphics.drawString(
                this.font,
                this.title,
                this.titleLabelX,
                this.titleLabelY,
                0xF2F2F2,
                false
        );

        // Custom color for the player's inventory label
        guiGraphics.drawString(
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                0xF2F2F2,
                false
        );
    }
}