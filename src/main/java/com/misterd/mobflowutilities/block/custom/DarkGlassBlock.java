package com.misterd.mobflowutilities.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class DarkGlassBlock extends Block {
    public static final BooleanProperty CONNECTED_DOWN = BooleanProperty.create("connected_down");
    public static final BooleanProperty CONNECTED_UP = BooleanProperty.create("connected_up");
    public static final BooleanProperty CONNECTED_NORTH = BooleanProperty.create("connected_north");
    public static final BooleanProperty CONNECTED_SOUTH = BooleanProperty.create("connected_south");
    public static final BooleanProperty CONNECTED_WEST = BooleanProperty.create("connected_west");
    public static final BooleanProperty CONNECTED_EAST = BooleanProperty.create("connected_east");

    public DarkGlassBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(CONNECTED_DOWN, Boolean.FALSE).setValue(CONNECTED_EAST, Boolean.FALSE).setValue(CONNECTED_NORTH, Boolean.FALSE).setValue(CONNECTED_SOUTH, Boolean.FALSE).setValue(CONNECTED_UP, Boolean.FALSE).setValue(CONNECTED_WEST, Boolean.FALSE));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(@Nonnull BlockState state, BlockState adjacentState, @Nonnull Direction direction) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected int getLightBlock(@Nonnull BlockState state, BlockGetter level, BlockPos pos) {
        return level.getMaxLightLevel();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return super.getStateForPlacement(context)
                .setValue(CONNECTED_DOWN, this.canConnect(level, pos, Direction.DOWN))
                .setValue(CONNECTED_EAST, this.canConnect(level, pos, Direction.EAST))
                .setValue(CONNECTED_NORTH, this.canConnect(level, pos, Direction.NORTH))
                .setValue(CONNECTED_SOUTH, this.canConnect(level, pos, Direction.SOUTH))
                .setValue(CONNECTED_UP, this.canConnect(level, pos, Direction.UP))
                .setValue(CONNECTED_WEST, this.canConnect(level, pos, Direction.WEST));
    }

    @Nonnull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(CONNECTED_DOWN, this.canConnect(level, pos, Direction.DOWN))
                .setValue(CONNECTED_EAST, this.canConnect(level, pos, Direction.EAST))
                .setValue(CONNECTED_NORTH, this.canConnect(level, pos, Direction.NORTH))
                .setValue(CONNECTED_SOUTH, this.canConnect(level, pos, Direction.SOUTH))
                .setValue(CONNECTED_UP, this.canConnect(level, pos, Direction.UP))
                .setValue(CONNECTED_WEST, this.canConnect(level, pos, Direction.WEST));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST);
    }

    private boolean canConnect(BlockGetter level, BlockPos pos, Direction direction) {
        final BlockState stateConnection = level.getBlockState(pos.relative(direction));
        return stateConnection != null && stateConnection.getBlock() == this;
    }
}
