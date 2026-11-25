package com.misterd.mobflowutilities.blockentity.custom;

import com.misterd.mobflowutilities.blockentity.MFUBlockEntities;
import com.misterd.mobflowutilities.config.Config;
import com.misterd.mobflowutilities.gui.custom.GenesisInfuserMenu;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class GenesisInfuserBlockEntity extends BlockEntity implements MenuProvider {

    public static final int TANK_CAPACITY = 32000;

    public static final int INPUT_START = 0;
    public static final int INPUT_COUNT = 6;
    public static final int OUTPUT_START = 6;
    public static final int OUTPUT_COUNT = 6;
    public static final int SLOT_UPGRADE = 12;
    public static final int SLOT_COUNT = 13;

    public static final int MAX_UPGRADES = 10;
    private static final int TICKS_PER_UPGRADE = 15;

    public final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_UPGRADE ? MAX_UPGRADES : super.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot >= INPUT_START && slot < INPUT_START + INPUT_COUNT) return stack.is(ItemTags.COALS);
            if (slot >= OUTPUT_START && slot < OUTPUT_START + OUTPUT_COUNT) {
                return stack.getItem() == MFUItems.INFUSED_COAL.get() || stack.getItem() == MFUItems.INFUSED_CHARCOAL.get();
            }
            if (slot == SLOT_UPGRADE) return stack.getItem() == MFUItems.SPEED_MODULE.get();
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChangedAndUpdate();
        }
    };

    public final FluidTank tank = new FluidTank(TANK_CAPACITY, stack -> stack.is(MFUTags.Fluids.EXPERIENCE)) {
        @Override
        protected void onContentsChanged() {
            setChangedAndUpdate();
        }
    };

    private int progress = 0;
    private int maxProgress = 0;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? progress : maxProgress;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public GenesisInfuserBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.GENESIS_INFUSER_BE.get(), pos, blockState);
    }

    public IItemHandler getItemHandler(@Nullable Direction direction) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return INPUT_COUNT + OUTPUT_COUNT;
            }

            private int mapIndex(int index) {
                return index < INPUT_COUNT ? INPUT_START + index : OUTPUT_START + (index - INPUT_COUNT);
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(mapIndex(slot));
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot >= INPUT_COUNT || !stack.is(ItemTags.COALS)) return stack;
                return inventory.insertItem(mapIndex(slot), stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < INPUT_COUNT) return ItemStack.EMPTY;
                return inventory.extractItem(mapIndex(slot), amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(mapIndex(slot));
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return slot < INPUT_COUNT && stack.is(ItemTags.COALS);
            }
        };
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    public FluidStack getFluid() {
        return tank.getFluid();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        boolean changed = false;
        int inputSlot = findProcessableInput();

        if (inputSlot < 0 || tank.getFluidAmount() < Config.getGenesisInfuserXpCost()) {
            if (progress != 0) {
                progress = 0;
                changed = true;
            }
        } else {
            maxProgress = getProcessTicks();
            progress++;
            changed = true;
            if (progress >= maxProgress) {
                progress = processInput(inputSlot) ? 0 : maxProgress;
            }
        }

        if (changed) setChanged();
    }

    private int getProcessTicks() {
        int modules = Math.min(MAX_UPGRADES, inventory.getStackInSlot(SLOT_UPGRADE).getCount());
        return Math.max(1, Config.getGenesisInfuserProcessTicks() - (modules * TICKS_PER_UPGRADE));
    }

    private int findProcessableInput() {
        for (int i = INPUT_START; i < INPUT_START + INPUT_COUNT; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ItemStack result = getResultFor(stack);
            if (result.isEmpty()) continue;
            if (findOutputSlot(result) >= 0) return i;
        }
        return -1;
    }

    private ItemStack getResultFor(ItemStack stack) {
        if (stack.is(Items.COAL)) return new ItemStack(MFUItems.INFUSED_COAL.get());
        if (stack.is(Items.CHARCOAL)) return new ItemStack(MFUItems.INFUSED_CHARCOAL.get());
        return ItemStack.EMPTY;
    }

    private int findOutputSlot(ItemStack result) {
        for (int i = OUTPUT_START; i < OUTPUT_START + OUTPUT_COUNT; i++) {
            if (inventory.insertItem(i, result.copy(), true).isEmpty()) return i;
        }
        return -1;
    }

    private boolean processInput(int inputSlot) {
        ItemStack input = inventory.getStackInSlot(inputSlot);
        if (input.isEmpty()) return false;
        ItemStack result = getResultFor(input);
        if (result.isEmpty()) return false;

        int cost = Config.getGenesisInfuserXpCost();
        if (tank.drain(cost, IFluidHandler.FluidAction.SIMULATE).getAmount() < cost) return false;

        int outputSlot = findOutputSlot(result);
        if (outputSlot < 0) return false;
        if (inventory.extractItem(inputSlot, 1, true).isEmpty()) return false;

        inventory.extractItem(inputSlot, 1, false);
        inventory.insertItem(outputSlot, result, false);
        tank.drain(cost, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    private void setChangedAndUpdate() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.put("tank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        tank.readFromNBT(registries, tag.getCompound("tank"));
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.mobflowutilities.genesis_infuser");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new GenesisInfuserMenu(id, inv, this);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
