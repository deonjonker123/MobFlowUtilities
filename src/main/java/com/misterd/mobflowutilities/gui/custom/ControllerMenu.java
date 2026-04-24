package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.entity.custom.ControllerBlockEntity;
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

public class ControllerMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;
    private static final int SLOT_SHARPNESS = 0;
    private static final int SLOT_FIRE_ASPECT = 1;
    private static final int SLOT_SMITE = 2;
    private static final int SLOT_BOA = 3;
    private static final int SLOT_LOOTING = 4;
    private static final int TE_SLOT_COUNT = 5;
    private static final int TE_FIRST_SLOT = PLAYER_SLOTS;
    private static final int TE_LAST_SLOT = TE_FIRST_SLOT + TE_SLOT_COUNT;

    public final ControllerBlockEntity blockEntity;
    private final Level level;

    public ControllerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ControllerMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(MFUMenuTypes.CONTROLLER_MENU.get(), containerId);
        this.blockEntity = (ControllerBlockEntity) blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addBlockEntitySlots();
    }

    private void addBlockEntitySlots() {
        addSlot(new ControllerSlot(blockEntity, SLOT_SHARPNESS, 8, 18, MFUItems.SHARPNESS_MODULE.get()));
        addSlot(new ControllerSlot(blockEntity, SLOT_FIRE_ASPECT, 44, 18, MFUItems.FIRE_ASPECT_MODULE.get()));
        addSlot(new ControllerSlot(blockEntity, SLOT_SMITE, 80, 18, MFUItems.SMITE_MODULE.get()));
        addSlot(new ControllerSlot(blockEntity, SLOT_BOA, 116, 18, MFUItems.BOA_MODULE.get()));
        addSlot(new ControllerSlot(blockEntity, SLOT_LOOTING, 152, 18, MFUItems.LOOTING_MODULE.get()));
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
        if (item == MFUItems.SHARPNESS_MODULE.get()) return insertSingle(stack, SLOT_SHARPNESS);
        if (item == MFUItems.FIRE_ASPECT_MODULE.get()) return insertSingle(stack, SLOT_FIRE_ASPECT);
        if (item == MFUItems.SMITE_MODULE.get()) return insertSingle(stack, SLOT_SMITE);
        if (item == MFUItems.BOA_MODULE.get()) return insertSingle(stack, SLOT_BOA);
        if (item == MFUItems.LOOTING_MODULE.get()) return insertSingle(stack, SLOT_LOOTING);
        return false;
    }

    private boolean insertSingle(ItemStack stack, int slot) {
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = blockEntity.inventory.insert(slot, ItemResource.of(stack), stack.getCount(), tx);
            if (inserted == 0) return false;
            tx.commit();
            stack.shrink(inserted);
            return true;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, MFUBlocks.CONTROLLER.get());
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

    private static class ControllerSlot extends Slot {
        private final ControllerBlockEntity be;
        private final int index;
        private final Item allowedItem;

        ControllerSlot(ControllerBlockEntity be, int index, int x, int y, Item allowedItem) {
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
        public int getMaxStackSize() {
            return 10;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 10;
        }
    }
}