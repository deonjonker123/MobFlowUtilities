package com.misterd.mobflowutilities.gui.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.blockentity.custom.GenesisInfuserBlockEntity;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GenesisInfuserMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOTS = 36;
    private static final int TE_FIRST_SLOT = PLAYER_SLOTS;
    private static final int TE_LAST_SLOT = TE_FIRST_SLOT + GenesisInfuserBlockEntity.SLOT_COUNT;

    public final GenesisInfuserBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

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

        this.data = level.isClientSide() ? new SimpleContainerData(2) : this.blockEntity.getContainerData();
        addDataSlots(data);
    }

    private void addBlockEntitySlots() {
        IItemHandler handler = this.blockEntity.inventory;
        int index = GenesisInfuserBlockEntity.INPUT_START;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                addSlot(new InputSlot(handler, index++, 44 + col * 18, 19 + row * 18));
            }
        }

        index = GenesisInfuserBlockEntity.OUTPUT_START;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                addSlot(new OutputSlot(handler, index++, 98 + col * 18, 19 + row * 18));
            }
        }

        addSlot(new UpgradeSlot(handler, GenesisInfuserBlockEntity.SLOT_UPGRADE, 152, 19));
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (source == null || !source.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = source.getItem();
        ItemStack copy = stack.copy();

        if (index < PLAYER_SLOTS) {
            if (!moveItemStackTo(stack, TE_FIRST_SLOT, TE_LAST_SLOT, false)) return ItemStack.EMPTY;
        } else if (index < TE_LAST_SLOT) {
            if (!moveItemStackTo(stack, 0, PLAYER_SLOTS, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) source.set(ItemStack.EMPTY);
        else source.setChanged();

        source.onTake(player, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, MFUBlocks.GENESIS_INFUSER.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 88 + row * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; ++i)
            addSlot(new Slot(inv, i, 8 + i * 18, 146));
    }

    private static class InputSlot extends SlotItemHandler {
        InputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ItemTags.COALS);
        }
    }

    private static class OutputSlot extends SlotItemHandler {
        OutputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private static class UpgradeSlot extends SlotItemHandler {
        UpgradeSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == MFUItems.SPEED_MODULE.get();
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return GenesisInfuserBlockEntity.MAX_UPGRADES;
        }
    }
}
