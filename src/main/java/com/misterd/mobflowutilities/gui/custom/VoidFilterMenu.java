package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.component.custom.VoidFilterData;
import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;

public class VoidFilterMenu extends AbstractContainerMenu {

    private static final int FILTER_SLOT_COUNT = 45;
    private static final int FILTER_ROWS = 5;
    private static final int FILTER_COLS = 9;

    private ItemStack filterItem;
    private final SimpleContainer filterSlots;
    private boolean ignoreNBT;
    private boolean ignoreDurability;

    @Nullable
    private final BlockPos collectorPos;

    private final int collectorFilterSlotIndex;

    @Nullable
    private final Player player;

    public VoidFilterMenu(int containerId, Inventory inv, ItemStack filterItem) {
        this(containerId, inv, filterItem, null, -1);
    }

    private VoidFilterMenu(int containerId, Inventory inv, ItemStack filterItem, @Nullable BlockPos collectorPos, int filterSlotIndex) {
        super(MFUMenuTypes.VOID_FILTER_MENU.get(), containerId);
        this.ignoreNBT = true;
        this.ignoreDurability = true;
        this.filterItem = filterItem;
        this.collectorPos = collectorPos;
        this.collectorFilterSlotIndex = filterSlotIndex;
        this.player = inv.player;

        this.filterSlots = new SimpleContainer(FILTER_SLOT_COUNT) {
            @Override
            public void setChanged() {
                super.setChanged();
                VoidFilterMenu.this.saveFilterData();
            }
        };

        loadFilterData();
        addFilterSlots();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public VoidFilterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                getFilterFromPacket(inv, extraData),
                getCollectorPosFromPacket(extraData),
                getFilterSlotFromPacket(extraData));
    }

    public static MenuProvider createForCollectorFilter(Component title, BlockPos collectorPos, int filterSlotIndex) {
        return new MenuProvider() {
            @Override public Component getDisplayName() { return title; }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
                BlockEntity be = player.level().getBlockEntity(collectorPos);
                if (be instanceof CollectorBlockEntity collector) {
                    ItemStack filterStack = collector.moduleSlots.getStackInSlot(filterSlotIndex);
                    return new VoidFilterMenu(containerId, inv, filterStack, collectorPos, filterSlotIndex);
                }
                return null;
            }
        };
    }

    private static ItemStack getFilterFromPacket(Inventory inv, FriendlyByteBuf buf) {
        if (buf != null && buf.readableBytes() >= 16) {
            int idx = buf.readerIndex();
            BlockPos pos = buf.readBlockPos();
            int slot = buf.readInt();
            buf.readerIndex(idx);
            BlockEntity be = inv.player.level().getBlockEntity(pos);
            if (be instanceof CollectorBlockEntity collector)
                return collector.moduleSlots.getStackInSlot(slot);
        }
        return inv.player.getMainHandItem();
    }

    @Nullable
    private static BlockPos getCollectorPosFromPacket(FriendlyByteBuf buf) {
        if (buf != null && buf.readableBytes() >= 16) {
            int idx = buf.readerIndex();
            BlockPos pos = buf.readBlockPos();
            buf.readerIndex(idx);
            return pos;
        }
        return null;
    }

    private static int getFilterSlotFromPacket(FriendlyByteBuf buf) {
        if (buf != null && buf.readableBytes() >= 16) {
            int idx = buf.readerIndex();
            buf.readBlockPos();
            int slot = buf.readInt();
            buf.readerIndex(idx);
            return slot;
        }
        return -1;
    }

    private void addFilterSlots() {
        int i = 0;
        for (int row = 0; row < FILTER_ROWS; row++)
            for (int col = 0; col < FILTER_COLS; col++)
                addSlot(new FilterSlot(filterSlots, i++, 8 + col * 18, 18 + row * 18));
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 123 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 8 + i * 18, 182));
    }

    private void loadFilterData() {
        VoidFilterData data = filterItem.getOrDefault(
                MFUDataComponents.VOID_FILTER_DATA.get(), VoidFilterData.DEFAULT);
        this.ignoreNBT = data.ignoreNBT();
        this.ignoreDurability = data.ignoreDurability();
        data.loadIntoHandler(filterSlots);
    }

    private void saveFilterData() {
        VoidFilterData newData = VoidFilterData.fromHandler(filterSlots, ignoreNBT, ignoreDurability);

        if (collectorPos != null && collectorFilterSlotIndex >= 0 && player != null) {
            BlockEntity be = player.level().getBlockEntity(collectorPos);
            if (be instanceof CollectorBlockEntity collector) {
                int slot = collectorFilterSlotIndex;
                ItemResource existing = collector.inventory.getResource(slot);
                if (!existing.isEmpty()) {
                    ItemStack updated = existing.toStack();
                    updated.set(MFUDataComponents.VOID_FILTER_DATA.get(), newData);
                    ItemResource updatedResource = ItemResource.of(updated);

                    try (var tx = Transaction.openRoot()) {
                        collector.inventory.extract(slot, existing, 1, tx);
                        collector.inventory.insert(slot, updatedResource, 1, tx);
                        tx.commit();
                    }
                    collector.setChanged();
                    if (!player.level().isClientSide()) {
                        player.level().sendBlockUpdated(collectorPos,
                                player.level().getBlockState(collectorPos),
                                player.level().getBlockState(collectorPos), 3);
                    }
                }
            }
            return;
        }

        filterItem.set(MFUDataComponents.VOID_FILTER_DATA.get(), newData);
    }

    public boolean isIgnoreNBT() {
        return ignoreNBT;
    }

    public boolean isIgnoreDurability() {
        return ignoreDurability;
    }

    public SimpleContainer getFilterSlots() {
        return filterSlots;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int dragType, ContainerInput clickType, Player player) {
        if (slotId >= 0 && slotId < FILTER_SLOT_COUNT) {
            FilterSlot slot = (FilterSlot) slots.get(slotId);
            ItemStack carried = getCarried();
            slot.setFilterItem(carried.isEmpty() ? ItemStack.EMPTY : carried);
        } else {
            super.clicked(slotId, dragType, clickType, player);
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        saveFilterData();
    }

    @Override
    public boolean stillValid(Player player) {
        if (collectorPos != null) {
            BlockEntity be = player.level().getBlockEntity(collectorPos);
            return be instanceof CollectorBlockEntity && player.distanceToSqr(collectorPos.getX() + 0.5, collectorPos.getY() + 0.5, collectorPos.getZ() + 0.5) <= 64.0;
        }
        return player.getInventory().contains(filterItem);
    }

    private static class FilterSlot extends Slot {
        FilterSlot(SimpleContainer container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override public boolean mayPlace(ItemStack stack) { return false; }
        @Override public boolean mayPickup(Player player) { return true; }

        @Override
        public ItemStack remove(int amount) {
            ItemStack result = getItem().copy();
            set(ItemStack.EMPTY);
            return result;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            set(ItemStack.EMPTY);
        }

        @Override
        public boolean allowModification(Player player) {
            return true;
        }

        public void setFilterItem(ItemStack stack) {
            set(stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        }
    }
}