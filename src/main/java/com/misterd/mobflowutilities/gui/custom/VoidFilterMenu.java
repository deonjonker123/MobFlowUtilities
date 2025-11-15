package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.component.custom.VoidFilterData;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class VoidFilterMenu extends AbstractContainerMenu {
    private final ItemStack filterItem;
    private final ItemStackHandler filterSlots;
    private boolean ignoreNBT;
    private boolean ignoreDurability;

    public VoidFilterMenu(int containerId, Inventory playerInventory, ItemStack filterItem) {
        super(MFUMenuTypes.VOID_FILTER_MENU.get(), containerId);
        this.ignoreNBT = true;
        this.ignoreDurability = true;
        this.filterItem = filterItem;
        this.filterSlots = new ItemStackHandler(45) {
            protected void onContentsChanged(int slot) {
                VoidFilterMenu.this.saveFilterData();
            }

            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return super.extractItem(slot, amount, simulate);
            }
        };
        this.loadFilterData();
        this.addFilterSlots();
        this.addPlayerInventory(playerInventory);
        this.addPlayerHotbar(playerInventory);
    }

    public VoidFilterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.getMainHandItem());
    }

    private void addFilterSlots() {
        int slotIndex = 0;

        for(int row = 0; row < 5; ++row) {
            for(int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                this.addSlot(new VoidFilterMenu.FilterSlot(this.filterSlots, slotIndex, x, y));
                ++slotIndex;
            }
        }

    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = 123 + row * 18;
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for(int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            int y = 182;
            this.addSlot(new Slot(playerInventory, col, x, y));
        }

    }

    private void loadFilterData() {
        VoidFilterData data = (VoidFilterData)this.filterItem.getOrDefault(MFUDataComponents.VOID_FILTER_DATA.get(), VoidFilterData.DEFAULT);
        this.ignoreNBT = data.ignoreNBT();
        this.ignoreDurability = data.ignoreDurability();
        data.loadIntoHandler(this.filterSlots);
    }

    private void saveFilterData() {
        VoidFilterData newData = VoidFilterData.fromHandler(this.filterSlots, this.ignoreNBT, this.ignoreDurability);
        this.filterItem.set(MFUDataComponents.VOID_FILTER_DATA.get(), newData);
    }

    public boolean isIgnoreNBT() {
        return this.ignoreNBT;
    }

    public boolean isIgnoreDurability() {
        return this.ignoreDurability;
    }

    public ItemStackHandler getFilterSlots() {
        return this.filterSlots;
    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < 45) {
            VoidFilterMenu.FilterSlot filterSlot = (VoidFilterMenu.FilterSlot)this.slots.get(slotId);
            ItemStack carried = this.getCarried();
            if (!carried.isEmpty()) {
                filterSlot.setFilterItem(carried);
            } else {
                filterSlot.setFilterItem(ItemStack.EMPTY);
            }

        } else {
            super.clicked(slotId, dragType, clickType, player);
        }
    }

    public boolean stillValid(Player player) {
        return player.getInventory().contains(this.filterItem);
    }

    private static class FilterSlot extends SlotItemHandler {
        public FilterSlot(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        public boolean mayPickup(Player player) {
            return true;
        }

        public ItemStack remove(int amount) {
            ItemStack result = this.getItem().copy();
            this.set(ItemStack.EMPTY);
            return result;
        }

        public void onTake(Player player, ItemStack stack) {
            this.set(ItemStack.EMPTY);
        }

        public boolean allowModification(Player player) {
            return true;
        }

        public void setFilterItem(ItemStack stack) {
            if (stack.isEmpty()) {
                this.set(ItemStack.EMPTY);
            } else {
                this.set(stack.copyWithCount(1));
            }

        }
    }
}
