package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.entity.custom.GenesisChamberBlockEntity;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class GenesisChamberMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;
    private static final int SLOT_EGG = 0;
    private static final int SLOT_FUEL = 1;
    private static final int SLOT_MODULE_1 = 2;
    private static final int SLOT_MODULE_2 = 3;
    private static final int TE_SLOT_COUNT = 4;
    private static final int TE_FIRST_SLOT = PLAYER_SLOTS;
    private static final int TE_LAST_SLOT = TE_FIRST_SLOT + TE_SLOT_COUNT;

    public final GenesisChamberBlockEntity blockEntity;
    private final Level level;

    public GenesisChamberMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public GenesisChamberMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(MFUMenuTypes.GENESIS_CHAMBER_MENU.get(), containerId);
        this.blockEntity = (GenesisChamberBlockEntity) blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addBlockEntitySlots();
    }

    private void addBlockEntitySlots() {
        addSlot(new EggSlot(blockEntity, SLOT_EGG, 80, 23));
        addSlot(new FuelSlot(blockEntity, SLOT_FUEL, 80, 71));
        addSlot(new ChamberSlot(blockEntity, SLOT_MODULE_1, 8, 71, MFUItems.SPEED_MODULE.get()));
        addSlot(new ChamberSlot(blockEntity, SLOT_MODULE_2, 26, 71, MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get()));
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

        if (item instanceof SpawnEggItem) {
            if (!blockEntity.getStack(SLOT_EGG).isEmpty()) return false;
            return insertSingle(stack, SLOT_EGG);
        }

        if (stack.is(MFUTags.Items.GENESIS_CHAMBER_FUELS))
            return insertIntoSlot(stack, SLOT_FUEL);

        if (item == MFUItems.SPEED_MODULE.get())
            return insertIntoSlot(stack, SLOT_MODULE_1);

        if (item == MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get())
            return insertIntoSlot(stack, SLOT_MODULE_2);

        return false;
    }

    private boolean insertSingle(ItemStack stack, int slot) {
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = blockEntity.inventory.insert(slot, ItemResource.of(stack), 1, tx);
            if (inserted == 0) return false;
            tx.commit();
            stack.shrink(inserted);
            return true;
        }
    }

    private boolean insertIntoSlot(ItemStack stack, int slot) {
        int limit = slots.get(TE_FIRST_SLOT + slot).getMaxStackSize(stack);
        int existing = blockEntity.getStack(slot).getCount();
        int space = limit - existing;
        if (space <= 0) return false;
        int toInsert = Math.min(space, stack.getCount());
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = blockEntity.inventory.insert(slot, ItemResource.of(stack), toInsert, tx);
            if (inserted == 0) return false;
            tx.commit();
            stack.shrink(inserted);
            return true;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, MFUBlocks.GENESIS_CHAMBER.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 8 + i * 18, 163));
    }

    private static class ChamberSlot extends Slot {
        private final GenesisChamberBlockEntity be;
        private final int index;
        private final Item allowedItem;

        ChamberSlot(GenesisChamberBlockEntity be, int index, int x, int y, Item allowedItem) {
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
        public int getMaxStackSize(ItemStack stack) { return 10; }
    }

    private static class EggSlot extends Slot {
        private final GenesisChamberBlockEntity be;
        private final int index;

        EggSlot(GenesisChamberBlockEntity be, int index, int x, int y) {
            super(new SimpleContainer(be.inventory.size()), index, x, y);
            this.be = be;
            this.index = index;
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
        public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof SpawnEggItem; }

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
    }

    private static class FuelSlot extends Slot {
        private final GenesisChamberBlockEntity be;
        private final int index;

        FuelSlot(GenesisChamberBlockEntity be, int index, int x, int y) {
            super(new SimpleContainer(be.inventory.size()), index, x, y);
            this.be = be;
            this.index = index;
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
        public boolean mayPlace(ItemStack stack) { return stack.is(MFUTags.Items.GENESIS_CHAMBER_FUELS); }

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
    }
}