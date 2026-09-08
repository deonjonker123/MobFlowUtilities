package com.misterd.mobflowutilities.blockentity.custom;

import com.misterd.mobflowutilities.blockentity.MFUBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class GigaTankBlockEntity extends BlockEntity {
    private static final int MAX_CAPACITY = 1_024_000;

    public final FluidTank tank = new FluidTank(MAX_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public GigaTankBlockEntity(BlockPos pos, BlockState state) {
        super(MFUBlockEntities.GIGATANK_BE.get(), pos, state);
    }

    public boolean canFillBucket() {
        return tank.getFluidAmount() >= 1000;
    }

    public void loadFromItem(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (tag.contains("Tank")) {
                tank.readFromNBT(level.registryAccess(), tag.getCompound("Tank"));
            }
        }
    }

    public void saveToItem(ItemStack stack) {
        if (!tank.isEmpty()) {
            CompoundTag nbt = new CompoundTag();
            nbt.put("Tank", tank.writeToNBT(level.registryAccess(), new CompoundTag()));
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        }
    }

    public void drops() {
        // Tanks don't drop contents as items, they're saved in the block item
    }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    public int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Tank", tank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tank.readFromNBT(registries, tag.getCompound("Tank"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}