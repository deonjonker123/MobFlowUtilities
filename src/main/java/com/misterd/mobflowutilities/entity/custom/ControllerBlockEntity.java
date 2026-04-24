package com.misterd.mobflowutilities.entity.custom;

import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.gui.custom.ControllerMenu;
import com.misterd.mobflowutilities.item.MFUItems;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControllerBlockEntity extends BlockEntity implements MenuProvider {

    private static final int SLOT_SHARPNESS = 0;
    private static final int SLOT_FIRE_ASPECT = 1;
    private static final int SLOT_SMITE = 2;
    private static final int SLOT_BOA = 3;
    private static final int SLOT_LOOTING = 4;
    private static final int SLOT_COUNT = 5;

    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(SLOT_COUNT) {
        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return 10;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (resource.isEmpty()) return false;
            return switch (index) {
                case SLOT_SHARPNESS -> resource.toStack().getItem() == MFUItems.SHARPNESS_MODULE.get();
                case SLOT_FIRE_ASPECT -> resource.toStack().getItem() == MFUItems.FIRE_ASPECT_MODULE.get();
                case SLOT_SMITE -> resource.toStack().getItem() == MFUItems.SMITE_MODULE.get();
                case SLOT_BOA -> resource.toStack().getItem() == MFUItems.BOA_MODULE.get();
                case SLOT_LOOTING -> resource.toStack().getItem() == MFUItems.LOOTING_MODULE.get();
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            ControllerBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    private final Set<BlockPos> linkedPads = new HashSet<>();
    private boolean playerKillMode = false;

    public ControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.CONTROLLER_BE.get(), pos, blockState);
    }

    public ItemStack getStack(int slot) {
        ItemResource res = inventory.getResource(slot);
        if (res.isEmpty()) return ItemStack.EMPTY;
        return res.toStack(inventory.getAmountAsInt(slot));
    }

    public int getModuleCount(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return 0;
        return Math.min(getStack(slot).getCount(), 10);
    }

    public boolean hasSharpnessModule() {
        return getModuleCount(SLOT_SHARPNESS) > 0;
    }

    public boolean hasFireAspectModule() {
        return getModuleCount(SLOT_FIRE_ASPECT) > 0;
    }

    public boolean hasSmiteModule() {
        return getModuleCount(SLOT_SMITE) > 0;
    }

    public boolean hasBaneOfArthropodsModule() {
        return getModuleCount(SLOT_BOA) > 0;
    }

    public boolean hasLootingModule() {
        return getModuleCount(SLOT_LOOTING) > 0;
    }

    public int getSharpnessLevel() {
        return getModuleCount(SLOT_SHARPNESS);
    }

    public int getFireAspectLevel() {
        return getModuleCount(SLOT_FIRE_ASPECT);
    }

    public int getSmiteLevel() {
        return getModuleCount(SLOT_SMITE);
    }

    public int getBaneOfArthropodsLevel() {
        return getModuleCount(SLOT_BOA);
    }

    public int getLootingLevel() {
        return getModuleCount(SLOT_LOOTING);
    }

    public void addPad(BlockPos padPos) {
        linkedPads.add(padPos);
        setChangedAndUpdate();
    }

    public void removePad(BlockPos padPos) {
        linkedPads.remove(padPos);
        setChangedAndUpdate();
    }

    public boolean removePadAt(BlockPos padPos) {
        if (!linkedPads.remove(padPos)) return false;
        if (level != null && !level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(padPos);
            if (be instanceof DamagePadBlockEntity pad) pad.clearControllerPos();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
        setChanged();
        return true;
    }

    public void clearAllLinkedPads() {
        if (level != null && !level.isClientSide()) {
            for (BlockPos padPos : linkedPads) {
                BlockEntity be = level.getBlockEntity(padPos);
                if (be instanceof DamagePadBlockEntity pad) pad.clearControllerPos();
            }
        }
        linkedPads.clear();
        setChanged();
    }

    public Set<BlockPos> getLinkedPads() {
        return new HashSet<>(linkedPads);
    }

    public List<BlockPos> getLinkedPadsList() {
        return new ArrayList<>(linkedPads);
    }

    public int getLinkedPadCount() {
        return linkedPads.size();
    }

    public boolean isPlayerKillMode() {
        return playerKillMode;
    }

    public void setPlayerKillMode(boolean playerKillMode) {
        this.playerKillMode = playerKillMode;
        setChangedAndUpdate();
    }

    public void clearContents() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack existing = getStack(i);
            if (!existing.isEmpty()) {
                try (var tx = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
                    inventory.extract(i, ItemResource.of(existing), existing.getCount(), tx);
                    tx.commit();
                }
            }
        }
        setChanged();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) inv.setItem(i, getStack(i));
        Containers.dropContents(level, worldPosition, inv);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.mobflowutilities.controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ControllerMenu(id, inv, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output);
        output.putBoolean("playerKillMode", playerKillMode);

        output.putInt("linkedPadCount", linkedPads.size());
        int i = 0;
        for (BlockPos padPos : linkedPads)
            output.putLong("linkedPad_" + i++, padPos.asLong());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input);
        playerKillMode = input.getBooleanOr("playerKillMode", false);

        linkedPads.clear();
        int padCount = input.getIntOr("linkedPadCount", 0);
        for (int i = 0; i < padCount; i++)
            input.getLong("linkedPad_" + i).ifPresent(l -> linkedPads.add(BlockPos.of(l)));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    private void setChangedAndUpdate() {
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
}