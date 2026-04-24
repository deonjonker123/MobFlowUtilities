package com.misterd.mobflowutilities.entity.custom;

import com.misterd.mobflowutilities.block.custom.DamagePadBlock;
import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.util.FakePlayerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

public class DamagePadBlockEntity extends BlockEntity {

    @Nullable private BlockPos controllerPos;
    @Nullable private UUID placer;
    private WeakReference<FakePlayer> fakePlayer = new WeakReference<>(null);

    private static final float BASE_DAMAGE  = 5.0F;
    private static final int DAMAGE_INTERVAL = 5;
    private int tickCounter = 0;

    public DamagePadBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.DAMAGE_PAD_BE.get(), pos, blockState);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof DamagePadBlockEntity pad) pad.tick();
        };
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;
        tickCounter++;
        if (tickCounter >= DAMAGE_INTERVAL) {
            tickCounter = 0;
            if (isPowered()) dealDamage();
        }
    }

    private boolean isPowered() {
        BlockState state = getBlockState();
        return state.getBlock() instanceof DamagePadBlock && state.getValue(DamagePadBlock.POWERED);
    }

    private void dealDamage() {
        if (level == null) return;
        AABB area = new AABB(
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1
        ).inflate(0.0625);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
        if (entities.isEmpty()) return;

        ControllerBlockEntity controller = getLinkedController();
        if (controller != null) dealEnchantedDamage(entities, controller);
        else dealDirectDamage(entities);
    }

    private void dealEnchantedDamage(List<LivingEntity> entities, ControllerBlockEntity controller) {
        fakePlayer = FakePlayerHandler.get(fakePlayer, (ServerLevel) level, placer, worldPosition.below(-500));
        FakePlayer fp = fakePlayer.get();
        if (fp == null) return;

        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);

        int sharpness = controller.getModuleCount(0);
        if (sharpness > 0) sword.enchant(level.holderOrThrow(Enchantments.SHARPNESS), sharpness * 10);

        int fireAspect = controller.getModuleCount(1);
        if (fireAspect > 0) sword.enchant(level.holderOrThrow(Enchantments.FIRE_ASPECT), fireAspect);

        int smite = controller.getModuleCount(2);
        if (smite > 0) sword.enchant(level.holderOrThrow(Enchantments.SMITE),smite * 10);

        int boa = controller.getModuleCount(3);
        if (boa > 0) sword.enchant(level.holderOrThrow(Enchantments.BANE_OF_ARTHROPODS), boa * 10);

        int looting = controller.getModuleCount(4);
        if (looting > 0) sword.enchant(level.holderOrThrow(Enchantments.LOOTING), looting);

        fp.setItemInHand(InteractionHand.MAIN_HAND, sword);
        for (LivingEntity entity : entities) {
            if (!(entity instanceof Player)) {
                fp.attack(entity);
                fp.attackStrengthTicker = 100;
            }
        }
        fp.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    private void dealDirectDamage(List<LivingEntity> entities) {
        DamageSource src = level.damageSources().generic();
        for (LivingEntity entity : entities) {
            if (!(entity instanceof Player)) entity.hurt(src, BASE_DAMAGE);
        }
    }

    @Nullable
    private ControllerBlockEntity getLinkedController() {
        if (controllerPos == null || level == null) return null;
        BlockEntity be = level.getBlockEntity(controllerPos);
        return be instanceof ControllerBlockEntity c ? c : null;
    }

    @Nullable
    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public boolean isLinked() {
        return controllerPos != null;
    }

    public boolean isLinkedTo(BlockPos pos) {
        return controllerPos != null && controllerPos.equals(pos);
    }

    public void setControllerPos(@Nullable BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
        if (level != null && !level.isClientSide()) {
            DamagePadBlock.updateLinkedState(level, getBlockPos(), pos != null);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void clearControllerPos() { setControllerPos(null); }

    public void setPlacer(@Nullable Player player) {
        this.placer = player != null ? player.getUUID() : null;
        setChanged();
    }

    @Nullable public UUID getPlacer() {
        return placer;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (controllerPos != null) output.putLong("controllerPos", controllerPos.asLong());
        if (placer != null) output.putString("placer", placer.toString());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        long posLong = input.getLongOr("controllerPos", Long.MIN_VALUE);
        controllerPos = posLong != Long.MIN_VALUE ? BlockPos.of(posLong) : null;
        placer = input.getString("placer").map(UUID::fromString).orElse(null);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (controllerPos != null) tag.putLong("controllerPos", controllerPos.asLong());
        if (placer != null) tag.putString("placer", placer.toString());
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}