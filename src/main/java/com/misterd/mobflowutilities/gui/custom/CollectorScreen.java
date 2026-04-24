package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.client.renderer.CollectorWireframeRenderer;
import com.misterd.mobflowutilities.network.CollectorXpPacket;
import com.misterd.mobflowutilities.network.ConfigPacket;
import com.misterd.mobflowutilities.network.OpenFilterPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public class CollectorScreen extends AbstractContainerScreen<CollectorMenu> {

    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath("mobflowutilities", "textures/gui/collector_gui.png");
    private static final WidgetSprites REDUCE_OFFSET_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn_hover"));
    private static final WidgetSprites INCREASE_OFFSET_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn_hover"));
    private static final WidgetSprites TOGGLE_WIREFRAME_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover"));
    private static final WidgetSprites WITHDRAW_XP_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "withdraw_xp_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "withdraw_xp_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "withdraw_xp_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "withdraw_xp_btn_hover"));
    private static final WidgetSprites DEPOSIT_XP_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "deposit_xp_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "deposit_xp_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "deposit_xp_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "deposit_xp_btn_hover"));
    private static final WidgetSprites WITHDRAW_ALL_XP_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "withdraw_all_xp_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "withdraw_all_xp_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "withdraw_all_xp_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "withdraw_all_xp_btn_hover"));
    private static final WidgetSprites DEPOSIT_ALL_XP_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "deposit_all_xp_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "deposit_all_xp_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "deposit_all_xp_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "deposit_all_xp_btn_hover"));
    private static final WidgetSprites RESET_OFFSET_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn_hover"));
    private static final WidgetSprites EDIT_FILTER_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath("mobflowutilities", "edit_filter_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "edit_filter_btn"), Identifier.fromNamespaceAndPath("mobflowutilities", "edit_filter_btn_hover"), Identifier.fromNamespaceAndPath("mobflowutilities", "edit_filter_btn_hover"));

    private static final int GUI_W = 234;
    private static final int GUI_H = 244;

    private EditBox xpInputField;
    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;
    private boolean showWireframe = false;
    private boolean xpCollectionEnabled = false;
    private int storedXP = 0;
    private int maxStoredXP = Integer.MAX_VALUE;

    public CollectorScreen(CollectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, GUI_W, GUI_H);
        this.inventoryLabelY = GUI_H - 96;
        this.inventoryLabelX = 36;
        this.syncFromBlockEntity();
    }

    private void syncFromBlockEntity() {
        if (this.menu.blockEntity != null) {
            this.downUpOffset = this.menu.blockEntity.getDownUpOffset();
            this.northSouthOffset = this.menu.blockEntity.getNorthSouthOffset();
            this.eastWestOffset = this.menu.blockEntity.getEastWestOffset();
            this.xpCollectionEnabled = this.menu.blockEntity.isXpCollectionEnabled();
            this.storedXP = this.menu.blockEntity.getStoredXP();
        }
    }

    private int xpToLevel(int xp) {
        if (xp < 0) return 0;
        int level = 0;
        for (int remaining = xp; remaining > 0; ++level) {
            int xpForNextLevel = getXpNeededForLevel(level);
            if (remaining < xpForNextLevel) break;
            remaining -= xpForNextLevel;
        }
        return level;
    }

    private int getXpNeededForLevel(int level) {
        if (level >= 30) return 112 + (level - 30) * 9;
        return level >= 16 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }

    private int levelToXp(int level) {
        if (level <= 0) return 0;
        int totalXp = 0;
        for (int i = 0; i < level; ++i) totalXp += getXpNeededForLevel(i);
        return totalXp;
    }

    @Override
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
        ImageButton wireframeButton = new ImageButton(leftPos + 155, topPos + 112, 10, 10,
                this.createConditionalSprites(TOGGLE_WIREFRAME_SPRITES, () -> this.showWireframe),
                button -> this.toggleWireframe());
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
        ImageButton editFilter1Button = new ImageButton(leftPos + 177, topPos + 5, 10, 10, EDIT_FILTER_SPRITES, button -> this.openFilterEditor(1));
        editFilter1Button.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.edit_filter")));
        this.addRenderableWidget(editFilter1Button);

        ImageButton editFilter2Button = new ImageButton(leftPos + 195, topPos + 5, 10, 10, EDIT_FILTER_SPRITES, button -> this.openFilterEditor(2));
        editFilter2Button.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.edit_filter")));
        this.addRenderableWidget(editFilter2Button);

        ImageButton editFilter3Button = new ImageButton(leftPos + 213, topPos + 5, 10, 10, EDIT_FILTER_SPRITES, button -> this.openFilterEditor(3));
        editFilter3Button.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.edit_filter")));
        this.addRenderableWidget(editFilter3Button);
    }

    private void openFilterEditor(int filterSlotIndex) {
        ClientPacketDistributor.sendToServer(new OpenFilterPacket(this.menu.blockEntity.getBlockPos(), filterSlotIndex));
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
            default -> { return; }
        }
        ClientPacketDistributor.sendToServer(new ConfigPacket(
                ConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                this.menu.blockEntity.getBlockPos(),
                configType,
                newValue,
                false
        ));
    }

    private void toggleWireframe() {
        this.showWireframe = !this.showWireframe;
        CollectorWireframeRenderer.toggleWireframe(this.menu.blockEntity.getBlockPos());
        this.init();
    }

    private void resetAllOffsets() {
        this.downUpOffset = 0;
        this.northSouthOffset = 0;
        this.eastWestOffset = 0;
        ClientPacketDistributor.sendToServer(new ConfigPacket(ConfigPacket.ConfigTarget.COLLECTOR_BLOCK, this.menu.blockEntity.getBlockPos(), ConfigPacket.ConfigType.COLLECTOR_DOWN_UP_OFFSET, 0, false));
        ClientPacketDistributor.sendToServer(new ConfigPacket(ConfigPacket.ConfigTarget.COLLECTOR_BLOCK, this.menu.blockEntity.getBlockPos(), ConfigPacket.ConfigType.COLLECTOR_NORTH_SOUTH_OFFSET, 0, false));
        ClientPacketDistributor.sendToServer(new ConfigPacket(ConfigPacket.ConfigTarget.COLLECTOR_BLOCK, this.menu.blockEntity.getBlockPos(), ConfigPacket.ConfigType.COLLECTOR_EAST_WEST_OFFSET, 0, false));
    }

    private void withdrawXP() {
        try {
            int levels = Integer.parseInt(this.xpInputField.getValue());
            if (levels > 0) {
                int xpAmount = this.levelToXp(levels);
                if (xpAmount <= this.storedXP) {
                    ClientPacketDistributor.sendToServer(new CollectorXpPacket(this.menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.WITHDRAW, xpAmount));
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
                ClientPacketDistributor.sendToServer(new CollectorXpPacket(this.menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.DEPOSIT, xpAmount));
                this.storedXP = Math.min(this.maxStoredXP, this.storedXP + xpAmount);
                this.xpInputField.setValue("0");
            }
        } catch (NumberFormatException e) {
            this.xpInputField.setValue("0");
        }
    }

    private void withdrawAllXP() {
        if (this.storedXP > 0) {
            ClientPacketDistributor.sendToServer(new CollectorXpPacket(this.menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.WITHDRAW, this.storedXP));
            this.storedXP = 0;
            this.xpInputField.setValue("0");
        }
    }

    private void depositAllXP() {
        int playerXP = this.minecraft.player.totalExperience;
        if (playerXP > 0) {
            ClientPacketDistributor.sendToServer(new CollectorXpPacket(this.menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.DEPOSIT, playerXP));
            this.storedXP = Math.min(this.maxStoredXP, this.storedXP + playerXP);
            this.xpInputField.setValue("0");
        }
    }

    private void renderOffsetValues(GuiGraphicsExtractor graphics, int x, int y) {
        var pose = graphics.pose();
        float scale = 0.65F;

        pose.pushMatrix();
        pose.scale(scale, scale);
        graphics.text(this.font, (this.downUpOffset >= 0 ? "+" : "") + this.downUpOffset, (int) ((x + 194) / scale), (int) ((y + 71) / scale), 0xFF000000, false);
        graphics.text(this.font, (this.northSouthOffset >= 0 ? "+" : "") + this.northSouthOffset, (int) ((x + 194) / scale), (int) ((y + 93) / scale), 0xFF000000, false);
        graphics.text(this.font, (this.eastWestOffset >= 0 ? "+" : "") + this.eastWestOffset, (int) ((x + 194) / scale), (int) ((y + 115) / scale), 0xFF000000, false);
        pose.popMatrix();

        pose.pushMatrix();
        pose.scale(scale, scale);
        graphics.text(this.font, Component.translatable("gui.mobflowutilities.collector.offset.down_up").getString(), (int) ((x + 182) / scale), (int) ((y + 60) / scale), 0xFF000000, false);
        graphics.text(this.font, Component.translatable("gui.mobflowutilities.collector.offset.north_south").getString(), (int) ((x + 182) / scale), (int) ((y + 82) / scale), 0xFF000000, false);
        graphics.text(this.font, Component.translatable("gui.mobflowutilities.collector.offset.east_west").getString(), (int) ((x + 182) / scale), (int) ((y + 104) / scale), 0xFF000000, false);
        pose.popMatrix();
    }

    private void renderXPCollectionToggle(GuiGraphicsExtractor graphics, int x, int y) {
        Identifier toggleHandle = Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_scroller_handle");
        int handleX = this.xpCollectionEnabled ? x + 125 : x + 117;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, toggleHandle, handleX, y + 112, 6, 10);
    }

    private void renderXPDisplay(GuiGraphicsExtractor graphics, int x, int y) {
        var pose = graphics.pose();
        float scale = 0.65F;
        pose.pushMatrix();
        pose.scale(scale, scale);
        int storedLevels = this.xpToLevel(this.storedXP);
        String xpDisplayText = String.format(Component.translatable("gui.mobflowutilities.collector.xp_levels_stored").getString(), storedLevels);
        graphics.text(this.font, xpDisplayText, (int) ((x + 12) / scale), (int) ((y + 132) / scale), 0xFF30a324, false);
        pose.popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (event.button() == 0 && event.x() >= x + 118 && event.x() <= x + 134 && event.y() >= y + 113 && event.y() <= y + 121) {
            this.xpCollectionEnabled = !this.xpCollectionEnabled;
            ClientPacketDistributor.sendToServer(new ConfigPacket(
                    ConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                    this.menu.blockEntity.getBlockPos(),
                    ConfigPacket.ConfigType.COLLECTOR_XP_COLLECTION_TOGGLE,
                    0,
                    this.xpCollectionEnabled
            ));
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.syncFromBlockEntity();
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                this.leftPos, this.topPos, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);
        this.renderOffsetValues(graphics, this.leftPos, this.topPos);
        this.renderXPCollectionToggle(graphics, this.leftPos, this.topPos);
        this.renderXPDisplay(graphics, this.leftPos, this.topPos);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (mouseX >= x + 117 && mouseX <= x + 131 && mouseY >= y + 113 && mouseY <= y + 130) {
            Component tooltipText = this.xpCollectionEnabled
                    ? Component.translatable("tooltip.mobflowutilities.collector.xp_collection.enabled")
                    : Component.translatable("tooltip.mobflowutilities.collector.xp_collection.disabled");
            graphics.setComponentTooltipForNextFrame(this.font, List.of(tooltipText), mouseX, mouseY);
            return;
        }

        float scale = 0.8F;
        int displayWidth = (int) (this.font.width(this.getXpDisplayText()) * scale);
        int displayHeight = (int) (9.0F * scale);
        int actualX = x + 9;
        int actualY = y + 129;
        if (mouseX >= actualX && mouseX <= actualX + displayWidth && mouseY >= actualY && mouseY <= actualY + displayHeight) {
            MutableComponent tooltipText = Component.translatable("tooltip.mobflowutilities.collector.xp_display", String.format("%,d", this.storedXP));
            graphics.setComponentTooltipForNextFrame(this.font, List.of(tooltipText), mouseX, mouseY);
            return;
        }

        if (mouseX >= x + 192 && mouseX <= x + 204 && mouseY >= y + 68 && mouseY <= y + 76) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(Component.translatable("tooltip.mobflowutilities.collector.offset.down_up.value", this.downUpOffset)), mouseX, mouseY);
            return;
        }
        if (mouseX >= x + 192 && mouseX <= x + 204 && mouseY >= y + 90 && mouseY <= y + 98) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(Component.translatable("tooltip.mobflowutilities.collector.offset.north_south.value", this.northSouthOffset)), mouseX, mouseY);
            return;
        }
        if (mouseX >= x + 192 && mouseX <= x + 204 && mouseY >= y + 112 && mouseY <= y + 120) {
            graphics.setComponentTooltipForNextFrame(this.font, List.of(Component.translatable("tooltip.mobflowutilities.collector.offset.east_west.value", this.eastWestOffset)), mouseX, mouseY);
            return;
        }

        super.extractTooltip(graphics, mouseX, mouseY);
    }

    private String getXpDisplayText() {
        return String.format("Levels Stored: %,d", this.xpToLevel(this.storedXP));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.syncFromBlockEntity();
    }
}