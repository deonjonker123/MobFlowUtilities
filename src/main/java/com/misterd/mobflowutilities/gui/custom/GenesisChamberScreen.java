package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.client.renderer.GenesisChamberWireframeRenderer;
import com.misterd.mobflowutilities.network.ConfigPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public class GenesisChamberScreen extends AbstractContainerScreen<GenesisChamberMenu> {

    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(MobFlowUtilities.MODID, "textures/gui/genesis_chamber_gui.png");
    private static final WidgetSprites REDUCE_OFFSET_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn_hover"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "reduce_offset_btn_hover")
    );
    private static final WidgetSprites INCREASE_OFFSET_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn_hover"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "increase_offset_btn_hover")
    );
    private static final WidgetSprites RESET_OFFSET_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn_hover"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "collection_zone_offset_reset_btn_hover")
    );
    private static final WidgetSprites TOGGLE_WIREFRAME_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover"),
            Identifier.fromNamespaceAndPath("mobflowutilities", "toggle_zone_wireframe_btn_hover")
    );

    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;
    private boolean showWireframe = false;
    private boolean requiresRedstone = false;

    public GenesisChamberScreen(GenesisChamberMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 189);
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.syncFromBlockEntity();
        this.addOffsetButtons(this.leftPos, this.topPos);
        this.addWireframeButton(this.leftPos, this.topPos);
    }

    private void addOffsetButtons(int leftPos, int topPos) {
        ImageButton duDecreaseButton = new ImageButton(leftPos + 121, topPos + 17, 10, 10, REDUCE_OFFSET_SPRITES, button -> this.adjustOffset("downUp", -1));
        duDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.down_up.decrease")));
        this.addRenderableWidget(duDecreaseButton);

        ImageButton duIncreaseButton = new ImageButton(leftPos + 153, topPos + 17, 10, 10, INCREASE_OFFSET_SPRITES, button -> this.adjustOffset("downUp", 1));
        duIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.down_up.increase")));
        this.addRenderableWidget(duIncreaseButton);

        ImageButton nsDecreaseButton = new ImageButton(leftPos + 121, topPos + 39, 10, 10, REDUCE_OFFSET_SPRITES, button -> this.adjustOffset("northSouth", -1));
        nsDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.north_south.decrease")));
        this.addRenderableWidget(nsDecreaseButton);

        ImageButton nsIncreaseButton = new ImageButton(leftPos + 153, topPos + 39, 10, 10, INCREASE_OFFSET_SPRITES, button -> this.adjustOffset("northSouth", 1));
        nsIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.north_south.increase")));
        this.addRenderableWidget(nsIncreaseButton);

        ImageButton ewDecreaseButton = new ImageButton(leftPos + 121, topPos + 61, 10, 10, REDUCE_OFFSET_SPRITES, button -> this.adjustOffset("eastWest", -1));
        ewDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.east_west.decrease")));
        this.addRenderableWidget(ewDecreaseButton);

        ImageButton ewIncreaseButton = new ImageButton(leftPos + 153, topPos + 61, 10, 10, INCREASE_OFFSET_SPRITES, button -> this.adjustOffset("eastWest", 1));
        ewIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.east_west.increase")));
        this.addRenderableWidget(ewIncreaseButton);

        ImageButton resetOffsetButton = new ImageButton(leftPos + 136, topPos + 76, 12, 12, RESET_OFFSET_SPRITES, button -> this.resetAllOffsets());
        resetOffsetButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.collector.offset.reset_all")));
        this.addRenderableWidget(resetOffsetButton);
    }

    private void addWireframeButton(int leftPos, int topPos) {
        ImageButton wireframeButton = new ImageButton(leftPos + 153, topPos + 77, 10, 10,
                this.createConditionalSprites(TOGGLE_WIREFRAME_SPRITES, () -> this.showWireframe),
                button -> this.toggleWireframe());
        wireframeButton.setTooltip(Tooltip.create(Component.translatable("tooltip.mobflowutilities.genesis_chamber.wireframe_toggle")));
        this.addRenderableWidget(wireframeButton);
    }

    private WidgetSprites createConditionalSprites(WidgetSprites baseSprites, Supplier<Boolean> condition) {
        return condition.get()
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
            default -> { return; }
        }
        ClientPacketDistributor.sendToServer(new ConfigPacket(
                ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK,
                this.menu.blockEntity.getBlockPos(),
                configType,
                newValue,
                false
        ));
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
        ClientPacketDistributor.sendToServer(new ConfigPacket(ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK, this.menu.blockEntity.getBlockPos(), ConfigPacket.ConfigType.GENESIS_CHAMBER_DOWN_UP_OFFSET, 0, false));
        ClientPacketDistributor.sendToServer(new ConfigPacket(ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK, this.menu.blockEntity.getBlockPos(), ConfigPacket.ConfigType.GENESIS_CHAMBER_NORTH_SOUTH_OFFSET, 0, false));
        ClientPacketDistributor.sendToServer(new ConfigPacket(ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK, this.menu.blockEntity.getBlockPos(), ConfigPacket.ConfigType.GENESIS_CHAMBER_EAST_WEST_OFFSET, 0, false));
    }

    private void syncFromBlockEntity() {
        if (this.menu.blockEntity != null) {
            this.downUpOffset = this.menu.blockEntity.getDownUpOffset();
            this.northSouthOffset = this.menu.blockEntity.getNorthSouthOffset();
            this.eastWestOffset = this.menu.blockEntity.getEastWestOffset();
            this.showWireframe = GenesisChamberWireframeRenderer.isWireframeActive(this.menu.blockEntity.getBlockPos());
            this.requiresRedstone = this.menu.blockEntity.getRequiresRedstone();
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (event.button() == 0 && event.x() >= x + 55 && event.x() <= x + 67 && event.y() >= y + 73 && event.y() <= y + 85) {
            this.requiresRedstone = !this.requiresRedstone;
            ClientPacketDistributor.sendToServer(new ConfigPacket(
                    ConfigPacket.ConfigTarget.GENESIS_CHAMBER_BLOCK,
                    this.menu.blockEntity.getBlockPos(),
                    ConfigPacket.ConfigType.GENESIS_CHAMBER_REDSTONE_MODE,
                    0,
                    this.requiresRedstone
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
        this.renderBurnProgress(graphics);
        this.renderMobPreview(graphics, mouseX, mouseY);
        this.renderRedstoneToggle(graphics);
        this.renderOffsetValues(graphics);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
    }

    private void renderBurnProgress(GuiGraphicsExtractor graphics) {
        if (this.menu.blockEntity == null) return;
        int burnTime = this.menu.blockEntity.getBurnTime();
        int maxBurnTime = this.menu.blockEntity.getMaxBurnTime();
        if (maxBurnTime > 0) {
            int progress = (int) (13.0 * burnTime / maxBurnTime);
            if (progress > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE,
                        this.leftPos + 81, this.topPos + 50 + (13 - progress),
                        176.0F, (float) (13 - progress),
                        14, progress, 256, 256);
            }
        }
    }

    private void renderMobPreview(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.menu.blockEntity == null) return;
        ItemStack eggStack = this.menu.blockEntity.getStack(0);
        if (!(eggStack.getItem() instanceof SpawnEggItem spawnEgg)) return;
        EntityType<?> entityType = spawnEgg.getType(eggStack);
        if (entityType == null) return;
        Entity entity = this.menu.blockEntity.getOrCreateRenderedEntity(entityType);
        if (!(entity instanceof LivingEntity living)) return;

        int centerX = this.leftPos + 25;
        int centerY = this.topPos + 26;
        InventoryScreen.extractEntityInInventoryFollowsMouse(
                graphics,
                centerX - 17, centerY - 8,
                centerX + 17, centerY + 38,
                17, 0.0625F,
                mouseX, mouseY,
                living
        );
    }

    private void renderRedstoneToggle(GuiGraphicsExtractor graphics) {
        Identifier sprite = this.requiresRedstone
                ? Identifier.fromNamespaceAndPath("mobflowutilities", "redstone_required_btn")
                : Identifier.fromNamespaceAndPath("mobflowutilities", "no_redstone_required_btn");
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.leftPos + 55, this.topPos + 73, 12, 12);
    }

    private void renderOffsetValues(GuiGraphicsExtractor graphics) {
        var pose = graphics.pose();
        float scale = 0.7F;
        pose.pushMatrix();
        pose.scale(scale, scale);

        int x = (int)(this.leftPos / scale);
        int y = (int)(this.topPos / scale);

        graphics.text(this.font, (this.downUpOffset >= 0 ? "+" : "") + this.downUpOffset,   x + (int)(137 / scale), y + (int)(20 / scale), 0xFF000000, false);
        graphics.text(this.font, (this.northSouthOffset >= 0 ? "+" : "") + this.northSouthOffset, x + (int)(137 / scale), y + (int)(42 / scale), 0xFF000000, false);
        graphics.text(this.font, (this.eastWestOffset >= 0 ? "+" : "") + this.eastWestOffset,   x + (int)(137 / scale), y + (int)(64 / scale), 0xFF000000, false);
        graphics.text(this.font, Component.translatable("gui.mobflowutilities.collector.offset.down_up").getString(),    x + (int)(124 / scale), y + (int)(10 / scale), 0xFF000000, false);
        graphics.text(this.font, Component.translatable("gui.mobflowutilities.collector.offset.north_south").getString(), x + (int)(124 / scale), y + (int)(32 / scale), 0xFF000000, false);
        graphics.text(this.font, Component.translatable("gui.mobflowutilities.collector.offset.east_west").getString(),  x + (int)(124 / scale), y + (int)(54 / scale), 0xFF000000, false);
        pose.popMatrix();
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (mouseX >= x + 55 && mouseX <= x + 67 && mouseY >= y + 73 && mouseY <= y + 85) {
            Component tooltipText = this.requiresRedstone
                    ? Component.translatable("tooltip.mobflowutilities.genesis_chamber.redstone_required")
                    : Component.translatable("tooltip.mobflowutilities.genesis_chamber.no_redstone_required");
            graphics.setComponentTooltipForNextFrame(this.font, List.of(tooltipText), mouseX, mouseY);
            return;
        }
        super.extractTooltip(graphics, mouseX, mouseY);
    }
}