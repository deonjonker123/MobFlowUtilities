package com.misterd.mobflowutilities.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;

public class GlimmerLampBlock extends Block {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<GlimmerLampBlock> CODEC = simpleCodec(GlimmerLampBlock::new);

    public GlimmerLampBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            boolean isPowered = level.hasNeighborSignal(pos);
            if (isPowered != state.getValue(LIT)) {
                level.setBlock(pos, state.setValue(LIT, isPowered), 3);
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("block.mobflowutilities.glimmer_lamp.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}