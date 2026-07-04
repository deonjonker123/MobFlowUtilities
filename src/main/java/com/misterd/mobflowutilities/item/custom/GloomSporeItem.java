package com.misterd.mobflowutilities.item.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class GloomSporeItem extends Item {

    private static final List<TagKey<Block>> CONVERTIBLE_TAGS = List.of(
            BlockTags.DIRT,
            BlockTags.GRASS_BLOCKS
    );

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
            level.playSound(null, clickedPos, SoundEvents.SOUL_SAND_PLACE, SoundSource.BLOCKS, 2.0F, 1.0F);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private boolean isConvertibleBlock(BlockState state) {
        for (TagKey<Block> tag : CONVERTIBLE_TAGS) {
            if (state.is(tag)) return true;
        }
        return false;
    }
}