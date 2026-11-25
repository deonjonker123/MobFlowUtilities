package com.misterd.mobflowutilities.blockentity.custom;

import com.misterd.mobflowutilities.blockentity.MFUBlockEntities;
import com.misterd.mobflowutilities.config.Config;
import com.misterd.mobflowutilities.gui.custom.ControllerMenu;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
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
import java.util.HashSet;
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
            cachedWeapon = null;
            ControllerBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    private final Set<BlockPos> linkedPads = new HashSet<>();
    @Nullable private ItemStack cachedWeapon;

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

    public ItemStack getWeapon() {
        if (cachedWeapon == null && level != null) cachedWeapon = buildWeapon(level);
        return cachedWeapon != null ? cachedWeapon : ItemStack.EMPTY;
    }

    private ItemStack buildWeapon(Level level) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        enchant(sword, level, Enchantments.SHARPNESS, getModuleCount(SLOT_SHARPNESS) * 10);
        enchant(sword, level, Enchantments.FIRE_ASPECT, getModuleCount(SLOT_FIRE_ASPECT));
        enchant(sword, level, Enchantments.SMITE, getModuleCount(SLOT_SMITE) * 10);
        enchant(sword, level, Enchantments.BANE_OF_ARTHROPODS, getModuleCount(SLOT_BOA) * 10);
        enchant(sword, level, Enchantments.LOOTING, getModuleCount(SLOT_LOOTING));
        return sword;
    }

    private static void enchant(ItemStack sword, Level level, ResourceKey<Enchantment> key, int enchantLevel) {
        if (enchantLevel > 0) sword.enchant(level.holderOrThrow(key), enchantLevel);
    }

    public enum PadLinkResult {
        SUCCESS,
        ALREADY_LINKED,
        TOO_FAR,
        FULL
    }

    public PadLinkResult addPad(BlockPos padPos) {
        if (linkedPads.contains(padPos)) return PadLinkResult.ALREADY_LINKED;
        pruneStalePads();

        int radius = Config.getDamagePadConnectionRadius();
        if (padPos.distSqr(this.worldPosition) > (long) radius * radius) {
            return PadLinkResult.TOO_FAR;
        }

        if (linkedPads.size() >= Config.getDamagePadMaxConnectedPads()) {
            return PadLinkResult.FULL;
        }

        linkedPads.add(padPos);
        setChangedAndUpdate();
        return PadLinkResult.SUCCESS;
    }

    public void removePad(BlockPos padPos) {
        if (linkedPads.remove(padPos)) setChangedAndUpdate();
    }

    public boolean hasPad(BlockPos padPos) {
        return linkedPads.contains(padPos);
    }

    private void pruneStalePads() {
        if (level == null || level.isClientSide()) return;
        boolean removed = linkedPads.removeIf(padPos -> level.hasChunkAt(padPos)
                && !(level.getBlockEntity(padPos) instanceof DamagePadBlockEntity pad && pad.isLinkedTo(worldPosition)));
        if (removed) setChangedAndUpdate();
    }

    public Set<BlockPos> getLinkedPads() {
        return new HashSet<>(linkedPads);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        drops();
        if (level == null || level.isClientSide()) return;
        for (BlockPos padPos : new HashSet<>(linkedPads)) {
            if (!level.hasChunkAt(padPos)) continue;
            if (level.getBlockEntity(padPos) instanceof DamagePadBlockEntity pad && pad.isLinkedTo(worldPosition)) {
                pad.onControllerRemoved();
            }
        }
        linkedPads.clear();
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

        output.putInt("linkedPadCount", linkedPads.size());
        int i = 0;
        for (BlockPos padPos : linkedPads)
            output.putLong("linkedPad_" + i++, padPos.asLong());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input);
        cachedWeapon = null;

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