package com.misterd.mobflowutilities.blockentity.custom;

import com.misterd.mobflowutilities.config.Config;
import com.misterd.mobflowutilities.blockentity.MFUBlockEntities;
import com.misterd.mobflowutilities.gui.custom.GenesisInfuserMenu;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;

public class GenesisInfuserBlockEntity extends BlockEntity implements MenuProvider {

    public static final int TANK_CAPACITY = 32000;

    private static final int INPUT_START = 0;
    private static final int INPUT_COUNT = 6;
    private static final int OUTPUT_START = 6;
    private static final int OUTPUT_COUNT = 6;
    private static final int SLOT_UPGRADE = 12;
    private static final int SLOT_COUNT = 13;

    private static final int MAX_UPGRADES = 10;
    private static final int TICKS_PER_UPGRADE = 15;

    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(SLOT_COUNT) {
        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (resource.isEmpty()) return false;
            ItemStack stack = resource.toStack();
            if (index >= INPUT_START && index < INPUT_START + INPUT_COUNT) {
                return stack.is(ItemTags.COALS);
            }
            if (index >= OUTPUT_START && index < OUTPUT_START + OUTPUT_COUNT) {
                return stack.getItem() == MFUItems.INFUSED_COAL.get()
                        || stack.getItem() == MFUItems.INFUSED_CHARCOAL.get();
            }
            if (index == SLOT_UPGRADE) {
                return stack.getItem() == MFUItems.SPEED_MODULE.get();
            }
            return false;
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            if (level != null && !level.isClientSide())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    public final InfuserFluidTank tank = new InfuserFluidTank();

    private int progress = 0;
    private int maxProgress = 0;

    public GenesisInfuserBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.GENESIS_INFUSER_BE.get(), pos, blockState);
    }

    public ItemStack getStack(int slot) {
        ItemResource res = inventory.getResource(slot);
        if (res.isEmpty()) return ItemStack.EMPTY;
        return res.toStack(inventory.getAmountAsInt(slot));
    }

    public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction direction) {
        return new ResourceHandler<>() {
            @Override
            public int size() {
                return INPUT_COUNT + OUTPUT_COUNT;
            }

            private int mapIndex(int index) {
                if (index < INPUT_COUNT) return INPUT_START + index;
                return OUTPUT_START + (index - INPUT_COUNT);
            }

            private boolean isInput(int index) {
                return index < INPUT_COUNT;
            }

            @Override
            public ItemResource getResource(int index) {
                return inventory.getResource(mapIndex(index));
            }

            @Override
            public long getAmountAsLong(int index) {
                return inventory.getAmountAsLong(mapIndex(index));
            }

            @Override
            public long getCapacityAsLong(int index, ItemResource resource) {
                return inventory.getCapacityAsLong(mapIndex(index), resource);
            }

            @Override
            public boolean isValid(int index, ItemResource resource) {
                if (isInput(index)) {
                    return !resource.isEmpty() && resource.toStack().is(ItemTags.COALS);
                }
                return false;
            }

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext tx) {
                if (!isInput(index)) return 0;
                if (!isValid(index, resource)) return 0;
                return inventory.insert(mapIndex(index), resource, amount, tx);
            }

            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext tx) {
                if (isInput(index)) return 0;
                return inventory.extract(mapIndex(index), resource, amount, tx);
            }
        };
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

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getFluidAmount() {
        return tank.amount;
    }

    public FluidResource getFluidResource() {
        return tank.resource;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        drops();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) inv.setItem(i, getStack(i));
        Containers.dropContents(level, worldPosition, inv);
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        boolean changed = false;

        int inputSlot = findProcessableInput();

        if (inputSlot < 0 || tank.amount < Config.getGenesisInfuserXpCost()) {
            if (progress != 0) {
                progress = 0;
                changed = true;
            }
        } else {
            maxProgress = getProcessTicks();
            progress++;
            changed = true;
            if (progress >= maxProgress) {
                if (processInput(inputSlot)) {
                    progress = 0;
                } else {
                    progress = maxProgress;
                }
            }
        }

        if (changed) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private int getProcessTicks() {
        int modules = Math.min(MAX_UPGRADES, inventory.getAmountAsInt(SLOT_UPGRADE));
        return Math.max(1, Config.getGenesisInfuserProcessTicks() - (modules * TICKS_PER_UPGRADE));
    }

    private int findProcessableInput() {
        for (int i = INPUT_START; i < INPUT_START + INPUT_COUNT; i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty()) continue;
            ItemStack result = getResultFor(stack);
            if (result.isEmpty()) continue;
            if (canFitOutput(result)) return i;
        }
        return -1;
    }

    private ItemStack getResultFor(ItemStack stack) {
        if (stack.is(Items.COAL)) return new ItemStack(MFUItems.INFUSED_COAL.get());
        if (stack.is(Items.CHARCOAL)) return new ItemStack(MFUItems.INFUSED_CHARCOAL.get());
        return ItemStack.EMPTY;
    }

    private boolean canFitOutput(ItemStack result) {
        ItemResource res = ItemResource.of(result);
        for (int i = OUTPUT_START; i < OUTPUT_START + OUTPUT_COUNT; i++) {
            ItemStack cur = getStack(i);
            if (cur.isEmpty()) return true;
            if (ItemResource.of(cur).equals(res) && cur.getCount() < cur.getMaxStackSize()) return true;
        }
        return false;
    }

    private boolean depositOutput(ItemStack result) {
        ItemResource res = ItemResource.of(result);
        for (int i = OUTPUT_START; i < OUTPUT_START + OUTPUT_COUNT; i++) {
            ItemStack cur = getStack(i);
            if (cur.isEmpty()) {
                inventory.set(i, res, 1);
                return true;
            }
            if (ItemResource.of(cur).equals(res) && cur.getCount() < cur.getMaxStackSize()) {
                inventory.set(i, res, cur.getCount() + 1);
                return true;
            }
        }
        return false;
    }

    private boolean processInput(int inputSlot) {
        ItemStack input = getStack(inputSlot);
        if (input.isEmpty()) return false;
        ItemStack result = getResultFor(input);
        if (result.isEmpty()) return false;

        int cost = Config.getGenesisInfuserXpCost();
        if (tank.amount < cost) return false;

        if (!canFitOutput(result)) return false;

        try (Transaction tx = Transaction.openRoot()) {
            int extracted = inventory.extract(inputSlot, ItemResource.of(input), 1, tx);
            if (extracted != 1) return false;
            tx.commit();
        }

        if (!depositOutput(result)) return false;

        tank.amount -= cost;
        if (tank.amount <= 0) {
            tank.amount = 0;
            tank.resource = FluidResource.EMPTY;
        }
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        return true;
    }

    public class InfuserFluidTank implements ResourceHandler<FluidResource> {

        private FluidResource resource = FluidResource.EMPTY;
        private int amount = 0;
        private final Journal journal = new Journal();

        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            return resource;
        }

        @Override
        public long getAmountAsLong(int index) {
            return amount;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            return TANK_CAPACITY;
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            if (resource.isEmpty()) return true;
            if (!resource.is(MFUTags.Fluids.EXPERIENCE)) return false;
            return this.resource.isEmpty() || this.resource.equals(resource);
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (!isValid(index, resource)) return 0;
            int accepted = Math.min(amount, TANK_CAPACITY - this.amount);
            if (accepted <= 0) return 0;
            journal.updateSnapshots(transaction);
            this.resource = resource;
            this.amount += accepted;
            return accepted;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (this.resource.isEmpty()) return 0;
            if (!resource.equals(this.resource)) return 0;
            int extracted = Math.min(amount, this.amount);
            if (extracted <= 0) return 0;
            journal.updateSnapshots(transaction);
            this.amount -= extracted;
            if (this.amount <= 0) {
                this.amount = 0;
                this.resource = FluidResource.EMPTY;
            }
            return extracted;
        }

        private class Journal extends SnapshotJournal<int[]> {
            @Override
            protected int[] createSnapshot() {
                return new int[]{amount};
            }

            @Override
            protected void revertToSnapshot(int[] snapshot) {
                amount = snapshot[0];
                if (amount <= 0) {
                    amount = 0;
                    resource = FluidResource.EMPTY;
                }
            }

            @Override
            protected void onRootCommit(int[] originalState) {
                setChanged();
                if (level != null && !level.isClientSide())
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output);
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putInt("fluidAmount", tank.amount);
        if (!tank.resource.isEmpty()) {
            output.putString("fluidId", BuiltInRegistries.FLUID.getKey(tank.resource.getFluid()).toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input);
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", 0);
        tank.amount = input.getIntOr("fluidAmount", 0);
        String fluidId = input.getStringOr("fluidId", "");
        if (!fluidId.isEmpty() && tank.amount > 0) {
            BuiltInRegistries.FLUID
                    .getOptional(net.minecraft.resources.Identifier.parse(fluidId))
                    .ifPresent(f -> tank.resource = FluidResource.of(f));
        } else {
            tank.resource = FluidResource.EMPTY;
            tank.amount = 0;
        }
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