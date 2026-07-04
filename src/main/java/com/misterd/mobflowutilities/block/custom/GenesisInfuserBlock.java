package com.misterd.mobflowutilities.block.custom;

import com.misterd.mobflowutilities.blockentity.MFUBlockEntities;
import com.misterd.mobflowutilities.blockentity.custom.GenesisInfuserBlockEntity;
import com.misterd.mobflowutilities.util.MFUTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class GenesisInfuserBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
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
        return SHAPE;
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
        if (!level.isClientSide()) {
            return blockEntityType == MFUBlockEntities.GENESIS_INFUSER_BE.get()
                    ? (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof GenesisInfuserBlockEntity genesisInfuser) {
                    genesisInfuser.tick();
                }
            }
                    : null;
        }
        return null;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof GenesisInfuserBlockEntity infuser)) return InteractionResult.SUCCESS;

        if (stack.getItem() instanceof BucketItem bucketItem && isExperienceFluid(bucketItem.content)) {
            if (tryFillFromBucket(infuser, bucketItem, player, stack, level, pos)) return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.BUCKET)) {
            if (tryDrainToBucket(infuser, player, stack, level, pos)) return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(infuser, Component.translatable("gui.mobflowutilities.genesis_infuser")), pos);
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean isExperienceFluid(Fluid fluid) {
        return fluid != null && fluid.builtInRegistryHolder().is(MFUTags.Fluids.EXPERIENCE);
    }

    private boolean tryFillFromBucket(GenesisInfuserBlockEntity infuser, BucketItem bucketItem, Player player, ItemStack stack, Level level, BlockPos pos) {
        FluidResource res = FluidResource.of(bucketItem.content);
        try (Transaction tx = Transaction.openRoot()) {
            int filled = infuser.tank.insert(0, res, FluidType.BUCKET_VOLUME, tx);
            if (filled != FluidType.BUCKET_VOLUME) return false;
            tx.commit();
        }
        if (!player.isCreative()) {
            stack.shrink(1);
            ItemStack empty = new ItemStack(Items.BUCKET);
            if (!player.getInventory().add(empty)) player.drop(empty, false);
        }
        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    private boolean tryDrainToBucket(GenesisInfuserBlockEntity infuser, Player player, ItemStack stack, Level level, BlockPos pos) {
        FluidResource res = infuser.getFluidResource();
        if (res.isEmpty()) return false;

        Fluid fluid = res.getFluid();
        Item bucket = fluid.getBucket();
        if (bucket == Items.AIR) return false;

        try (Transaction tx = Transaction.openRoot()) {
            int drained = infuser.tank.extract(0, res, FluidType.BUCKET_VOLUME, tx);
            if (drained != FluidType.BUCKET_VOLUME) return false;
            tx.commit();
        }
        if (!player.isCreative()) {
            stack.shrink(1);
            ItemStack filled = new ItemStack(bucket);
            if (!player.getInventory().add(filled)) player.drop(filled, false);
        }
        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof GenesisInfuserBlockEntity infuser && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(infuser, Component.translatable("gui.mobflowutilities.genesis_infuser")), pos);
        }
        return InteractionResult.SUCCESS;
    }
}