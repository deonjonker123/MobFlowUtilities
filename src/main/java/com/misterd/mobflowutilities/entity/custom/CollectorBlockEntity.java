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
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class CollectorBlockEntity extends BlockEntity implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int COLLECTION_INTERVAL = 5;

    private AABB cachedCollectionArea;
    private boolean collectionAreaDirty = true;
    private int cachedPickupRange = -1;
    private boolean pickupRangeDirty = true;
    private int tickCounter = 0;

    public final ItemStackHandler moduleSlots = new ItemStackHandler(4) {
        @Override
        public int getSlotLimit(int slot) {
            return switch (slot) {
                case 0 -> 8;
                case 1, 2, 3 -> 1;
                default -> 1;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0) invalidatePickupRangeCache();
            CollectorBlockEntity.this.setChanged();
            if (!CollectorBlockEntity.this.level.isClientSide()) {
                try {
                    CollectorBlockEntity.this.level.sendBlockUpdated(
                            CollectorBlockEntity.this.getBlockPos(),
                            CollectorBlockEntity.this.getBlockState(),
                            CollectorBlockEntity.this.getBlockState(),
                            3
                    );
                } catch (Exception e) {
                    LOGGER.error("Failed to send block update for collector at {}", CollectorBlockEntity.this.getBlockPos(), e);
                }
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return true;
            try {
                stack.save(CollectorBlockEntity.this.level.registryAccess());
                return super.isItemValid(slot, stack);
            } catch (Exception e) {
                LOGGER.warn("Rejected invalid item {} for collector slot {}", stack, slot, e);
                return false;
            }
        }
    };

    public final ItemStackHandler outputInventory = new ItemStackHandler(45) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return ItemStack.EMPTY;

            int limit = getStackLimit(slot, stack);
            ItemStack existing = getStackInSlot(slot);

            if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(stack, existing)) return stack;

            int canInsert = limit - existing.getCount();
            if (canInsert <= 0) return stack;

            int toInsert = Math.min(stack.getCount(), canInsert);
            if (!simulate) {
                if (existing.isEmpty()) {
                    setStackInSlot(slot, stack.copyWithCount(toInsert));
                } else {
                    existing.grow(toInsert);
                }
            }
            return toInsert == stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - toInsert);
        }

        @Override
        protected void onContentsChanged(int slot) {
            CollectorBlockEntity.this.setChanged();
            if (!CollectorBlockEntity.this.level.isClientSide()) {
                CollectorBlockEntity.this.level.sendBlockUpdated(
                        CollectorBlockEntity.this.getBlockPos(),
                        CollectorBlockEntity.this.getBlockState(),
                        CollectorBlockEntity.this.getBlockState(),
                        3
                );
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            try {
                stack.save(CollectorBlockEntity.this.level.registryAccess());
                return true;
            } catch (Exception e) {
                LOGGER.warn("Rejected invalid item {} for collector output slot {}", stack, slot, e);
                return false;
            }
        }
    };

    private int storedXP = 0;
    private boolean xpCollectionEnabled = false;

    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;

    public CollectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.COLLECTOR_BE.get(), pos, blockState);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(ItemHandler.BLOCK, MFUBlockEntities.COLLECTOR_BE.get(),
                (blockEntity, direction) -> blockEntity instanceof CollectorBlockEntity collector
                        ? collector.getItemHandler(direction)
                        : null
        );
    }

    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction direction) {
        return outputInventory;
    }

    public void loadFromBlockItem(ItemStack stack) {
        CustomData customData = (CustomData) stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return;

        CompoundTag tag = customData.copyTag();
        if (level != null) {
            Provider registries = level.registryAccess();
            storedXP = tag.getInt("storedXP");
            xpCollectionEnabled = tag.getBoolean("xpCollectionEnabled");
            downUpOffset = tag.getInt("downUpOffset");
            northSouthOffset = tag.getInt("northSouthOffset");
            eastWestOffset = tag.getInt("eastWestOffset");

            if (!level.isClientSide()) {
                CollectorBlock.updateXpCollectionState(level, worldPosition, xpCollectionEnabled);
            }

            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    public int getStoredXP() { return storedXP; }

    public void setStoredXP(int xp) {
        int oldXP = storedXP;
        storedXP = Math.max(0, xp);
        if (oldXP != storedXP) setChangedAndUpdate();
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

    public int getDownUpOffset() { return downUpOffset; }
    public void setDownUpOffset(int offset) { setOffset(() -> downUpOffset, offset, val -> downUpOffset = val); }

    public int getNorthSouthOffset() { return northSouthOffset; }
    public void setNorthSouthOffset(int offset) { setOffset(() -> northSouthOffset, offset, val -> northSouthOffset = val); }

    public int getEastWestOffset() { return eastWestOffset; }
    public void setEastWestOffset(int offset) { setOffset(() -> eastWestOffset, offset, val -> eastWestOffset = val); }

    private void setOffset(java.util.concurrent.Callable<Integer> getter, int offset, java.util.function.IntConsumer setter) {
        int newOffset = Math.max(-10, Math.min(10, offset));
        try {
            if (getter.call() != newOffset) {
                setter.accept(newOffset);
                invalidateCollectionAreaCache();
                setChanged();
            }
        } catch (Exception ignored) {}
    }

    public int getPickupRange() {
        if (pickupRangeDirty || cachedPickupRange == -1) {
            cachedPickupRange = 3 + moduleSlots.getStackInSlot(0).getCount();
            pickupRangeDirty = false;
        }
        return cachedPickupRange;
    }

    private void invalidateCollectionAreaCache() { collectionAreaDirty = true; }
    private void invalidatePickupRangeCache() {
        pickupRangeDirty = true;
        invalidateCollectionAreaCache();
    }
    public void invalidateAllCaches() {
        invalidatePickupRangeCache();
        invalidateCollectionAreaCache();
    }

    public void drops() {
        SimpleContainer outputInv = new SimpleContainer(outputInventory.getSlots());
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            outputInv.setItem(i, outputInventory.getStackInSlot(i));
            outputInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        Containers.dropContents(level, worldPosition, outputInv);

        SimpleContainer moduleInv = new SimpleContainer(moduleSlots.getSlots());
        for (int i = 0; i < moduleSlots.getSlots(); i++) {
            moduleInv.setItem(i, moduleSlots.getStackInSlot(i));
            moduleSlots.setStackInSlot(i, ItemStack.EMPTY);
        }
        Containers.dropContents(level, worldPosition, moduleInv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        try { tag.put("moduleSlots", moduleSlots.serializeNBT(registries)); }
        catch (Exception e) { LOGGER.error("Failed to serialize module slots for collector at {}", getBlockPos(), e); }

        try { tag.put("outputInventory", outputInventory.serializeNBT(registries)); }
        catch (Exception e) { LOGGER.error("Failed to serialize output inventory for collector at {}", getBlockPos(), e); }

        tag.putInt("storedXP", storedXP);
        tag.putBoolean("xpCollectionEnabled", xpCollectionEnabled);
        tag.putInt("downUpOffset", downUpOffset);
        tag.putInt("northSouthOffset", northSouthOffset);
        tag.putInt("eastWestOffset", eastWestOffset);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            if (tag.contains("moduleSlots")) {
                CompoundTag moduleTag = tag.getCompound("moduleSlots");
                int oldSize = moduleTag.getInt("Size");
                if (oldSize == moduleSlots.getSlots()) moduleSlots.deserializeNBT(registries, moduleTag);
                else migrateModuleSlots(moduleTag, registries, oldSize);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize module slots for collector at {}, resetting to empty", getBlockPos(), e);
            for (int i = 0; i < moduleSlots.getSlots(); i++) moduleSlots.setStackInSlot(i, ItemStack.EMPTY);
        }

        try {
            if (tag.contains("outputInventory")) {
                CompoundTag outputTag = tag.getCompound("outputInventory");
                int oldSize = outputTag.getInt("Size");
                if (oldSize == outputInventory.getSlots()) outputInventory.deserializeNBT(registries, outputTag);
                else migrateOutputInventory(outputTag, registries, oldSize);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize output inventory for collector at {}, resetting to empty", getBlockPos(), e);
            for (int i = 0; i < outputInventory.getSlots(); i++) outputInventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        storedXP = tag.getInt("storedXP");
        xpCollectionEnabled = tag.getBoolean("xpCollectionEnabled");
        downUpOffset = tag.getInt("downUpOffset");
        northSouthOffset = tag.getInt("northSouthOffset");
        eastWestOffset = tag.getInt("eastWestOffset");

        invalidateAllCaches();
    }

    private void migrateModuleSlots(CompoundTag oldTag, Provider registries, int oldSize) {
        for (int i = 0; i < moduleSlots.getSlots(); i++) moduleSlots.setStackInSlot(i, ItemStack.EMPTY);
        int maxSlots = Math.min(oldSize, moduleSlots.getSlots());
        for (int i = 0; i < maxSlots; i++) {
            String key = String.valueOf(i);
            if (oldTag.contains(key)) {
                ItemStack stack = ItemStack.parseOptional(registries, oldTag.getCompound(key));
                if (moduleSlots.isItemValid(i, stack)) moduleSlots.setStackInSlot(i, stack);
                else if (level != null && !stack.isEmpty()) Block.popResource(level, worldPosition, stack);
            }
        }
    }

    private void migrateOutputInventory(CompoundTag oldTag, Provider registries, int oldSize) {
        for (int i = 0; i < outputInventory.getSlots(); i++) outputInventory.setStackInSlot(i, ItemStack.EMPTY);
        int maxSlots = Math.min(oldSize, outputInventory.getSlots());
        for (int i = 0; i < maxSlots; i++) {
            String key = String.valueOf(i);
            if (oldTag.contains(key)) outputInventory.setStackInSlot(i, ItemStack.parseOptional(registries, oldTag.getCompound(key)));
        }
        for (int i = outputInventory.getSlots(); i < oldSize; i++) {
            String key = String.valueOf(i);
            if (oldTag.contains(key) && level != null) {
                ItemStack stack = ItemStack.parseOptional(registries, oldTag.getCompound(key));
                if (!stack.isEmpty()) Block.popResource(level, worldPosition, stack);
            }
        }
    }

    @Override
    public Component getDisplayName() { return Component.translatable("menu.mobflowutilities.collector"); }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CollectorMenu(i, inventory, this);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        try { return saveWithoutMetadata(registries); }
        catch (Exception e) {
            LOGGER.error("Failed to create update tag for collector at {}", getBlockPos(), e);
            CompoundTag tag = new CompoundTag();
            tag.putString("id", MFUBlockEntities.COLLECTOR_BE.get().toString());
            return tag;
        }
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;
        tickCounter++;
        if (tickCounter >= COLLECTION_INTERVAL) {
            tickCounter = 0;
            collectItems();
            if (xpCollectionEnabled) collectXP();
        }
    }

    private void collectItems() {
        AABB area = getCollectionArea();
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        for (ItemEntity item : items) {
            if (!item.isAlive() || item.hasPickUpDelay()) continue;

            ItemStack stack = item.getItem().copy();
            if (shouldVoidItem(stack)) {
                int amount = stack.getCount();
                ItemStack current = item.getItem();
                if (amount >= current.getCount()) item.discard();
                else item.setItem(current.copyWithCount(current.getCount() - amount));
            } else {
                ItemStack remaining = insertIntoInventory(stack);
                int inserted = stack.getCount() - remaining.getCount();
                if (inserted > 0) {
                    ItemStack current = item.getItem();
                    if (inserted >= current.getCount()) item.discard();
                    else item.setItem(current.copyWithCount(current.getCount() - inserted));
                }
            }
        }
    }

    private boolean shouldVoidItem(ItemStack stack) {
        for (int i = 1; i <= 3; i++) {
            ItemStack module = moduleSlots.getStackInSlot(i);
            if (module.getItem() instanceof VoidFilterItem) {
                VoidFilterData data = module.getOrDefault(MFUDataComponents.VOID_FILTER_DATA.get(), VoidFilterData.DEFAULT);
                if (data.shouldVoidItem(stack)) return true;
            }
        }
        return false;
    }

    private void collectXP() {
        AABB area = getCollectionArea();
        List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, area);
        int collected = 0;
        for (ExperienceOrb orb : orbs) {
            if (!orb.isAlive()) continue;
            collected += orb.getValue();
            orb.discard();
        }
        if (collected > 0) {
            storedXP += collected;
            setChangedAndUpdate();
        }
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

    private ItemStack insertIntoInventory(ItemStack stack) {
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            stack = outputInventory.insertItem(i, stack, false);
            if (stack.isEmpty()) break;
        }
        return stack;
    }

    private void setChangedAndUpdate() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof CollectorBlockEntity collector) collector.tick();
        };
    }
}