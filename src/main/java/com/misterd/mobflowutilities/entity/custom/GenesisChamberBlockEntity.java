package com.misterd.mobflowutilities.entity.custom;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.neoforged.neoforge.transfer.ResourceHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GenesisChamberBlockEntity extends BlockEntity implements MenuProvider {

    private static final int SLOT_EGG = 0;
    private static final int SLOT_FUEL = 1;
    private static final int SLOT_MODULE_1 = 2;
    private static final int SLOT_MODULE_2 = 3;
    private static final int SLOT_COUNT = 4;

    public Entity cachedEntity = null;

    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(SLOT_COUNT) {
        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return switch (index) {
                case SLOT_EGG -> 1;
                default -> resource.toStack().getMaxStackSize();
            };
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (resource.isEmpty()) return false;
            ItemStack stack = resource.toStack();
            return switch (index) {
                case SLOT_EGG -> stack.getItem() instanceof SpawnEggItem;
                case SLOT_FUEL -> stack.is(MFUTags.Items.GENESIS_CHAMBER_FUELS);
                case SLOT_MODULE_1, SLOT_MODULE_2 ->
                        stack.getItem() == MFUItems.SPEED_MODULE.get() || stack.getItem() == MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get();
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            if (level != null && !level.isClientSide())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;

    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int spawnTimer = 0;

    private boolean requiresRedstone = false;

    public GenesisChamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.GENESIS_CHAMBER_BE.get(), pos, blockState);
    }

    public ItemStack getStack(int slot) {
        ItemResource res = inventory.getResource(slot);
        if (res.isEmpty()) return ItemStack.EMPTY;
        return res.toStack(inventory.getAmountAsInt(slot));
    }

    public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction direction) {
        return new ResourceHandler<>() {
            @Override public int size() { return 1; }

            @Override
            public ItemResource getResource(int index) {
                return inventory.getResource(SLOT_FUEL);
            }

            @Override
            public long getAmountAsLong(int index) {
                return inventory.getAmountAsLong(SLOT_FUEL);
            }

            @Override
            public long getCapacityAsLong(int index, ItemResource resource) {
                return inventory.getCapacityAsLong(SLOT_FUEL, resource);
            }

            @Override
            public boolean isValid(int index, ItemResource resource) {
                return !resource.isEmpty() && resource.toStack().is(MFUTags.Items.GENESIS_CHAMBER_FUELS);
            }

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext tx) {
                if (!isValid(index, resource)) return 0;
                return inventory.insert(SLOT_FUEL, resource, amount, tx);
            }

            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext tx) {
                return 0;
            }
        };
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, MFUBlockEntities.GENESIS_CHAMBER_BE.get(),
                (be, dir) -> be instanceof GenesisChamberBlockEntity gc ? gc.getItemHandler(dir) : null);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.mobflowutilities.genesis_chamber");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new GenesisChamberMenu(id, inv, this);
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
        downUpOffset = Math.max(-10, Math.min(10, v)); setChanged();
    }

    public void setNorthSouthOffset(int v) {
        northSouthOffset = Math.max(-10, Math.min(10, v)); setChanged();
    }

    public void setEastWestOffset(int v) {
        eastWestOffset = Math.max(-10, Math.min(10, v)); setChanged();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getMaxBurnTime() {
        return maxBurnTime;
    }

    public boolean getRequiresRedstone() {
        return requiresRedstone;
    }

    public void setRequiresRedstone(boolean value) {
        requiresRedstone = value;
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public Entity getOrCreateRenderedEntity(EntityType<?> type) {
        if (cachedEntity != null && cachedEntity.getType() == type) return cachedEntity;
        Level lvl = getLevel();
        if (lvl == null) return null;
        Entity entity = type.create(lvl, EntitySpawnReason.NATURAL);
        if (entity == null) return null;
        entity.noPhysics = true;
        if (entity instanceof Mob mob) mob.setNoAi(true);
        entity.invulnerableTime = Integer.MAX_VALUE;
        cachedEntity = entity;
        return entity;
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
        boolean zoneIsFull = isSpawnZoneFull();

        if (requiresRedstone && !level.hasNeighborSignal(worldPosition)) {
            if (changed) {
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return;
        }

        if (burnTime > 0 && !zoneIsFull) { burnTime--; changed = true; }

        if (burnTime <= 0 && canOperate() && !zoneIsFull) {
            ItemStack fuel = getStack(SLOT_FUEL);
            if (!fuel.isEmpty()) {
                int fuelValue = getBurnDuration(fuel);
                if (fuelValue > 0) {
                    burnTime = fuelValue;
                    maxBurnTime = fuelValue;
                    try (Transaction tx = Transaction.openRoot()) {
                        inventory.extract(SLOT_FUEL, ItemResource.of(fuel), 1, tx);
                        tx.commit();
                    }
                    changed = true;
                }
            }
        }

        if (burnTime > 0 && canOperate() && !zoneIsFull) {
            spawnTimer++;
            if (spawnTimer >= getSpawnInterval()) {
                if (attemptSpawn()) { spawnTimer = 0; changed = true; }
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
        return getStack(SLOT_EGG).getItem() instanceof SpawnEggItem;
    }

    private int getSpawnInterval() {
        int modules = Math.min(10, inventory.getAmountAsInt(SLOT_MODULE_1));
        return Math.max(5, 200 - (modules * 20));
    }

    private int getSpawnRadius() {
        int modules = Math.min(5, inventory.getAmountAsInt(SLOT_MODULE_2));
        return 2 + modules;
    }

    private boolean attemptSpawn() {
        ServerLevel serverLevel = (ServerLevel) level;
        ItemStack egg = getStack(SLOT_EGG);
        if (!(egg.getItem() instanceof SpawnEggItem spawnEgg)) return false;
        EntityType<?> type = spawnEgg.getType(egg);
        if (type == null) return false;

        List<BlockPos> valid = getValidSpawnPositions();
        if (valid.isEmpty()) return false;

        BlockPos spawnPos = valid.get(serverLevel.getRandom().nextInt(valid.size()));
        if (!isValidLightLevel(spawnPos, type)) return false;

        Entity entity = type.create(serverLevel, EntitySpawnReason.NATURAL);
        if (entity == null) return false;

        entity.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, serverLevel.getRandom().nextFloat() * 360.0F, 0.0F);

        if (entity instanceof Mob mob)
            mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.SPAWNER, null);

        return serverLevel.addFreshEntity(entity);
    }

    private List<BlockPos> getValidSpawnPositions() {
        List<BlockPos> positions = new ArrayList<>();
        int radius = getSpawnRadius();
        BlockPos origin = getBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) continue;
                BlockPos pos = origin.offset(x + eastWestOffset, downUpOffset, z + northSouthOffset);
                if (isValidSpawnPosition(pos)) positions.add(pos);
            }
        }
        return positions;
    }

    private boolean isValidSpawnPosition(BlockPos pos) {
        BlockState ground = level.getBlockState(pos);
        BlockState above  = level.getBlockState(pos.above());
        return (ground.isAir() || ground.canBeReplaced()) &&
                (above.isAir()  || above.canBeReplaced());
    }

    private boolean isValidLightLevel(BlockPos pos, EntityType<?> type) {
        int light = level.getMaxLocalRawBrightness(pos);
        Entity test = type.create(level, EntitySpawnReason.NATURAL);
        if (test instanceof Mob mob) {
            boolean friendly = mob.getType().getCategory().isFriendly();
            test.discard();
            return friendly ? light >= 9 : light <= 7;
        }
        if (test != null) test.discard();
        return true;
    }

    private int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(MFUTags.Items.GENESIS_CHAMBER_FUELS)) return 0;
        return stack.getBurnTime(null, ((ServerLevel) level).fuelValues());
    }

    private boolean isSpawnZoneFull() {
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, getSpawnZoneAABB());
        return mobs.size() >= Config.getGenesisChamberSpawnCap();
    }

    private AABB getSpawnZoneAABB() {
        BlockPos pos = getBlockPos();
        int r = getSpawnRadius();
        return new AABB(
                pos.getX() - r + eastWestOffset, pos.getY() + downUpOffset, pos.getZ() - r + northSouthOffset,
                pos.getX() + r + 1 + eastWestOffset, pos.getY() + 2 + downUpOffset, pos.getZ() + r + 1 + northSouthOffset
        );
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output);
        output.putInt("downUpOffset", downUpOffset);
        output.putInt("northSouthOffset", northSouthOffset);
        output.putInt("eastWestOffset", eastWestOffset);
        output.putInt("burnTime", burnTime);
        output.putInt("maxBurnTime", maxBurnTime);
        output.putInt("spawnTimer", spawnTimer);
        output.putBoolean("requiresRedstone", requiresRedstone);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input);
        downUpOffset = input.getIntOr("downUpOffset", 0);
        northSouthOffset = input.getIntOr("northSouthOffset", 0);
        eastWestOffset = input.getIntOr("eastWestOffset", 0);
        burnTime = input.getIntOr("burnTime", 0);
        maxBurnTime = input.getIntOr("maxBurnTime", 0);
        spawnTimer = input.getIntOr("spawnTimer", 0);
        requiresRedstone = input.getBooleanOr("requiresRedstone", false);
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