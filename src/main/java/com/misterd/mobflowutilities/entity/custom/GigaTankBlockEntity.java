package com.misterd.mobflowutilities.entity.custom;

import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class GigaTankBlockEntity extends BlockEntity {
    private static final int MAX_CAPACITY = 1_024_000;

    public final FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1, MAX_CAPACITY) {
        @Override
        protected void onContentsChanged(int slot, FluidStack previous) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public GigaTankBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MFUBlockEntities.GIGA_TANK_BE.get(), worldPosition, blockState);
    }

    public boolean canFillBucket() {
        return tank.getAmountAsInt(0) >= FluidType.BUCKET_VOLUME;
    }

    public int getFluidAmount() {
        return tank.getAmountAsInt(0);
    }

    public int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public FluidResource getFluidResource() {
        return tank.getResource(0);
    }

    public FluidStack getFluidStack() {
        FluidResource res = tank.getResource(0);
        if (res.isEmpty()) return FluidStack.EMPTY;
        return res.toStack(tank.getAmountAsInt(0));
    }

    public void loadFromItem(ItemStack stack) {
        if (level == null || !stack.has(DataComponents.CUSTOM_DATA)) return;
        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        if (!tag.contains("Tank")) return;

        var ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        FluidStack.OPTIONAL_CODEC.parse(ops, tag.get("Tank")).result().ifPresent(fs -> {
            if (!fs.isEmpty()) {
                try (Transaction tx = Transaction.openRoot()) {
                    tank.insert(0, FluidResource.of(fs), fs.getAmount(), tx);
                    tx.commit();
                }
            }
        });
    }

    public void saveToItem(ItemStack stack) {
        if (level == null) return;
        FluidResource res = tank.getResource(0);
        if (res.isEmpty()) return;

        var ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        FluidStack fs = res.toStack(tank.getAmountAsInt(0));
        FluidStack.CODEC.encodeStart(ops, fs).result().ifPresent(tag -> {
            CompoundTag nbt = new CompoundTag();
            nbt.put("Tank", tag);
            nbt.putInt("Amount", tank.getAmountAsInt(0));
            nbt.putString("FluidName", getFluidStack().getHoverName().getString());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        });
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        tank.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tank.deserialize(input);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }
}
