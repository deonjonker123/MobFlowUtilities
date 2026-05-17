package com.misterd.mobflowutilities.item.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GloomSporeItem extends Item {

    public GloomSporeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }

        int area = Config.getGloomSporeConversionArea();
        int half = area / 2;
        int blocksConverted = 0;

        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                BlockPos targetPos = clickedPos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);

                if (isConvertibleBlock(targetState)) {
                    level.setBlock(targetPos, MFUBlocks.DARK_DIRT.get().defaultBlockState(), 3);
                    level.levelEvent(2001, targetPos, Block.getId(targetState));
                    blocksConverted++;
                }
            }
        }

        if (blocksConverted > 0) {
            stack.shrink(1);
            level.playSound(null, clickedPos, SoundEvents.SOUL_SAND_PLACE, SoundSource.BLOCKS, 2.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private boolean isConvertibleBlock(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.DIRT
                || block == Blocks.GRASS_BLOCK
                || block == Blocks.PODZOL
                || block == Blocks.MYCELIUM
                || block == Blocks.ROOTED_DIRT
                || block == Blocks.COARSE_DIRT;
    }
}