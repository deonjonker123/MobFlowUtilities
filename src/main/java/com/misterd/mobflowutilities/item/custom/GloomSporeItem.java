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

        // Check valid block
        if (!isConvertibleBlock(level.getBlockState(clickedPos))) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.gloom_spore.invalid_block"), true);
            return InteractionResult.FAIL;
        }

        // Check light too bright
        int lightLevel = level.getBrightness(LightLayer.BLOCK, clickedPos.above());
        int maxLight = Config.getDarkDirtConversionLightLevel();
        if (lightLevel > maxLight) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.gloom_spore.too_bright", lightLevel, maxLight), true);
            return InteractionResult.FAIL;
        }

        int currentUses = stack.getOrDefault(DataComponents.DAMAGE, 0);
        int maxUses = Config.getGloomSporeUses();

        if (currentUses >= maxUses) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.gloom_spore.depleted"), true);
            return InteractionResult.FAIL;
        }

        int area = Config.getGloomSporeConversionArea();
        int half = area / 2;
        int blocksConverted = 0;

        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                BlockPos targetPos = clickedPos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);

                if (!isConvertibleBlock(targetState)) continue;

                int targetLight = level.getBrightness(LightLayer.BLOCK, targetPos.above());
                if (targetLight <= maxLight) {
                    level.setBlock(targetPos, MFUBlocks.DARK_DIRT.get().defaultBlockState(), 3);
                    blocksConverted++;
                }
            }
        }

        if (blocksConverted == 0) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.gloom_spore.no_conversion"), true);
            return InteractionResult.FAIL;
        }

        // Apply usage
        stack.set(DataComponents.DAMAGE, currentUses + 1);

        if (currentUses + 1 >= maxUses) {
            stack.shrink(1);
            player.displayClientMessage(
                    Component.translatable("item.mobflowutilities.gloom_spore.consumed", blocksConverted),
                    true
            );
        } else {
            int remaining = maxUses - (currentUses + 1);
            player.displayClientMessage(
                    Component.translatable("item.mobflowutilities.gloom_spore.success", blocksConverted, remaining),
                    true
            );
        }

        return InteractionResult.SUCCESS;
    }

    private boolean isConvertibleBlock(BlockState state) {
        Block b = state.getBlock();
        return b == Blocks.DIRT ||
                b == Blocks.GRASS_BLOCK ||
                b == Blocks.PODZOL ||
                b == Blocks.MYCELIUM ||
                b == Blocks.ROOTED_DIRT ||
                b == Blocks.COARSE_DIRT;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.mobflowutilities.gloom_spore.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));

        int current = stack.getOrDefault(DataComponents.DAMAGE, 0);
        int max = Config.getGloomSporeUses();
        int remaining = max - current;

        if (remaining > 0) {
            tooltip.add(Component.translatable("item.mobflowutilities.gloom_spore.uses_remaining", remaining, max)
                    .withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("item.mobflowutilities.gloom_spore.depleted_tooltip")
                    .withStyle(ChatFormatting.RED));
        }

        tooltip.add(Component.translatable("item.mobflowutilities.gloom_spore.area", Config.getGloomSporeConversionArea())
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.mobflowutilities.gloom_spore.light_requirement", Config.getDarkDirtConversionLightLevel())
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(DataComponents.DAMAGE, 0) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int used = stack.getOrDefault(DataComponents.DAMAGE, 0);
        int max = Config.getGloomSporeUses();
        return Math.round(13.0F * (1.0F - (float) used / max));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int used = stack.getOrDefault(DataComponents.DAMAGE, 0);
        int max = Config.getGloomSporeUses();
        float ratio = 1F - (float) used / max;
        return ratio > 0.5F ? 0x4A4E4A : 0x8B8E90; // hex versions of 4868682 and 9109504
    }
}