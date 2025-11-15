package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
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
    public final CollectorBlockEntity blockEntity;
    private final Level level;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
    private static final int VANILLA_SLOT_COUNT = 36;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int MODULE_SLOTS_FIRST_INDEX = 36;
    private static final int MODULE_SLOTS_COUNT = 4;
    private static final int OUTPUT_SLOTS_FIRST_INDEX = 40;
    private static final int OUTPUT_SLOTS_COUNT = 45;

    public CollectorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CollectorMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(MFUMenuTypes.COLLECTOR_MENU.get(), containerId);
        this.blockEntity = (CollectorBlockEntity)blockEntity;
        this.level = inv.player.level();
        this.addPlayerInventory(inv);
        this.addPlayerHotbar(inv);
        this.addSlot(new CollectorMenu.ModuleSlot(this.blockEntity.moduleSlots, 0, 192, 36, MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get()));
        this.addSlot(new CollectorMenu.ModuleSlot(this.blockEntity.moduleSlots, 1, 174, 18, MFUItems.VOID_FILTER_MODULE.get()));
        this.addSlot(new CollectorMenu.ModuleSlot(this.blockEntity.moduleSlots, 2, 192, 18, MFUItems.VOID_FILTER_MODULE.get()));
        this.addSlot(new CollectorMenu.ModuleSlot(this.blockEntity.moduleSlots, 3, 210, 18, MFUItems.VOID_FILTER_MODULE.get()));
        int slotIndex = 0;

        for(int row = 0; row < 5; ++row) {
            for(int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                this.addSlot(new CollectorMenu.OutputSlot(this.blockEntity.outputInventory, slotIndex, x, y, this.blockEntity));
                ++slotIndex;
            }
        }

    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot sourceSlot = this.slots.get(slotIndex);
        if (sourceSlot != null && sourceSlot.hasItem()) {
            ItemStack sourceStack = sourceSlot.getItem();
            ItemStack copyOfSourceStack = sourceStack.copy();
            if (slotIndex < 36) {
                if (!this.isValidModule(sourceStack)) {
                    return ItemStack.EMPTY;
                }

                if (!this.moveItemStackTo(sourceStack, 36, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < 40) {
                if (!this.moveItemStackTo(sourceStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotIndex >= 85) {
                    return ItemStack.EMPTY;
                }

                if (!this.moveItemStackTo(sourceStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (sourceStack.getCount() == 0) {
                sourceSlot.set(ItemStack.EMPTY);
            } else {
                sourceSlot.setChanged();
            }

            sourceSlot.onTake(player, sourceStack);
            return copyOfSourceStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    private boolean isValidModule(ItemStack stack) {
        return stack.getItem() == MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get() || stack.getItem() == MFUItems.VOID_FILTER_MODULE.get();
    }

    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.level, this.blockEntity.getBlockPos()), player, MFUBlocks.COLLECTOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0; i < 3; ++i) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 37 + l * 18, 159 + i * 18));
            }
        }

    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 37 + i * 18, 218));
        }

    }

    private static class ModuleSlot extends SlotItemHandler {
        private final Item allowedModule;

        public ModuleSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item allowedModule) {
            super(itemHandler, index, xPosition, yPosition);
            this.allowedModule = allowedModule;
        }

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

        public int getMaxStackSize() {
            return this.getItemHandler().getSlotLimit(this.getSlotIndex());
        }

        public int getMaxStackSize(ItemStack stack) {
            return this.getItemHandler().getSlotLimit(this.getSlotIndex());
        }

        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
