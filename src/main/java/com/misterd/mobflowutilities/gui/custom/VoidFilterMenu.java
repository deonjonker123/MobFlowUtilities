package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.component.custom.VoidFilterData;
import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class VoidFilterMenu extends AbstractContainerMenu {
    private ItemStack filterItem;
    private final ItemStackHandler filterSlots;
    private boolean ignoreNBT;
    private boolean ignoreDurability;

    @Nullable
    private final BlockPos collectorPos;
    private final int collectorFilterSlotIndex;
    @Nullable
    private final Player player;

    public VoidFilterMenu(int containerId, Inventory playerInventory, ItemStack filterItem) {
        this(containerId, playerInventory, filterItem, null, -1);
    }

    private VoidFilterMenu(int containerId, Inventory playerInventory, ItemStack filterItem,
                           @Nullable BlockPos collectorPos, int filterSlotIndex) {
        super(MFUMenuTypes.VOID_FILTER_MENU.get(), containerId);
        this.ignoreNBT = true;
        this.ignoreDurability = true;
        this.filterItem = filterItem;
        this.collectorPos = collectorPos;
        this.collectorFilterSlotIndex = filterSlotIndex;
        this.player = playerInventory.player;

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
        this(containerId, playerInventory, getFilterFromPacket(playerInventory, extraData),
                getCollectorPosFromPacket(extraData), getFilterSlotFromPacket(extraData));
    }

    private static ItemStack getFilterFromPacket(Inventory playerInventory, FriendlyByteBuf extraData) {
        if (extraData != null && extraData.readableBytes() >= 16) {
            int readerIndex = extraData.readerIndex();
            BlockPos collectorPos = extraData.readBlockPos();
            int filterSlotIndex = extraData.readInt();
            extraData.readerIndex(readerIndex);

            BlockEntity be = playerInventory.player.level().getBlockEntity(collectorPos);
            if (be instanceof CollectorBlockEntity collector) {
                return collector.moduleSlots.getStackInSlot(filterSlotIndex);
            }
        }
        return playerInventory.player.getMainHandItem();
    }

    @Nullable
    private static BlockPos getCollectorPosFromPacket(FriendlyByteBuf extraData) {
        if (extraData != null && extraData.readableBytes() >= 16) {
            int readerIndex = extraData.readerIndex();
            BlockPos pos = extraData.readBlockPos();
            extraData.readerIndex(readerIndex);
            return pos;
        }
        return null;
    }

    private static int getFilterSlotFromPacket(FriendlyByteBuf extraData) {
        if (extraData != null && extraData.readableBytes() >= 16) {
            int readerIndex = extraData.readerIndex();
            extraData.readBlockPos();
            int slot = extraData.readInt();
            extraData.readerIndex(readerIndex);
            return slot;
        }
        return -1;
    }

    public static MenuProvider createForCollectorFilter(Component title, BlockPos collectorPos, int filterSlotIndex) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return title;
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                BlockEntity be = player.level().getBlockEntity(collectorPos);
                if (be instanceof CollectorBlockEntity collector) {
                    ItemStack filterStack = collector.moduleSlots.getStackInSlot(filterSlotIndex);
                    return new VoidFilterMenu(containerId, playerInventory, filterStack, collectorPos, filterSlotIndex);
                }
                return null;
            }
        };
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
        VoidFilterData data = this.filterItem.getOrDefault(
                MFUDataComponents.VOID_FILTER_DATA.get(),
                VoidFilterData.DEFAULT
        );
        this.ignoreNBT = data.ignoreNBT();
        this.ignoreDurability = data.ignoreDurability();
        data.loadIntoHandler(this.filterSlots);
    }

    private void saveFilterData() {
        if (this.collectorPos != null && this.collectorFilterSlotIndex >= 0 && this.player != null) {
            BlockEntity be = this.player.level().getBlockEntity(this.collectorPos);
            if (be instanceof CollectorBlockEntity collector) {
                this.filterItem = collector.moduleSlots.getStackInSlot(this.collectorFilterSlotIndex);
            }
        }

        VoidFilterData newData = VoidFilterData.fromHandler(
                this.filterSlots,
                this.ignoreNBT,
                this.ignoreDurability
        );
        this.filterItem.set(MFUDataComponents.VOID_FILTER_DATA.get(), newData);

        if (this.collectorPos != null && this.player != null) {
            BlockEntity be = this.player.level().getBlockEntity(this.collectorPos);
            if (be instanceof CollectorBlockEntity collector) {
                collector.setChanged();
            }
        }
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.saveFilterData();
    }

    public boolean stillValid(Player player) {
        if (this.collectorPos != null) {
            BlockEntity be = player.level().getBlockEntity(this.collectorPos);
            return be instanceof CollectorBlockEntity &&
                    player.distanceToSqr(collectorPos.getX() + 0.5, collectorPos.getY() + 0.5, collectorPos.getZ() + 0.5) <= 64.0;
        }
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