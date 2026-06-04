package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.entity.custom.GenesisInfuserBlockEntity;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class GenesisInfuserMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;
    private static final int INPUT_START = 0;
    private static final int INPUT_COUNT = 6;
    private static final int OUTPUT_START = 6;
    private static final int OUTPUT_COUNT = 6;
    private static final int SLOT_UPGRADE = 12;
    private static final int TE_SLOT_COUNT = 13;
    private static final int TE_FIRST_SLOT = PLAYER_SLOTS;
    private static final int TE_LAST_SLOT = TE_FIRST_SLOT + TE_SLOT_COUNT;

    public final GenesisInfuserBlockEntity blockEntity;
    private final Level level;

    private DataSlot progressData;
    private DataSlot maxProgressData;

    public GenesisInfuserMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public GenesisInfuserMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(MFUMenuTypes.GENESIS_INFUSER_MENU.get(), containerId);
        this.blockEntity = (GenesisInfuserBlockEntity) blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addBlockEntitySlots();
        addProgressData();
    }

    private void addBlockEntitySlots() {
        int index = INPUT_START;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                addSlot(new InputSlot(blockEntity, index, 44 + col * 18, 19 + row * 18));
                index++;
            }
        }

        index = OUTPUT_START;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                addSlot(new OutputSlot(blockEntity, index, 98 + col * 18, 19 + row * 18));
                index++;
            }
        }

        addSlot(new UpgradeSlot(blockEntity, SLOT_UPGRADE, 152, 19));
    }

    private void addProgressData() {
        progressData = addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getProgress();
            }

            @Override
            public void set(int value) {
            }
        });
        maxProgressData = addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getMaxProgress();
            }

            @Override
            public void set(int value) {
            }
        });
    }

    public int getProgress() {
        return progressData.get();
    }

    public int getMaxProgress() {
        return maxProgressData.get();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (source == null || !source.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = source.getItem();
        ItemStack copy = stack.copy();

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
        if (stack.getItem() == MFUItems.SPEED_MODULE.get())
            return insertIntoSlot(stack, SLOT_UPGRADE);

        if (stack.is(ItemTags.COALS))
            return insertIntoInputs(stack);

        return false;
    }

    private boolean insertIntoInputs(ItemStack stack) {
        boolean moved = false;
        for (int slot = INPUT_START; slot < INPUT_START + INPUT_COUNT; slot++) {
            if (stack.isEmpty()) break;
            if (insertIntoSlot(stack, slot)) moved = true;
        }
        return moved;
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
                player, MFUBlocks.GENESIS_INFUSER.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 88 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 8 + i * 18, 146));
    }

    private abstract static class InfuserSlot extends Slot {
        protected final GenesisInfuserBlockEntity be;
        protected final int index;

        InfuserSlot(GenesisInfuserBlockEntity be, int index, int x, int y) {
            super(new SimpleContainer(be.inventory.size()), index, x, y);
            this.be = be;
            this.index = index;
        }

        @Override
        public ItemStack getItem() {
            return be.getStack(index);
        }

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

    private static class InputSlot extends InfuserSlot {
        InputSlot(GenesisInfuserBlockEntity be, int index, int x, int y) {
            super(be, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ItemTags.COALS);
        }
    }

    private static class OutputSlot extends InfuserSlot {
        OutputSlot(GenesisInfuserBlockEntity be, int index, int x, int y) {
            super(be, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private static class UpgradeSlot extends InfuserSlot {
        UpgradeSlot(GenesisInfuserBlockEntity be, int index, int x, int y) {
            super(be, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == MFUItems.SPEED_MODULE.get();
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 10;
        }
    }
}