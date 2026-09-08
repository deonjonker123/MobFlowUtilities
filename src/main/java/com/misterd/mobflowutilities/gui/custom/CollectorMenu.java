package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.blockentity.custom.CollectorBlockEntity;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CollectorMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;
    private static final int MODULE_SLOT_COUNT = 1;
    private static final int OUTPUT_SLOT_COUNT = 45;
    private static final int TE_FIRST_SLOT = PLAYER_SLOTS;
    private static final int MODULE_SLOT_INDEX = TE_FIRST_SLOT;
    private static final int OUTPUT_FIRST_SLOT = MODULE_SLOT_INDEX + MODULE_SLOT_COUNT;
    private static final int TE_LAST_SLOT = OUTPUT_FIRST_SLOT + OUTPUT_SLOT_COUNT;

    public final CollectorBlockEntity blockEntity;
    private final Level level;

    public CollectorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CollectorMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(MFUMenuTypes.COLLECTOR_MENU.get(), containerId);
        this.blockEntity = (CollectorBlockEntity) blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addSlot(new ModuleSlot(this.blockEntity.moduleSlots, 0, 192, 18, MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get()));

        int slotIndex = 0;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                addSlot(new OutputSlot(this.blockEntity.outputInventory, slotIndex, x, y, this.blockEntity));
                slotIndex++;
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot sourceSlot = this.slots.get(slotIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (slotIndex < PLAYER_SLOTS) {
            // Player -> block entity. Only the module slot accepts manual placement;
            // output slots are collector-filled only (see OutputSlot#mayPlace).
            if (!isValidModule(sourceStack) || !moveItemStackTo(sourceStack, MODULE_SLOT_INDEX, OUTPUT_FIRST_SLOT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotIndex < TE_LAST_SLOT) {
            // Block entity -> player
            if (!moveItemStackTo(sourceStack, 0, PLAYER_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    private boolean isValidModule(ItemStack stack) {
        return stack.getItem() == MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.level, this.blockEntity.getBlockPos()), player, MFUBlocks.COLLECTOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(playerInventory, l + i * 9 + 9, 37 + l * 18, 159 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 37 + i * 18, 218));
        }
    }

    private static class ModuleSlot extends SlotItemHandler {
        private final Item allowedModule;

        public ModuleSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item allowedModule) {
            super(itemHandler, index, xPosition, yPosition);
            this.allowedModule = allowedModule;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == this.allowedModule;
        }
    }

    public static class OutputSlot extends SlotItemHandler {
        private final CollectorBlockEntity collector;

        public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, CollectorBlockEntity collector) {
            super(itemHandler, index, xPosition, yPosition);
            this.collector = collector;
        }

        @Override
        public int getMaxStackSize() {
            return this.getItemHandler().getSlotLimit(this.getSlotIndex());
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return this.getItemHandler().getSlotLimit(this.getSlotIndex());
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}