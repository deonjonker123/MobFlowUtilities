package com.misterd.mobflowutilities.item.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GlimmerSproutItem extends Item {

    public GlimmerSproutItem(Properties properties) {
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

        if (!isConvertibleBlock(level.getBlockState(clickedPos))) {
            player.displayClientMessage(
                    Component.translatable("item.mobflowutilities.glimmer_sprout.invalid_block"),
                    true
            );
            return InteractionResult.FAIL;
        }

        int lightLevel = level.getBrightness(LightLayer.BLOCK, clickedPos.above());
        int requiredLight = Config.getGlimmerGrassConversionLightLevel();
        if (lightLevel < requiredLight) {
            player.displayClientMessage(
                    Component.translatable("item.mobflowutilities.glimmer_sprout.too_dark", lightLevel, requiredLight),
                    true
            );
            return InteractionResult.FAIL;
        }

        int conversionArea = Config.getGlimmerSproutConversionArea();
        int halfArea = conversionArea / 2;
        int blocksConverted = 0;

        for (int dx = -halfArea; dx <= halfArea; dx++) {
            for (int dz = -halfArea; dz <= halfArea; dz++) {
                BlockPos targetPos = clickedPos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);

                if (isConvertibleBlock(targetState)
                        && level.getBrightness(LightLayer.BLOCK, targetPos.above()) >= requiredLight) {
                    level.setBlock(targetPos, MFUBlocks.GLIMMER_GRASS.get().defaultBlockState(), 3);
                    blocksConverted++;
                }
            }
        }

        if (blocksConverted == 0) {
            player.displayClientMessage(
                    Component.translatable("item.mobflowutilities.glimmer_sprout.no_conversion"),
                    true
            );
            return InteractionResult.FAIL;
        }

        stack.shrink(1);

        return InteractionResult.SUCCESS;
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