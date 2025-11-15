package com.misterd.mobflowutilities.entity.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import java.util.List;

import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlowPadBlockEntity extends BlockEntity {

    private static final int MOVEMENT_INTERVAL = 2;
    private int tickCounter = 0;

    private static final double FAST_SPEED = 0.15D;
    private static final double FASTER_SPEED = 0.3D;
    private static final double FASTEST_SPEED = 0.6D;

    public FlowPadBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.FLOW_PAD_BE.get(), pos, blockState);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof FlowPadBlockEntity flowPad) {
                flowPad.tick();
            }
        };
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        tickCounter++;
        if (tickCounter >= MOVEMENT_INTERVAL) {
            tickCounter = 0;
            applyMovement();
        }
    }

    private void applyMovement() {
        if (level == null) return;

        AABB movementArea = new AABB(
                worldPosition.getX() - 0.1D, worldPosition.getY() + 0.0625D, worldPosition.getZ() - 0.1D,
                worldPosition.getX() + 1.1D, worldPosition.getY() + 1.5D, worldPosition.getZ() + 1.1D
        );

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, movementArea, this::shouldMoveEntity);
        if (entities.isEmpty()) return;

        Direction facing = getFacing();
        double speed = getSpeedMultiplier();
        Vec3 movement = getMovementVector(facing, speed);

        for (Entity entity : entities) {
            applyMovementToEntity(entity, movement);
        }
    }

    private boolean shouldMoveEntity(Entity entity) {
        if (entity instanceof Player player && player.isShiftKeyDown()) return false;

        if (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity) {
            double entityBottom = entity.getBoundingBox().minY;
            double padTop = worldPosition.getY() + 0.0625D;
            return Math.abs(entityBottom - padTop) < 0.1D;
        }
        return false;
    }

    private Direction getFacing() {
        BlockState state = getBlockState();
        if (state.hasProperty((Property) HorizontalDirectionalBlock.FACING)) {
            return state.getValue(HorizontalDirectionalBlock.FACING);
        }
        return Direction.NORTH;
    }

    private double getSpeedMultiplier() {
        Block block = getBlockState().getBlock();

        if (block == MFUBlocks.FAST_FLOW_PAD.get()) return FAST_SPEED;
        if (block == MFUBlocks.FASTER_FLOW_PAD.get()) return FASTER_SPEED;
        if (block == MFUBlocks.FASTEST_FLOW_PAD.get()) return FASTEST_SPEED;
        return FAST_SPEED;
    }

    private Vec3 getMovementVector(Direction facing, double speed) {
        return switch (facing) {
            case NORTH -> new Vec3(0, 0, speed);
            case SOUTH -> new Vec3(0, 0, -speed);
            case EAST -> new Vec3(-speed, 0, 0);
            case WEST -> new Vec3(speed, 0, 0);
            default -> Vec3.ZERO;
        };
    }

    private void applyMovementToEntity(Entity entity, Vec3 movement) {
        Vec3 currentVelocity = entity.getDeltaMovement();
        Vec3 newVelocity = currentVelocity.add(movement);

        double maxVelocity = 2.0D;
        newVelocity = new Vec3(
                Math.max(-maxVelocity, Math.min(maxVelocity, newVelocity.x)),
                newVelocity.y,
                Math.max(-maxVelocity, Math.min(maxVelocity, newVelocity.z))
        );

        entity.setDeltaMovement(newVelocity);
        entity.hurtMarked = true;
    }
}
