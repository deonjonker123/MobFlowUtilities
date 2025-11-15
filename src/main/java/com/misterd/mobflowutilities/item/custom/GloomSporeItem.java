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

        // Check if clicked block is convertible
        if (!isConvertibleBlock(level.getBlockState(clickedPos))) {
            player.displayClientMessage(
                    Component.translatable("item.mobflowutilities.gloom_spore.invalid_block"),
                    true
            );
            return InteractionResult.FAIL;
        }

        // Check light level
        int lightLevel = level.getBrightness(LightLayer.BLOCK, clickedPos.above());
        int maxLight = Config.getDarkDirtConversionLightLevel();
        if (lightLevel > maxLight) {
            player.displayClientMessage(
                    Component.translatable("item.mobflowutilities.gloom_spore.too_bright", lightLevel, maxLight),
                    true
            );
            return InteractionResult.FAIL;
        }

        // Convert blocks in area
        int area = Config.getGloomSporeConversionArea();
        int half = area / 2;
        int blocksConverted = 0;

        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                BlockPos targetPos = clickedPos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);

                if (!isConvertibleBlock(targetState)) {
                    continue;
                }

                int targetLight = level.getBrightness(LightLayer.BLOCK, targetPos.above());
                if (targetLight <= maxLight) {
                    level.setBlock(targetPos, MFUBlocks.DARK_DIRT.get().defaultBlockState(), 3);
                    blocksConverted++;
                }
            }
        }

        // Check if any blocks were converted
        if (blocksConverted == 0) {
            player.displayClientMessage(
                    Component.translatable("item.mobflowutilities.gloom_spore.no_conversion"),
                    true
            );
            return InteractionResult.FAIL;
        }

        // Consume the item
        stack.shrink(1);
        player.displayClientMessage(
                Component.translatable("item.mobflowutilities.gloom_spore.success", blocksConverted),
                true
        );

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