package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.entity.custom.FanBlockEntity;
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

public class FanMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;
    private static final int SLOT_WIDTH = 0;
    private static final int SLOT_HEIGHT = 1;
    private static final int SLOT_DISTANCE = 2;
    private static final int TE_SLOT_COUNT = 3;
    private static final int TE_FIRST_SLOT = PLAYER_SLOTS;
    private static final int TE_LAST_SLOT = TE_FIRST_SLOT + TE_SLOT_COUNT;

    public final FanBlockEntity blockEntity;
    private final Level level;

    public FanMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public FanMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(MFUMenuTypes.FAN_MENU.get(), containerId);
        this.blockEntity = (FanBlockEntity) blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addBlockEntitySlots();
    }

    private void addBlockEntitySlots() {
        addSlot(new FanSlot(blockEntity, SLOT_WIDTH, 44, 18, MFUItems.FAN_WIDTH_INCREASE_MODULE.get()));
        addSlot(new FanSlot(blockEntity, SLOT_HEIGHT, 80, 18, MFUItems.FAN_HEIGHT_INCREASE_MODULE.get()));
        addSlot(new FanSlot(blockEntity, SLOT_DISTANCE, 116, 18, MFUItems.FAN_DISTANCE_INCREASE_MODULE.get()));
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
        if (item == MFUItems.FAN_WIDTH_INCREASE_MODULE.get()) return insertIntoSlot(stack, SLOT_WIDTH);
        if (item == MFUItems.FAN_HEIGHT_INCREASE_MODULE.get()) return insertIntoSlot(stack, SLOT_HEIGHT);
        if (item == MFUItems.FAN_DISTANCE_INCREASE_MODULE.get()) return insertIntoSlot(stack, SLOT_DISTANCE);
        return false;
    }

    private boolean insertIntoSlot(ItemStack stack, int slot) {
        int limit = slots.get(TE_FIRST_SLOT + slot).getMaxStackSize(stack);
        int existing = blockEntity.getStack(slot).getCount();
        int space = limit - existing;
        if (space <= 0) return false;
        int toInsert = Math.min(space, stack.getCount());
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = blockEntity.inventory.insert(slot, ItemResource.of(stack), toInsert, tx);
            if (inserted == 0) { return false; }
            tx.commit();
            stack.shrink(inserted);
            return true;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, MFUBlocks.FAN.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 8 + i * 18, 110));
    }

    private static class FanSlot extends Slot {
        private final FanBlockEntity be;
        private final int index;
        private final Item allowedItem;

        FanSlot(FanBlockEntity be, int index, int x, int y, Item allowedItem) {
            super(new SimpleContainer(be.inventory.size()), index, x, y);
            this.be = be;
            this.index = index;
            this.allowedItem = allowedItem;
        }

        @Override public ItemStack getItem() { return be.getStack(index); }

        @Override
        public void set(ItemStack stack) {
            try (Transaction tx = Transaction.openRoot()) {
                ItemStack existing = be.getStack(index);
                if (!existing.isEmpty())
                    be.inventory.extract(index, ItemResource.of(existing), existing.getCount(), tx);
                if (!stack.isEmpty())
                    be.inventory.insert(index, ItemResource.of(stack), stack.getCount(), tx);
                tx.commit();
            }
            setChanged();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == allowedItem;
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
        public int getMaxStackSize(ItemStack stack) {
            return switch (index) {
                case 0, 1 -> 4;
                case 2 -> 10;
                default -> 1;
            };
        }
    }
}