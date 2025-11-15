package com.misterd.mobflowutilities.entity.custom;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import com.misterd.mobflowutilities.block.custom.DamagePadBlock;
import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.util.FakePlayerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
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
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;

public class DamagePadBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos controllerPos;
    @Nullable
    private UUID placer;
    private WeakReference<FakePlayer> fakePlayer = new WeakReference<>(null);
    private static final float BASE_DAMAGE = 5.0F;
    private static final int DAMAGE_INTERVAL = 5;
    private int tickCounter = 0;

    public DamagePadBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.DAMAGE_PAD_BE.get(), pos, blockState);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof DamagePadBlockEntity attackPad) {
                attackPad.tick();
            }
        };
    }

    public void tick() {
        if (this.level != null && !this.level.isClientSide()) {
            ++this.tickCounter;
            if (this.tickCounter >= 5) {
                this.tickCounter = 0;
                if (this.isPowered()) {
                    this.dealDamage();
                }
            }
        }
    }

    private boolean isPowered() {
        if (this.level == null) return false;
        BlockState state = this.getBlockState();
        return state.getBlock() instanceof DamagePadBlock ? state.getValue(DamagePadBlock.POWERED) : false;
    }

    private void dealDamage() {
        if (this.level != null) {
            AABB damageArea = new AABB(
                    this.worldPosition.getX(),
                    this.worldPosition.getY(),
                    this.worldPosition.getZ(),
                    this.worldPosition.getX() + 1.0D,
                    this.worldPosition.getY() + 1.0D,
                    this.worldPosition.getZ() + 1.0D
            ).inflate(0.0625D);

            List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, damageArea);
            if (!entities.isEmpty()) {
                ControllerBlockEntity controller = this.getLinkedController();
                if (controller != null) {
                    this.dealEnchantedDamage(entities, controller);
                } else {
                    this.dealDirectDamage(entities);
                }
            }
        }
    }

    private void dealEnchantedDamage(List<LivingEntity> entities, ControllerBlockEntity controller) {
        this.fakePlayer = FakePlayerHandler.get(this.fakePlayer, (ServerLevel) this.level, this.placer, this.worldPosition.below(-500));
        FakePlayer fakePlayer = this.fakePlayer.get();

        if (fakePlayer != null) {
            ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD);

            int sharpness = controller.getModuleCount(0);
            if (sharpness > 0) tempSword.enchant(this.level.holderOrThrow(Enchantments.SHARPNESS), sharpness * 10);

            int fireAspect = controller.getModuleCount(1);
            if (fireAspect > 0) tempSword.enchant(this.level.holderOrThrow(Enchantments.FIRE_ASPECT), fireAspect);

            int smite = controller.getModuleCount(2);
            if (smite > 0) tempSword.enchant(this.level.holderOrThrow(Enchantments.SMITE), smite * 10);

            int baneOfArthropods = controller.getModuleCount(3);
            if (baneOfArthropods > 0) tempSword.enchant(this.level.holderOrThrow(Enchantments.BANE_OF_ARTHROPODS), baneOfArthropods * 10);

            int looting = controller.getModuleCount(4);
            if (looting > 0) tempSword.enchant(this.level.holderOrThrow(Enchantments.LOOTING), looting);

            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, tempSword);

            for (LivingEntity entity : entities) {
                if (!(entity instanceof Player)) {
                    fakePlayer.attack(entity);
                    fakePlayer.attackStrengthTicker = 100;
                }
            }

            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    private void dealDirectDamage(List<LivingEntity> entities) {

        for (LivingEntity entity : entities) {
            if (!(entity instanceof Player)) {
                DamageSource damageSource = this.level.damageSources().generic();
                entity.hurt(damageSource, 5.0F);
            }
        }
    }

    @Nullable
    private ControllerBlockEntity getLinkedController() {
        if (this.controllerPos != null && this.level != null) {
            BlockEntity be = this.level.getBlockEntity(this.controllerPos);
            return (be instanceof ControllerBlockEntity controller) ? controller : null;
        }
        return null;
    }

    @Nullable
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    public void setControllerPos(@Nullable BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            DamagePadBlock.updateLinkedState(this.level, this.getBlockPos(), controllerPos != null);
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void clearControllerPos() {
        this.setControllerPos(null);
    }

    public boolean isLinked() {
        return this.controllerPos != null;
    }

    public boolean isLinkedTo(BlockPos pos) {
        return this.controllerPos != null && this.controllerPos.equals(pos);
    }

    public void setPlacer(@Nullable Player player) {
        this.placer = player != null ? player.getUUID() : null;
        this.setChanged();
    }

    @Nullable
    public UUID getPlacer() {
        return this.placer;
    }

    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.controllerPos != null) tag.putLong("controllerPos", this.controllerPos.asLong());
        if (this.placer != null) tag.putUUID("placer", this.placer);
    }

    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        this.controllerPos = tag.contains("controllerPos") ? BlockPos.of(tag.getLong("controllerPos")) : null;
        this.placer = tag.hasUUID("placer") ? tag.getUUID("placer") : null;
    }

    public CompoundTag getUpdateTag(Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (this.controllerPos != null) tag.putLong("controllerPos", this.controllerPos.asLong());
        if (this.placer != null) tag.putUUID("placer", this.placer);
        return tag;
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}