package com.misterd.mobflowutilities.item.equipment;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public class PaxelItem extends TieredItem {

    private static final Set<ItemAbility> TOOL_ABILITIES = Set.of(
            ItemAbilities.PICKAXE_DIG,
            ItemAbilities.AXE_DIG,
            ItemAbilities.AXE_STRIP,
            ItemAbilities.AXE_SCRAPE,
            ItemAbilities.AXE_WAX_OFF,
            ItemAbilities.SHOVEL_DIG,
            ItemAbilities.SHOVEL_FLATTEN
    );

    public PaxelItem(Properties properties) {
        super(MFUToolTiers.GLOOMSTEEL, properties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return TOOL_ABILITIES.contains(ability);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return getTier().getSpeed();
        }
        return 1.0F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, miningEntity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return getTier().getEnchantmentValue();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return (int) (getTier().getUses() * 1.4D);
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState blockState = world.getBlockState(pos);

        BlockState resultToSet = useAsAxe(blockState, context);

        if (resultToSet == null) {
            if (context.getClickedFace() == Direction.DOWN) return InteractionResult.PASS;

            BlockState flattenResult = blockState.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
            if (flattenResult != null && world.isEmptyBlock(pos.above())) {
                world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                resultToSet = flattenResult;
            } else {
                BlockState douseResult = blockState.getToolModifiedState(context, ItemAbilities.SHOVEL_DOUSE, false);
                if (douseResult != null && !world.isClientSide) {
                    world.levelEvent(null, 1009, pos, 0);
                }
                resultToSet = douseResult;
            }

            if (resultToSet == null) return InteractionResult.PASS;
        }

        if (!world.isClientSide) {
            ItemStack stack = context.getItemInHand();
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
            }

            world.setBlock(pos, resultToSet, 11);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, resultToSet));

            if (player != null) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }
        }

        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Nullable
    private BlockState useAsAxe(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        BlockState result = state.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false);
        if (result != null) {
            world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            return result;
        }

        result = state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false);
        if (result != null) {
            world.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.levelEvent(player, 3005, pos, 0);
            return result;
        }

        result = state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false);
        if (result != null) {
            world.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.levelEvent(player, 3004, pos, 0);
            return result;
        }

        return null;
    }
}