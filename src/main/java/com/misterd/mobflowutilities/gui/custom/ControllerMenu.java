package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.entity.custom.ControllerBlockEntity;
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

public class ControllerMenu extends AbstractContainerMenu {
    public final ControllerBlockEntity blockEntity;
    private final Level level;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
    private static final int VANILLA_SLOT_COUNT = 36;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int MODULE_INVENTORY_FIRST_SLOT_INDEX = 36;
    private static final int MODULE_INVENTORY_SLOT_COUNT = 5;

    public ControllerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ControllerMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(MFUMenuTypes.CONTROLLER_MENU.get(), containerId);
        this.blockEntity = (ControllerBlockEntity)blockEntity;
        this.level = inv.player.level();
        this.addPlayerInventory(inv);
        this.addPlayerHotbar(inv);
        this.addSlot(new ControllerMenu.ModuleSlot(this.blockEntity.inventory, 0, 8, 18, MFUItems.SHARPNESS_MODULE.get()));
        this.addSlot(new ControllerMenu.ModuleSlot(this.blockEntity.inventory, 1, 44, 18, MFUItems.FIRE_ASPECT_MODULE.get()));
        this.addSlot(new ControllerMenu.ModuleSlot(this.blockEntity.inventory, 2, 80, 18, MFUItems.SMITE_MODULE.get()));
        this.addSlot(new ControllerMenu.ModuleSlot(this.blockEntity.inventory, 3, 116, 18, MFUItems.BOA_MODULE.get()));
        this.addSlot(new ControllerMenu.ModuleSlot(this.blockEntity.inventory, 4, 152, 18, MFUItems.LOOTING_MODULE.get()));
    }

    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = this.slots.get(pIndex);
        if (sourceSlot != null && sourceSlot.hasItem()) {
            ItemStack sourceStack = sourceSlot.getItem();
            ItemStack copyOfSourceStack = sourceStack.copy();
            if (pIndex < 36) {
                if (!this.isValidModule(sourceStack)) {
                    return ItemStack.EMPTY;
                }

                if (!this.moveItemStackTo(sourceStack, 36, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (pIndex >= 41) {
                    System.out.println("Invalid slotIndex:" + pIndex);
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

            sourceSlot.onTake(playerIn, sourceStack);
            return copyOfSourceStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    private boolean isValidModule(ItemStack stack) {
        return stack.getItem() == MFUItems.SHARPNESS_MODULE.get() || stack.getItem() == MFUItems.FIRE_ASPECT_MODULE.get() || stack.getItem() == MFUItems.SMITE_MODULE.get() || stack.getItem() == MFUItems.BOA_MODULE.get() || stack.getItem() == MFUItems.LOOTING_MODULE.get();
    }

    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.level, this.blockEntity.getBlockPos()), player, MFUBlocks.CONTROLLER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0; i < 3; ++i) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 51 + i * 18));
            }
        }

    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 110));
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

        public int getMaxStackSize() {
            return 10;
        }

        public int getMaxStackSize(ItemStack stack) {
            return 10;
        }
    }
}
