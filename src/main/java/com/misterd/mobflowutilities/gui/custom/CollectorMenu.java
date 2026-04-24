package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class CollectorMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;
    private static final int SLOT_MODULE_RADIUS = 0;
    private static final int SLOT_MODULE_VOID_1 = 1;
    private static final int SLOT_MODULE_VOID_2 = 2;
    private static final int SLOT_MODULE_VOID_3 = 3;
    private static final int MODULE_SLOT_COUNT = 4;
    private static final int OUTPUT_SLOT_COUNT = 45;
    private static final int TE_SLOT_COUNT = MODULE_SLOT_COUNT + OUTPUT_SLOT_COUNT;
    private static final int TE_FIRST_SLOT = PLAYER_SLOTS;
    private static final int TE_LAST_SLOT = TE_FIRST_SLOT + TE_SLOT_COUNT;
    private static final int OUTPUT_FIRST_SLOT = TE_FIRST_SLOT + MODULE_SLOT_COUNT;

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
        addBlockEntitySlots();
    }

    private void addBlockEntitySlots() {
        addSlot(new CollectorSlot(blockEntity, SLOT_MODULE_RADIUS, 192, 41, MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get()));
        addSlot(new CollectorSlot(blockEntity, SLOT_MODULE_VOID_1, 174, 18, MFUItems.VOID_FILTER_MODULE.get()));
        addSlot(new CollectorSlot(blockEntity, SLOT_MODULE_VOID_2, 192, 18, MFUItems.VOID_FILTER_MODULE.get()));
        addSlot(new CollectorSlot(blockEntity, SLOT_MODULE_VOID_3, 210, 18, MFUItems.VOID_FILTER_MODULE.get()));

        int idx = MODULE_SLOT_COUNT;
        for (int row = 0; row < 5; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new CollectorSlot(blockEntity, idx++, 8 + col * 18, 18 + row * 18, null));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (source == null || !source.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = source.getItem();
        ItemStack copy  = stack.copy();

        if (index < PLAYER_SLOTS) {
            if (!moveToBlockEntity(stack)) return ItemStack.EMPTY;
        } else {
            if (index >= TE_LAST_SLOT) return ItemStack.EMPTY;
            if (!moveItemStackTo(stack, 0, PLAYER_SLOTS, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) source.set(ItemStack.EMPTY);
        else source.setChanged();

        source.onTake(player, stack);
        return copy;
    }

    private boolean moveToBlockEntity(ItemStack stack) {
        Item item = stack.getItem();

        if (item == MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get()) {
            ItemStack existing = blockEntity.getStack(SLOT_MODULE_RADIUS);
            int space = 8 - existing.getCount();
            if (space <= 0) return false;
            int toInsert = Math.min(space, stack.getCount());
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = blockEntity.inventory.insert(SLOT_MODULE_RADIUS, ItemResource.of(stack), toInsert, tx);
                if (inserted == 0) return false;
                tx.commit();
                stack.shrink(inserted);
                return true;
            }
        }

        if (item == MFUItems.VOID_FILTER_MODULE.get()) {
            for (int slot = SLOT_MODULE_VOID_1; slot <= SLOT_MODULE_VOID_3; slot++) {
                if (blockEntity.getStack(slot).isEmpty()) {
                    try (Transaction tx = Transaction.openRoot()) {
                        int inserted = blockEntity.inventory.insert(slot, ItemResource.of(stack), 1, tx);
                        if (inserted == 0) continue;
                        tx.commit();
                        stack.shrink(inserted);
                        return true;
                    }
                }
            }
            return false;
        }

        for (int slot = MODULE_SLOT_COUNT; slot < MODULE_SLOT_COUNT + OUTPUT_SLOT_COUNT; slot++) {
            ItemStack existing = blockEntity.getStack(slot);
            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)) {
                int space = existing.getMaxStackSize() - existing.getCount();
                if (space <= 0) continue;
                int toInsert = Math.min(space, stack.getCount());
                try (Transaction tx = Transaction.openRoot()) {
                    int inserted = blockEntity.inventory.insert(slot, ItemResource.of(stack), toInsert, tx);
                    if (inserted == 0) continue;
                    tx.commit();
                    stack.shrink(inserted);
                    if (stack.isEmpty()) return true;
                }
            }
        }
        for (int slot = MODULE_SLOT_COUNT; slot < MODULE_SLOT_COUNT + OUTPUT_SLOT_COUNT; slot++) {
            if (blockEntity.getStack(slot).isEmpty()) {
                try (Transaction tx = Transaction.openRoot()) {
                    int inserted = blockEntity.inventory.insert(slot, ItemResource.of(stack), stack.getCount(), tx);
                    if (inserted == 0) continue;
                    tx.commit();
                    stack.shrink(inserted);
                    if (stack.isEmpty()) return true;
                }
            }
        }

        return false;
    }

    private void insertSingle(ItemStack stack, int slot) {
        try (Transaction tx = Transaction.openRoot()) {
            blockEntity.inventory.insert(slot, ItemResource.of(stack), 1, tx);
            tx.commit();
        }
        stack.shrink(1);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, MFUBlocks.COLLECTOR.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 37 + col * 18, 159 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 37 + i * 18, 218));
    }

    private static class CollectorSlot extends Slot {
        private final CollectorBlockEntity be;
        private final int index;
        private final Item allowedItem;

        CollectorSlot(CollectorBlockEntity be, int index, int x, int y, Item allowedItem) {
            super(new SimpleContainer(be.inventory.size()), index, x, y);
            this.be = be;
            this.index = index;
            this.allowedItem = allowedItem;
        }

        @Override
        public ItemStack getItem() {
            return be.getStack(index);
        }

        @Override
        public boolean hasItem() {
            return !be.getStack(index).isEmpty();
        }

        @Override
        public void set(ItemStack stack) {
            ItemStack existing = be.getStack(index);
            try (Transaction tx = Transaction.openRoot()) {
                if (!existing.isEmpty())
                    be.inventory.extract(index, ItemResource.of(existing), existing.getCount(), tx);
                if (!stack.isEmpty())
                    be.inventory.insert(index, ItemResource.of(stack), stack.getCount(), tx);
                tx.commit();
            }
            setChanged();
        }

        @Override
        public void setChanged() {
            be.setChanged();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return be.inventory.isValid(index, ItemResource.of(stack));
        }

        @Override
        public boolean mayPickup(Player player) {
            return !be.getStack(index).isEmpty();
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack existing = getItem();
            if (existing.isEmpty()) return ItemStack.EMPTY;
            int toExtract = Math.min(amount, existing.getCount());
            try (Transaction tx = Transaction.openRoot()) {
                int extracted = be.inventory.extract(index, ItemResource.of(existing), toExtract, tx);
                tx.commit();
                return new ItemStack(existing.getItem(), extracted);
            }
        }

        @Override
        public int getMaxStackSize() {
            return (int) be.inventory.getCapacityAsLong(index, be.inventory.getResource(index));
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return (int) be.inventory.getCapacityAsLong(index, ItemResource.of(stack));
        }
    }
}