package com.misterd.mobflowutilities.block.custom;

import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.entity.custom.GenesisChamberBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GenesisChamberBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0,  16, 1,  16),
            Block.box(1, 1, 1,  15, 4,  15),
            Block.box(0, 4, 0,  16, 5,  16),
            Block.box(3, 5, 3,  13, 6,   13),
            Block.box(4, 6, 4,  12, 6.5, 12),
            Block.box(0,  5, 15, 1,  15, 16),
            Block.box(0,  5, 0,  1,  15, 1),
            Block.box(15, 5, 0,  16, 15, 1),
            Block.box(15, 5, 15, 16, 15, 16),
            Block.box(0,  15, 0,  16, 16, 1),
            Block.box(0,  15, 15, 16, 16, 16),
            Block.box(0,  15, 1,  1,  16, 15),
            Block.box(15, 15, 1,  16, 16, 15),
            Block.box(1,  5, 0.5,  15, 15, 1),
            Block.box(1,  5, 15,   15, 15, 15.5),
            Block.box(0.5, 5, 1,   1,  15, 15),
            Block.box(15, 5, 1,    15.5, 15, 15),
            Block.box(1,  15, 1,   15, 15.5, 15)
    );
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<GenesisChamberBlock> CODEC = simpleCodec(GenesisChamberBlock::new);

    protected GenesisChamberBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GenesisChamberBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide()) {
            return blockEntityType == MFUBlockEntities.GENESIS_CHAMBER_BE.get()
                    ? (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof GenesisChamberBlockEntity genesisChamber) {
                    genesisChamber.tick();
                }
            }
                    : null;
        }
        return null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof GenesisChamberBlockEntity genesisChamberBlockEntity) {
                genesisChamberBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GenesisChamberBlockEntity genesisChamberBlockEntity && !level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(genesisChamberBlockEntity, Component.translatable("gui.mobflowutilities.genesis_chamber")), pos);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.SUCCESS;
    }
}