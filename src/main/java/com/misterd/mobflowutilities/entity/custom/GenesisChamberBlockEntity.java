package com.misterd.mobflowutilities.entity.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.config.Config;
import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.gui.custom.GenesisChamberMenu;
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
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GenesisChamberBlockEntity extends BlockEntity implements MenuProvider {
    public Entity cachedEntity = null;

    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;

    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int spawnTimer = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0) {
                return 1;
            }
            return super.getSlotLimit(slot);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public IItemHandler getItemHandler(@Nullable Direction direction) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(1);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (!stack.is(MFUTags.Items.GENESIS_CHAMBER_FUELS)) {
                    return stack;
                }
                return inventory.insertItem(1, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(1);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.is(MFUTags.Items.GENESIS_CHAMBER_FUELS);
            }
        };
    }

    public GenesisChamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.GENESIS_CHAMBER_BE.get(), pos, blockState);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("downUpOffset", downUpOffset);
        tag.putInt("northSouthOffset", northSouthOffset);
        tag.putInt("eastWestOffset", eastWestOffset);
        tag.putInt("burnTime", burnTime);
        tag.putInt("maxBurnTime", maxBurnTime);
        tag.putInt("spawnTimer", spawnTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        downUpOffset = tag.getInt("downUpOffset");
        northSouthOffset = tag.getInt("northSouthOffset");
        eastWestOffset = tag.getInt("eastWestOffset");
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("maxBurnTime");
        spawnTimer = tag.getInt("spawnTimer");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.mobflowutilities.genesis_chamber");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new GenesisChamberMenu(i, inventory, this);
    }

    public Entity getOrCreateRenderedEntity(EntityType<?> type) {
        if (cachedEntity != null && cachedEntity.getType() == type) {
            return cachedEntity;
        }

        Level level = getLevel();
        if (level == null) return null;

        Entity entity = type.create(level);
        if (entity == null) return null;

        entity.noPhysics = true;
        if (entity instanceof Mob mobEntity) {
            mobEntity.setNoAi(true);
        }

        entity.invulnerableTime = Integer.MAX_VALUE;

        this.cachedEntity = entity;
        return entity;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public int getDownUpOffset() {
        return downUpOffset;
    }

    public void setDownUpOffset(int offset) {
        this.downUpOffset = Math.max(-10, Math.min(10, offset));
        setChanged();
    }

    public int getNorthSouthOffset() {
        return northSouthOffset;
    }

    public void setNorthSouthOffset(int offset) {
        this.northSouthOffset = Math.max(-10, Math.min(10, offset));
        setChanged();
    }

    public int getEastWestOffset() {
        return eastWestOffset;
    }

    public void setEastWestOffset(int offset) {
        this.eastWestOffset = Math.max(-10, Math.min(10, offset));
        setChanged();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getMaxBurnTime() {
        return maxBurnTime;
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        boolean wasLit = burnTime > 0;
        boolean changed = false;

        boolean zoneIsFull = isSpawnZoneFull();

        if (burnTime > 0 && !zoneIsFull) {
            burnTime--;
            changed = true;
        }

        if (burnTime <= 0 && canOperate() && !zoneIsFull) {
            ItemStack fuelStack = inventory.getStackInSlot(1);
            if (!fuelStack.isEmpty()) {
                int fuelValue = getBurnDuration(fuelStack);
                if (fuelValue > 0) {
                    burnTime = fuelValue;
                    maxBurnTime = fuelValue;
                    fuelStack.shrink(1);
                    changed = true;
                }
            }
        }

        if (burnTime > 0 && canOperate() && !zoneIsFull) {
            spawnTimer++;

            int spawnInterval = getSpawnInterval();
            if (spawnTimer >= spawnInterval) {
                if (attemptSpawn()) {
                    spawnTimer = 0;
                    changed = true;
                }
            }
        } else {
            spawnTimer = 0;
        }

        if (changed) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private boolean canOperate() {
        ItemStack eggStack = inventory.getStackInSlot(0);
        return eggStack.getItem() instanceof SpawnEggItem;
    }

    private int getSpawnInterval() {
        ItemStack speedModules = inventory.getStackInSlot(2);
        int moduleCount = Math.min(5, speedModules.getCount());
        return Math.max(50, 200 - (moduleCount * 30));
    }

    private int getSpawnRadius() {
        ItemStack radiusModules = inventory.getStackInSlot(3);
        int moduleCount = Math.min(5, radiusModules.getCount());
        return 2 + moduleCount;
    }

    private boolean attemptSpawn() {
        ItemStack eggStack = inventory.getStackInSlot(0);
        if (!(eggStack.getItem() instanceof SpawnEggItem spawnEgg)) {
            return false;
        }

        EntityType<?> entityType = spawnEgg.getType(eggStack);
        if (entityType == null) return false;

        List<BlockPos> validPositions = getValidSpawnPositions();
        if (validPositions.isEmpty()) {
            return false;
        }

        BlockPos spawnPos = validPositions.get(level.random.nextInt(validPositions.size()));

        if (!isValidLightLevel(spawnPos, entityType)) {
            return false;
        }

        Entity entity = entityType.create(level);
        if (entity == null) return false;

        entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                level.random.nextFloat() * 360.0F, 0.0F);

        if (entity instanceof Mob mob) {
            mob.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(spawnPos),
                    MobSpawnType.SPAWNER, null);
        }

        return level.addFreshEntity(entity);
    }

    private List<BlockPos> getValidSpawnPositions() {
        List<BlockPos> positions = new ArrayList<>();
        int radius = getSpawnRadius();
        BlockPos chamberPos = getBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) continue;

                BlockPos checkPos = chamberPos.offset(
                        x + eastWestOffset,
                        downUpOffset,
                        z + northSouthOffset
                );

                if (isValidSpawnPosition(checkPos)) {
                    positions.add(checkPos);
                }
            }
        }

        return positions;
    }

    private boolean isValidSpawnPosition(BlockPos pos) {
        BlockState spawnState = level.getBlockState(pos);
        BlockState aboveState = level.getBlockState(pos.above());

        boolean hasSpace = (spawnState.isAir() || spawnState.canBeReplaced()) &&
                (aboveState.isAir() || aboveState.canBeReplaced());

        return hasSpace;
    }

    private boolean isFlowPad(BlockState state) {
        return state.is(MFUBlocks.FAST_FLOW_PAD.get()) ||
                state.is(MFUBlocks.FASTER_FLOW_PAD.get()) ||
                state.is(MFUBlocks.FASTEST_FLOW_PAD.get());
    }

    private boolean isValidLightLevel(BlockPos pos, EntityType<?> entityType) {
        int lightLevel = level.getMaxLocalRawBrightness(pos);

        Entity testEntity = entityType.create(level);
        if (testEntity instanceof Mob mob) {
            boolean isFriendly = mob.getType().getCategory().isFriendly();

            testEntity.discard();

            if (isFriendly) {
                return lightLevel >= 9;
            } else {
                return lightLevel <= 7;
            }
        }

        if (testEntity != null) {
            testEntity.discard();
        }
        return true;
    }

    private int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        if (!stack.is(MFUTags.Items.GENESIS_CHAMBER_FUELS)) return 0;

        return stack.getBurnTime(RecipeType.SMELTING);
    }

    private boolean isSpawnZoneFull() {
        AABB spawnZone = getSpawnZoneAABB();
        List<Mob> mobsInZone = level.getEntitiesOfClass(Mob.class, spawnZone);
        return mobsInZone.size() >= Config.getGenesisChamberSpawnCap();
    }

    private AABB getSpawnZoneAABB() {
        BlockPos pos = getBlockPos();
        int radius = getSpawnRadius();

        return new AABB(
                pos.getX() - radius + eastWestOffset,
                pos.getY() + downUpOffset,
                pos.getZ() - radius + northSouthOffset,
                pos.getX() + radius + 1 + eastWestOffset,
                pos.getY() + 2 + downUpOffset,
                pos.getZ() + radius + 1 + northSouthOffset
        );
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MFUBlockEntities.GENESIS_CHAMBER_BE.get(),
                (blockEntity, direction) -> blockEntity instanceof GenesisChamberBlockEntity genesisChamberBlockEntity
                        ? genesisChamberBlockEntity.getItemHandler(direction)
                        : null
        );
    }
}