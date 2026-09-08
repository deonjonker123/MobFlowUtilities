package com.misterd.mobflowutilities.block.custom;

import com.misterd.mobflowutilities.blockentity.custom.GigaTankBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GigaTankBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof GigaTankBlockEntity tankEntity) {
            if (stack.isEmpty()) {
                int current = tankEntity.getFluidAmount();
                int max = tankEntity.getMaxCapacity();

                if (current > 0) {
                    String fluidName = tankEntity.tank.getFluid().getHoverName().getString();
                    player.displayClientMessage(
                            Component.literal(String.format("%s: %,d / %,d mB", fluidName, current, max))
                                    .withStyle(ChatFormatting.AQUA),
                            true
                    );
                } else {
                    player.displayClientMessage(
                            Component.literal(String.format("Empty: 0 / %,d mB", max))
                                    .withStyle(ChatFormatting.GRAY),
                            true
                    );
                }
                return ItemInteractionResult.SUCCESS;
            }

            if (stack.is(Items.BUCKET)) {
                if (tankEntity.canFillBucket()) {
                    FluidStack drained = tankEntity.tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    if (!drained.isEmpty()) {
                        if (!player.isCreative()) {
                            stack.shrink(1);
                            ItemStack filledBucket = new ItemStack(drained.getFluid().getBucket());
                            if (!player.getInventory().add(filledBucket)) {
                                player.drop(filledBucket, false);
                            }
                        }
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return ItemInteractionResult.SUCCESS;
                    }
                } else {
                    player.displayClientMessage(Component.translatable("message.mobflowutilities.tank.full").withStyle(ChatFormatting.RED), true);
                    return ItemInteractionResult.FAIL;
                }
            } else if (stack.getItem() instanceof BucketItem bucketItem) {
                FluidStack fluidStack = new FluidStack(bucketItem.content, 1000);
                if (tankEntity.tank.isFluidValid(fluidStack)) {
                    int filled = tankEntity.tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0) {
                        if (!player.isCreative()) {
                            stack.shrink(1);
                            ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                            if (!player.getInventory().add(emptyBucket)) {
                                player.drop(emptyBucket, false);
                            }
                        }
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return ItemInteractionResult.SUCCESS;
                    } else {
                        player.displayClientMessage(Component.translatable("message.mobflowutilities.tank.full").withStyle(ChatFormatting.RED), true);
                        return ItemInteractionResult.FAIL;
                    }
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
        List<ItemStack> drops = new ArrayList<>();

        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof GigaTankBlockEntity tank) {
            ItemStack stack = new ItemStack(this);
            tank.saveToItem(stack);
            drops.add(stack);
        } else {
            drops.add(new ItemStack(this));
        }

        return drops;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.mobflowutilities.gigatank.subtitle").withStyle(ChatFormatting.GRAY));

        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();

            if (tag.contains("Tank")) {
                FluidTank tempTank = new FluidTank(Integer.MAX_VALUE);
                tempTank.readFromNBT(context.registries(), tag.getCompound("Tank"));
                FluidStack fluidStack = tempTank.getFluid();

                if (!fluidStack.isEmpty()) {
                    String fluidName = fluidStack.getHoverName().getString();
                    tooltipComponents.add(
                            Component.literal(String.format("%s - %,d mB", fluidName, fluidStack.getAmount()))
                                    .withStyle(ChatFormatting.AQUA)
                    );
                    return;
                }
            }

            tooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
        }
    }
}