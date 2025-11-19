package com.misterd.mobflowutilities.block.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class MFUFlammableRotatedPillarBlock extends RotatedPillarBlock {
    public MFUFlammableRotatedPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (context.getItemInHand().getItem() instanceof AxeItem) {
            if (state.is (MFUBlocks.GLOOMWOOD_LOG)) {
                return MFUBlocks.STRIPPED_GLOOMWOOD_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }

            if (state.is (MFUBlocks.GLOOMWOOD)) {
                return MFUBlocks.STRIPPED_GLOOMWOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }

            if (state.is (MFUBlocks.GLIMMERWOOD_LOG)) {
                return MFUBlocks.STRIPPED_GLIMMERWOOD_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }

            if (state.is (MFUBlocks.GLIMMERWOOD)) {
                return MFUBlocks.STRIPPED_GLIMMERWOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }

        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
