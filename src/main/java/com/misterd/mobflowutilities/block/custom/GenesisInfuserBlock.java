package com.misterd.mobflowutilities.block.custom;

import com.misterd.mobflowutilities.blockentity.MFUBlockEntities;
import com.misterd.mobflowutilities.blockentity.custom.GenesisInfuserBlockEntity;
import com.misterd.mobflowutilities.util.MFUShapes;
import com.misterd.mobflowutilities.util.MFUTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GenesisInfuserBlock extends BaseEntityBlock {
    private static final Map<Direction, VoxelShape> SHAPES = MFUShapes.rotateHorizontal(Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(1.0D, 2.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Block.box(2.0D, 8.0D, 2.0D, 4.0D, 14.0D, 4.0D),
            Block.box(2.0D, 8.0D, 12.0D, 4.0D, 14.0D, 14.0D),
            Block.box(12.0D, 8.0D, 2.0D, 14.0D, 14.0D, 4.0D),
            Block.box(12.0D, 8.0D, 12.0D, 14.0D, 14.0D, 14.0D),
            Block.box(4.0D, 8.0D, 4.0D, 12.0D, 14.0D, 12.0D),
            Block.box(1.0D, 14.0D, 1.0D, 15.0D, 15.0D, 15.0D),
            Block.box(5.0D, 15.0D, 5.0D, 11.0D, 16.0D, 11.0D),
            Block.box(4.0D, 3.0D, 0.5D, 12.0D, 7.0D, 1.0D),
            Block.box(0.0D, 3.0D, 7.0D, 1.0D, 12.0D, 9.0D),
            Block.box(15.0D, 3.0D, 7.0D, 16.0D, 12.0D, 9.0D),
            Block.box(1.0D, 11.0D, 7.0D, 4.0D, 12.0D, 9.0D),
            Block.box(12.0D, 11.0D, 7.0D, 15.0D, 12.0D, 9.0D)
    ));
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<GenesisInfuserBlock> CODEC = simpleCodec(GenesisInfuserBlock::new);

    public GenesisInfuserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GenesisInfuserBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return blockEntityType == MFUBlockEntities.GENESIS_INFUSER_BE.get()
                ? (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof GenesisInfuserBlockEntity infuser) infuser.tick();
        }
                : null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof GenesisInfuserBlockEntity infuser) {
                infuser.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof GenesisInfuserBlockEntity infuser)) return ItemInteractionResult.SUCCESS;

        if (stack.getItem() instanceof BucketItem bucketItem && isExperienceFluid(bucketItem.content)) {
            if (tryFillFromBucket(infuser, bucketItem, player, stack, level, pos)) return ItemInteractionResult.SUCCESS;
        }

        if (stack.is(Items.BUCKET)) {
            if (tryDrainToBucket(infuser, player, stack, level, pos)) return ItemInteractionResult.SUCCESS;
        }

        openMenu(infuser, player, pos);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof GenesisInfuserBlockEntity infuser) {
            openMenu(infuser, player, pos);
        }
        return InteractionResult.SUCCESS;
    }

    private void openMenu(GenesisInfuserBlockEntity infuser, Player player, BlockPos pos) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(infuser, Component.translatable("gui.mobflowutilities.genesis_infuser")), pos);
        }
    }

    private static boolean isExperienceFluid(Fluid fluid) {
        return fluid != null && fluid != Fluids.EMPTY && fluid.builtInRegistryHolder().is(MFUTags.Fluids.EXPERIENCE);
    }

    private boolean tryFillFromBucket(GenesisInfuserBlockEntity infuser, BucketItem bucketItem, Player player, ItemStack stack, Level level, BlockPos pos) {
        FluidStack bucketFluid = new FluidStack(bucketItem.content, FluidType.BUCKET_VOLUME);
        if (infuser.tank.fill(bucketFluid, IFluidHandler.FluidAction.SIMULATE) != FluidType.BUCKET_VOLUME) return false;
        infuser.tank.fill(bucketFluid, IFluidHandler.FluidAction.EXECUTE);
        if (!player.isCreative()) {
            stack.shrink(1);
            ItemStack empty = new ItemStack(Items.BUCKET);
            if (!player.getInventory().add(empty)) player.drop(empty, false);
        }
        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    private boolean tryDrainToBucket(GenesisInfuserBlockEntity infuser, Player player, ItemStack stack, Level level, BlockPos pos) {
        FluidStack contained = infuser.getFluid();
        if (contained.isEmpty()) return false;

        Item bucket = contained.getFluid().getBucket();
        if (bucket == Items.AIR) return false;

        if (infuser.tank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE).getAmount() != FluidType.BUCKET_VOLUME) return false;
        infuser.tank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
        if (!player.isCreative()) {
            stack.shrink(1);
            ItemStack filled = new ItemStack(bucket);
            if (!player.getInventory().add(filled)) player.drop(filled, false);
        }
        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }
}
