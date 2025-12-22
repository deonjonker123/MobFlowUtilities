package com.misterd.mobflowutilities.entity.custom;

import com.misterd.mobflowutilities.block.custom.FanBlock;
import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.gui.custom.FanMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FanBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        public int getSlotLimit(int slot) {
            return switch (slot) {
                case 0, 1 -> 4;
                case 2 -> 10;
                default -> 1;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            invalidateCaches();
        }
    };

    private AABB cachedPushZone;
    private Integer cachedDistance;
    private Integer cachedWidth;
    private Integer cachedHeight;

    public FanBlockEntity(BlockPos pos, BlockState state) {
        super(MFUBlockEntities.FAN_BE.get(), pos, state);
    }

    public void tick() {
        if (this.level == null) return;

        if (!this.level.isClientSide && getBlockState().getValue(FanBlock.POWERED)) {
            pushEntities(this.level);
        }
    }

    public int getDistance() {
        if (cachedDistance != null) return cachedDistance;
        int modules = inventory.getStackInSlot(2).getCount();
        cachedDistance = Math.min(4 + modules, 15);
        return cachedDistance;
    }

    public int getWidth() {
        if (cachedWidth != null) return cachedWidth;
        cachedWidth = inventory.getStackInSlot(0).getCount();
        return cachedWidth;
    }

    public int getHeight() {
        if (cachedHeight != null) return cachedHeight;
        cachedHeight = inventory.getStackInSlot(1).getCount();
        return cachedHeight;
    }

    public AABB getPushZone() {
        if (cachedPushZone != null) return cachedPushZone;

        Direction facing = getBlockState().getValue(FanBlock.FACING);
        int d = getDistance();
        int w = getWidth();
        int h = getHeight();

        BlockPos o = worldPosition.relative(facing);

        cachedPushZone = switch (facing) {
            case NORTH -> new AABB(
                    o.getX() - w, o.getY() - h, o.getZ() - d,
                    o.getX() + w + 1, o.getY() + h + 1, o.getZ() + 1
            );
            case SOUTH -> new AABB(
                    o.getX() - w, o.getY() - h, o.getZ(),
                    o.getX() + w + 1, o.getY() + h + 1, o.getZ() + d + 1
            );
            case WEST -> new AABB(
                    o.getX() - d, o.getY() - h, o.getZ() - w,
                    o.getX() + 1, o.getY() + h + 1, o.getZ() + w + 1
            );
            case EAST -> new AABB(
                    o.getX(), o.getY() - h, o.getZ() - w,
                    o.getX() + d + 1, o.getY() + h + 1, o.getZ() + w + 1
            );
            case DOWN -> new AABB(
                    o.getX() - w, o.getY() - d, o.getZ() - h,
                    o.getX() + w + 1, o.getY() + 1, o.getZ() + h + 1
            );
            case UP -> new AABB(
                    o.getX() - w, o.getY(),
                    o.getZ() - h,
                    o.getX() + w + 1, o.getY() + d + 1, o.getZ() + h + 1
            );
        };

        return cachedPushZone;
    }

    private void pushEntities(Level level) {
        AABB zone = getPushZone();
        Direction facing = getBlockState().getValue(FanBlock.FACING);
        Vec3 push = Vec3.atLowerCornerOf(facing.getNormal()).scale(0.3);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, zone);

        for (LivingEntity entity : entities) {
            if (hasClearLane(level, entity)) {
                entity.push(push.x, push.y, push.z);
                entity.hurtMarked = true;
            }
        }
    }

    private boolean hasClearLane(Level level, LivingEntity entity) {
        Direction facing = getBlockState().getValue(FanBlock.FACING);

        BlockPos start = worldPosition.relative(facing);
        BlockPos end = entity.blockPosition();

        int steps = switch (facing) {
            case NORTH, SOUTH -> Math.abs(end.getZ() - start.getZ());
            case EAST, WEST   -> Math.abs(end.getX() - start.getX());
            case UP, DOWN     -> Math.abs(end.getY() - start.getY());
        };

        int dx = Integer.signum(end.getX() - start.getX());
        int dy = Integer.signum(end.getY() - start.getY());
        int dz = Integer.signum(end.getZ() - start.getZ());

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(start.getX(), start.getY(), start.getZ());


        for (int i = 0; i < steps; i++) {
            pos.move(dx, dy, dz);

            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && state.isSolidRender(level, pos)) {
                return false;
            }
        }

        return true;
    }

    private void invalidateCaches() {
        cachedPushZone = null;
        cachedDistance = null;
        cachedWidth = null;
        cachedHeight = null;
    }

    public void dropContents() {
        if (level != null) {
            SimpleContainer container = new SimpleContainer(inventory.getSlots());
            for (int i = 0; i < inventory.getSlots(); i++) {
                container.setItem(i, inventory.getStackInSlot(i));
            }
            Containers.dropContents(level, worldPosition, container);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        invalidateCaches();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.mobflowutilities.fan");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FanMenu(id, inv, this);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        return saveWithoutMetadata(registries);
    }
}