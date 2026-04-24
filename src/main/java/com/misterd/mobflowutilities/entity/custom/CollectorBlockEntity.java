package com.misterd.mobflowutilities.entity.custom;

import com.misterd.mobflowutilities.block.custom.CollectorBlock;
import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.component.custom.VoidFilterData;
import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.gui.custom.CollectorMenu;
import com.misterd.mobflowutilities.item.custom.VoidFilterItem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public class CollectorBlockEntity extends BlockEntity implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int COLLECTION_INTERVAL = 5;

    private static final int SLOT_RADIUS  = 0;
    private static final int SLOT_VOID_1  = 1;
    private static final int SLOT_VOID_2  = 2;
    private static final int SLOT_VOID_3  = 3;
    private static final int MODULE_COUNT = 4;
    private static final int OUTPUT_COUNT = 45;
    private static final int TOTAL_SLOTS  = MODULE_COUNT + OUTPUT_COUNT;

    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(TOTAL_SLOTS) {
        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            if (index == SLOT_RADIUS) return 8;
            if (index < MODULE_COUNT) return 1;
            return resource.toStack().getMaxStackSize();
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (resource.isEmpty()) return false;
            if (index < MODULE_COUNT) return false;
            return true;
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            if (index == SLOT_RADIUS) invalidatePickupRangeCache();
            CollectorBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    public final ModuleSlotsView moduleSlots = new ModuleSlotsView();

    public class ModuleSlotsView {
        public ItemStack getStackInSlot(int slot) { return getStack(slot); }
        public int getSlots() { return MODULE_COUNT; }
        public int getSlotLimit(int slot) { return slot == SLOT_RADIUS ? 8 : 1; }
    }

    private AABB cachedCollectionArea;
    private boolean collectionAreaDirty = true;
    private int cachedPickupRange = -1;
    private boolean pickupRangeDirty = true;
    private int tickCounter = 0;

    private int storedXP = 0;
    private boolean xpCollectionEnabled = false;
    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;

    public CollectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.COLLECTOR_BE.get(), pos, blockState);
    }

    @Nullable
    public ItemStacksResourceHandler getItemHandler(@Nullable Direction direction) {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.mobflowutilities.collector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CollectorMenu(id, inv, this);
    }

    public ItemStack getStack(int slot) {
        ItemResource res = inventory.getResource(slot);
        if (res.isEmpty()) return ItemStack.EMPTY;
        return res.toStack(inventory.getAmountAsInt(slot));
    }

    public int getStoredXP() {
        return storedXP;
    }

    public void setStoredXP(int xp) {
        int old = storedXP;
        storedXP = Math.max(0, xp);
        if (old != storedXP) setChangedAndUpdate();
    }

    public boolean isXpCollectionEnabled() { return xpCollectionEnabled; }

    public void setXpCollectionEnabled(boolean enabled) {
        if (xpCollectionEnabled == enabled) return;
        xpCollectionEnabled = enabled;
        setChanged();
        if (level != null && !level.isClientSide()) {
            CollectorBlock.updateXpCollectionState(level, worldPosition, enabled);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public int getDownUpOffset() {
        return downUpOffset;
    }

    public int getNorthSouthOffset() {
        return northSouthOffset;
    }

    public int getEastWestOffset() {
        return eastWestOffset;
    }

    public void setDownUpOffset(int v) {
        setOffset(downUpOffset, v, val -> downUpOffset = val);
    }

    public void setNorthSouthOffset(int v) {
        setOffset(northSouthOffset, v, val -> northSouthOffset = val);
    }

    public void setEastWestOffset(int v) {
        setOffset(eastWestOffset, v, val -> eastWestOffset = val);
    }

    private void setOffset(int current, int offset, java.util.function.IntConsumer setter) {
        int clamped = Math.max(-10, Math.min(10, offset));
        if (current != clamped) {
            setter.accept(clamped);
            invalidateCollectionAreaCache();
            setChanged();
        }
    }

    public int getPickupRange() {
        if (pickupRangeDirty || cachedPickupRange == -1) {
            cachedPickupRange = 3 + inventory.getAmountAsInt(SLOT_RADIUS);
            pickupRangeDirty = false;
        }
        return cachedPickupRange;
    }

    private void invalidateCollectionAreaCache() {
        collectionAreaDirty = true;
    }

    private void invalidatePickupRangeCache() {
        pickupRangeDirty = true; invalidateCollectionAreaCache();
    }

    public  void invalidateAllCaches() {
        invalidatePickupRangeCache();
    }

    private AABB getCollectionArea() {
        if (collectionAreaDirty || cachedCollectionArea == null) {
            int r = getPickupRange();
            BlockPos pos = getBlockPos();
            cachedCollectionArea = new AABB(
                    pos.getX() - r + eastWestOffset, pos.getY() - r + downUpOffset, pos.getZ() - r + northSouthOffset,
                    pos.getX() + r + 1 + eastWestOffset, pos.getY() + r + 1 + downUpOffset, pos.getZ() + r + 1 + northSouthOffset
            );
            collectionAreaDirty = false;
        }
        return cachedCollectionArea;
    }

    public void loadFromBlockItem(ItemStack stack) {
        if (level == null) return;
        CustomData customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (customData == null) return;

        CompoundTag tag = customData.copyTag();
        storedXP = tag.getIntOr("storedXP", 0);
        xpCollectionEnabled = tag.getBooleanOr("xpCollectionEnabled", false);
        downUpOffset = tag.getIntOr("downUpOffset", 0);
        northSouthOffset = tag.getIntOr("northSouthOffset", 0);
        eastWestOffset = tag.getIntOr("eastWestOffset", 0);

        if (!level.isClientSide())
            CollectorBlock.updateXpCollectionState(level, worldPosition, xpCollectionEnabled);

        setChangedAndUpdate();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(TOTAL_SLOTS);
        for (int i = 0; i < TOTAL_SLOTS; i++) inv.setItem(i, getStack(i));
        Containers.dropContents(level, worldPosition, inv);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output);
        output.putInt("storedXP", storedXP);
        output.putBoolean("xpCollectionEnabled", xpCollectionEnabled);
        output.putInt("downUpOffset", downUpOffset);
        output.putInt("northSouthOffset", northSouthOffset);
        output.putInt("eastWestOffset", eastWestOffset);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input);
        storedXP = input.getIntOr("storedXP", 0);
        xpCollectionEnabled = input.getBooleanOr("xpCollectionEnabled", false);
        downUpOffset = input.getIntOr("downUpOffset", 0);
        northSouthOffset = input.getIntOr("northSouthOffset", 0);
        eastWestOffset = input.getIntOr("eastWestOffset", 0);
        invalidateAllCaches();
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

    public void tick() {
        if (level == null || level.isClientSide()) return;
        if (++tickCounter >= COLLECTION_INTERVAL) {
            tickCounter = 0;
            collectItems();
            if (xpCollectionEnabled) collectXP();
        }
    }

    private void collectItems() {
        AABB area = getCollectionArea();
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, area)) {
            if (!itemEntity.isAlive() || itemEntity.hasPickUpDelay()) continue;
            ItemStack stack = itemEntity.getItem().copy();

            if (shouldVoidItem(stack)) {
                ItemStack current = itemEntity.getItem();
                if (stack.getCount() >= current.getCount()) itemEntity.discard();
                else itemEntity.setItem(current.copyWithCount(current.getCount() - stack.getCount()));
            } else {
                int inserted = insertIntoOutput(stack);
                if (inserted > 0) {
                    ItemStack current = itemEntity.getItem();
                    if (inserted >= current.getCount()) itemEntity.discard();
                    else itemEntity.setItem(current.copyWithCount(current.getCount() - inserted));
                }
            }
        }
    }

    private boolean shouldVoidItem(ItemStack stack) {
        for (int i = SLOT_VOID_1; i <= SLOT_VOID_3; i++) {
            ItemStack module = getStack(i);
            if (module.getItem() instanceof VoidFilterItem) {
                VoidFilterData data = module.getOrDefault(MFUDataComponents.VOID_FILTER_DATA.get(), VoidFilterData.DEFAULT);
                if (data.shouldVoidItem(stack)) return true;
            }
        }
        return false;
    }

    private void collectXP() {
        AABB area = getCollectionArea();
        int collected = 0;
        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, area)) {
            if (!orb.isAlive()) continue;
            collected += orb.getValue();
            orb.discard();
        }
        if (collected > 0) { storedXP += collected; setChangedAndUpdate(); }
    }

    private int insertIntoOutput(ItemStack stack) {
        int totalInserted = 0;
        ItemResource res = ItemResource.of(stack);
        int remaining = stack.getCount();

        for (int i = MODULE_COUNT; i < TOTAL_SLOTS && remaining > 0; i++) {
            ItemStack existing = getStack(i);
            if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, stack)) continue;
            int space = existing.getMaxStackSize() - existing.getCount();
            if (space <= 0) continue;
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = inventory.insert(i, res, Math.min(space, remaining), tx);
                tx.commit();
                remaining -= inserted;
                totalInserted += inserted;
            }
        }

        for (int i = MODULE_COUNT; i < TOTAL_SLOTS && remaining > 0; i++) {
            if (!getStack(i).isEmpty()) continue;
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = inventory.insert(i, res, Math.min(stack.getMaxStackSize(), remaining), tx);
                tx.commit();
                remaining -= inserted;
                totalInserted += inserted;
            }
        }

        return totalInserted;
    }

    private void setChangedAndUpdate() {
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, be) -> {
            if (be instanceof CollectorBlockEntity collector) collector.tick();
        };
    }
}