package com.misterd.mobflowutilities.block.custom;

import com.misterd.mobflowutilities.entity.custom.GigaTankBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class GigaTankBlock extends BaseEntityBlock {
    public static final MapCodec<GigaTankBlock> CODEC = simpleCodec(GigaTankBlock::new);
    
    public GigaTankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (!(level.getBlockEntity(pos) instanceof GigaTankBlockEntity tankEntity)) {
            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            int current = tankEntity.getFluidAmount();
            int max = tankEntity.getMaxCapacity();
            if (current > 0) {
                player.sendOverlayMessage(Component.literal(String.format("%s: %,d / %,d mB", tankEntity.getFluidStack().getHoverName().getString(), current, max)).withStyle(ChatFormatting.AQUA));
            } else {
                player.sendOverlayMessage(Component.literal(String.format("Empty: 0 / %,d mB", max)).withStyle(ChatFormatting.GRAY));
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.BUCKET)) {
            if (!tankEntity.canFillBucket()) {
                player.sendOverlayMessage(Component.translatable("message.mobflowutilities.tank.empty").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }
            FluidResource res = tankEntity.getFluidResource();
            if (!res.isEmpty()) {
                try (Transaction tx = Transaction.openRoot()) {
                    int drained = tankEntity.tank.extract(0, res, FluidType.BUCKET_VOLUME, tx);
                    if (drained == FluidType.BUCKET_VOLUME) {
                        tx.commit();
                        if (!player.isCreative()) {
                            stack.shrink(1);
                            ItemStack filledBucket = new ItemStack(res.getFluid().getBucket());
                            if (!player.getInventory().add(filledBucket)) player.drop(filledBucket, false);
                        }
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        } else if (stack.getItem() instanceof BucketItem bucketItem) {
            FluidResource res = FluidResource.of(bucketItem.content);
            try (Transaction tx = Transaction.openRoot()) {
                int filled = tankEntity.tank.insert(0, res, FluidType.BUCKET_VOLUME, tx);
                if (filled > 0) {
                    tx.commit();
                    if (!player.isCreative()) {
                        stack.shrink(1);
                        ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                        if (!player.getInventory().add(emptyBucket)) player.drop(emptyBucket, false);
                    }
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                } else {
                    player.sendOverlayMessage(Component.translatable("message.mobflowutilities.tank.full").withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof GigaTankBlockEntity tank) {
            tank.loadFromItem(stack);
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new java.util.ArrayList<>();
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof GigaTankBlockEntity tank) {
            ItemStack stack = new ItemStack(this);
            tank.saveToItem(stack);
            drops.add(stack);
        } else {
            drops.add(new ItemStack(this));
        }
        return drops;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GigaTankBlockEntity(blockPos, blockState);
    }
}
