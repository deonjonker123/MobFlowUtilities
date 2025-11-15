package com.misterd.mobflowutilities.entity.custom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.gui.custom.ControllerMenu;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
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
import net.neoforged.neoforge.items.ItemStackHandler;

public class ControllerBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(5) {
        public int getSlotLimit(int slot) {
            return 10;
        }

        protected void onContentsChanged(int slot) {
            ControllerBlockEntity.this.setChanged();
            if (!ControllerBlockEntity.this.level.isClientSide()) {
                ControllerBlockEntity.this.level.sendBlockUpdated(ControllerBlockEntity.this.getBlockPos(), ControllerBlockEntity.this.getBlockState(), ControllerBlockEntity.this.getBlockState(), 3);
            }

        }

        public boolean isItemValid(int slot, ItemStack stack) {
            boolean var10000;
            switch(slot) {
                case 0:
                    var10000 = stack.getItem() == MFUItems.SHARPNESS_MODULE.get();
                    break;
                case 1:
                    var10000 = stack.getItem() == MFUItems.FIRE_ASPECT_MODULE.get();
                    break;
                case 2:
                    var10000 = stack.getItem() == MFUItems.SMITE_MODULE.get();
                    break;
                case 3:
                    var10000 = stack.getItem() == MFUItems.BOA_MODULE.get();
                    break;
                case 4:
                    var10000 = stack.getItem() == MFUItems.LOOTING_MODULE.get();
                    break;
                default:
                    var10000 = false;
            }

            return var10000;
        }
    };
    private final Set<BlockPos> linkedPads = new HashSet();
    private boolean playerKillMode = false;

    public ControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(MFUBlockEntities.CONTROLLER_BE.get(), pos, blockState);
    }

    public void addPad(BlockPos padPos) {
        this.linkedPads.add(padPos);
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }

    }

    public void removePad(BlockPos padPos) {
        this.linkedPads.remove(padPos);
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }

    }

    public Set<BlockPos> getLinkedPads() {
        return new HashSet(this.linkedPads);
    }

    public List<BlockPos> getLinkedPadsList() {
        return new ArrayList(this.linkedPads);
    }

    public int getLinkedPadCount() {
        return this.linkedPads.size();
    }

    public void clearAllLinkedPads() {
        if (this.level != null && !this.level.isClientSide()) {
            Iterator var1 = this.linkedPads.iterator();

            while(var1.hasNext()) {
                BlockPos padPos = (BlockPos)var1.next();
                BlockEntity var4 = this.level.getBlockEntity(padPos);
                if (var4 instanceof DamagePadBlockEntity) {
                    DamagePadBlockEntity attackPad = (DamagePadBlockEntity)var4;
                    attackPad.clearControllerPos();
                }
            }
        }

        this.linkedPads.clear();
        this.setChanged();
    }

    public boolean isPlayerKillMode() {
        return this.playerKillMode;
    }

    public void setPlayerKillMode(boolean playerKillMode) {
        this.playerKillMode = playerKillMode;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }

    }

    public int getModuleCount(int slot) {
        if (slot >= 0 && slot < this.inventory.getSlots()) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            return stack.isEmpty() ? 0 : Math.min(stack.getCount(), 10);
        } else {
            return 0;
        }
    }

    public boolean hasSharpnessModule() {
        return this.getModuleCount(0) > 0;
    }

    public boolean hasFireAspectModule() {
        return this.getModuleCount(1) > 0;
    }

    public boolean hasSmiteModule() {
        return this.getModuleCount(2) > 0;
    }

    public boolean hasBaneOfArthropodsModule() {
        return this.getModuleCount(3) > 0;
    }

    public boolean hasLootingModule() {
        return this.getModuleCount(4) > 0;
    }

    public int getSharpnessLevel() {
        return this.getModuleCount(0);
    }

    public int getFireAspectLevel() {
        return this.getModuleCount(1);
    }

    public int getSmiteLevel() {
        return this.getModuleCount(2);
    }

    public int getBaneOfArthropodsLevel() {
        return this.getModuleCount(3);
    }

    public int getLootingLevel() {
        return this.getModuleCount(4);
    }

    public void clearContents() {
        for(int i = 0; i < this.inventory.getSlots(); ++i) {
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        this.setChanged();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(this.inventory.getSlots());

        for(int i = 0; i < this.inventory.getSlots(); ++i) {
            inv.setItem(i, this.inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public boolean removePadAt(BlockPos padPos) {
        if (this.linkedPads.remove(padPos)) {
            if (this.level != null && !this.level.isClientSide()) {
                BlockEntity var3 = this.level.getBlockEntity(padPos);
                if (var3 instanceof DamagePadBlockEntity) {
                    DamagePadBlockEntity attackPad = (DamagePadBlockEntity)var3;
                    attackPad.clearControllerPos();
                }

                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            }

            this.setChanged();
            return true;
        } else {
            return false;
        }
    }

    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", this.inventory.serializeNBT(registries));
        tag.putBoolean("playerKillMode", this.playerKillMode);
        ListTag padList = new ListTag();
        Iterator var4 = this.linkedPads.iterator();

        while(var4.hasNext()) {
            BlockPos padPos = (BlockPos)var4.next();
            padList.add(LongTag.valueOf(padPos.asLong()));
        }

        tag.put("linkedPads", padList);
    }

    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        this.playerKillMode = tag.getBoolean("playerKillMode");
        this.linkedPads.clear();
        if (tag.contains("linkedPads")) {
            ListTag padList = tag.getList("linkedPads", 4);

            for(int i = 0; i < padList.size(); ++i) {
                long posLong = ((LongTag)padList.get(i)).getAsLong();
                BlockPos padPos = BlockPos.of(posLong);
                this.linkedPads.add(padPos);
            }
        }

    }

    public Component getDisplayName() {
        return Component.translatable("menu.flowtech.controller");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ControllerMenu(i, inventory, this);
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag(Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }
}