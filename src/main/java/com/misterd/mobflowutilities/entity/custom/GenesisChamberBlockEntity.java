package com.misterd.mobflowutilities.entity.custom;

import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.gui.custom.GenesisChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class GenesisChamberBlockEntity extends BlockEntity implements MenuProvider {
    public Entity cachedEntity = null;
    private EntityType<?> cachedType = null;

    public final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0) {
                return 1;
            }
            return super.getSlotLimit(slot);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public GenesisChamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.GENESIS_CHAMBER_BE.get(), pos, blockState);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.mobflowutilities.genesis_chamber");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new GenesisChamberMenu(i, inventory, this);
    }

    public Entity getOrCreateRenderedEntity(EntityType<?> type) {
        if (cachedEntity != null && cachedEntity.getType() == type) {
            return cachedEntity;
        }

        Level level = getLevel();
        if (level == null) return null;

        Entity entity = type.create(level);
        if (entity == null) return null;

        entity.noPhysics = true;
        if (entity instanceof Mob mobEntity) {
            mobEntity.setNoAi(true);
        }

        entity.invulnerableTime = Integer.MAX_VALUE;

        this.cachedEntity = entity;
        return entity;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }
}
