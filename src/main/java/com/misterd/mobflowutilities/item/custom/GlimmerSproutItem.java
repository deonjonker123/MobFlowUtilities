package com.misterd.mobflowutilities.item.custom;

import java.util.List;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
            player.displayClientMessage(Component.translatable("item.mobflowutilities.glimmer_sprout.invalid_block"), true);
            return InteractionResult.FAIL;
        }

        int lightLevel = level.getBrightness(LightLayer.BLOCK, clickedPos.above());
        int requiredLight = Config.getGlimmerGrassConversionLightLevel();
        if (lightLevel < requiredLight) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.glimmer_sprout.too_dark", lightLevel, requiredLight), true);
            return InteractionResult.FAIL;
        }

        int currentUses = stack.getOrDefault(DataComponents.DAMAGE, 0);
        int maxUses = Config.getGlimmerSproutUses();
        if (currentUses >= maxUses) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.glimmer_sprout.depleted"), true);
            return InteractionResult.FAIL;
        }

        int conversionArea = Config.getGlimmerSproutConversionArea();
        int halfArea = conversionArea / 2;
        int blocksConverted = 0;

        for (int dx = -halfArea; dx <= halfArea; dx++) {
            for (int dz = -halfArea; dz <= halfArea; dz++) {
                BlockPos targetPos = clickedPos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);
                if (isConvertibleBlock(targetState) && level.getBrightness(LightLayer.BLOCK, targetPos.above()) >= requiredLight) {
                    level.setBlock(targetPos, MFUBlocks.GLIMMER_GRASS.get().defaultBlockState(), 3);
                    blocksConverted++;
                }
            }
        }

        if (blocksConverted > 0) {
            stack.set(DataComponents.DAMAGE, currentUses + 1);

            if (currentUses + 1 >= maxUses) {
                stack.shrink(1);
                player.displayClientMessage(Component.translatable("item.mobflowutilities.glimmer_sprout.consumed", blocksConverted), true);
            } else {
                int usesRemaining = maxUses - (currentUses + 1);
                player.displayClientMessage(Component.translatable("item.mobflowutilities.glimmer_sprout.success", blocksConverted, usesRemaining), true);
            }

            return InteractionResult.SUCCESS;
        } else {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.glimmer_sprout.no_conversion"), true);
            return InteractionResult.FAIL;
        }
    }

    private boolean isConvertibleBlock(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.PODZOL
                || block == Blocks.MYCELIUM || block == Blocks.ROOTED_DIRT || block == Blocks.COARSE_DIRT;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.mobflowutilities.glimmer_sprout.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));

        int currentUses = stack.getOrDefault(DataComponents.DAMAGE, 0);
        int maxUses = Config.getGlimmerSproutUses();
        int usesRemaining = maxUses - currentUses;

        if (usesRemaining > 0) {
            tooltipComponents.add(Component.translatable("item.mobflowutilities.glimmer_sprout.uses_remaining", usesRemaining, maxUses)
                    .withStyle(ChatFormatting.GREEN));
        } else {
            tooltipComponents.add(Component.translatable("item.mobflowutilities.glimmer_sprout.depleted_tooltip")
                    .withStyle(ChatFormatting.RED));
        }

        tooltipComponents.add(Component.translatable("item.mobflowutilities.glimmer_sprout.area", Config.getGlimmerSproutConversionArea())
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("item.mobflowutilities.glimmer_sprout.light_requirement", Config.getGlimmerGrassConversionLightLevel())
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(DataComponents.DAMAGE, 0) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int currentUses = stack.getOrDefault(DataComponents.DAMAGE, 0);
        int maxUses = Config.getGlimmerSproutUses();
        return Math.round(13.0F * (1.0F - (float) currentUses / maxUses));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int currentUses = stack.getOrDefault(DataComponents.DAMAGE, 0);
        int maxUses = Config.getGlimmerSproutUses();
        float ratio = 1.0F - (float) currentUses / maxUses;
        return ratio > 0.5F ? 0x989C04 : 0x4B4A52; // 10040012 : 4915330 in hex
    }
}