package com.misterd.mobflowutilities.component.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public record VoidFilterData(List<ItemStack> filterItems, boolean ignoreNBT, boolean ignoreDurability) {

    // Clean codec compatible with NeoForged
    public static final Codec<VoidFilterData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ItemStack.CODEC)
                            .optionalFieldOf("filterItems", List.of())
                            .forGetter(data -> data.filterItems().stream().filter(stack -> !stack.isEmpty()).toList()),
                    Codec.BOOL.optionalFieldOf("ignoreNBT", true).forGetter(VoidFilterData::ignoreNBT),
                    Codec.BOOL.optionalFieldOf("ignoreDurability", true).forGetter(VoidFilterData::ignoreDurability)
            ).apply(instance, (items, ignoreNBT, ignoreDurability) -> {
                NonNullList<ItemStack> fullList = NonNullList.withSize(45, ItemStack.EMPTY);
                for (int i = 0; i < Math.min(items.size(), 45); i++) {
                    fullList.set(i, items.get(i));
                }
                return new VoidFilterData(fullList, ignoreNBT, ignoreDurability);
            })
    );

    public static final VoidFilterData DEFAULT = new VoidFilterData(createEmptyFilterList(), true, true);

    public VoidFilterData {
        // compact constructor; no decomp artifacts needed
    }

    private static NonNullList<ItemStack> createEmptyFilterList() {
        return NonNullList.withSize(45, ItemStack.EMPTY);
    }

    public void loadIntoHandler(ItemStackHandler handler) {
        for (int i = 0; i < Math.min(this.filterItems.size(), handler.getSlots()); i++) {
            handler.setStackInSlot(i, this.filterItems.get(i).copy());
        }
    }

    public static VoidFilterData fromHandler(ItemStackHandler handler, boolean ignoreNBT, boolean ignoreDurability) {
        NonNullList<ItemStack> items = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < handler.getSlots(); i++) {
            items.set(i, handler.getStackInSlot(i).copy());
        }
        return new VoidFilterData(items, ignoreNBT, ignoreDurability);
    }

    public boolean shouldVoidItem(ItemStack itemToCheck) {
        if (itemToCheck.isEmpty()) return false;

        for (ItemStack filterItem : filterItems) {
            if (!filterItem.isEmpty() && itemsMatch(itemToCheck, filterItem)) {
                return true;
            }
        }
        return false;
    }

    private boolean itemsMatch(ItemStack itemToCheck, ItemStack filterItem) {
        if (!itemToCheck.is(filterItem.getItem())) return false;

        if (ignoreNBT && ignoreDurability) return true;
        if (ignoreNBT) return itemToCheck.getDamageValue() == filterItem.getDamageValue();
        if (ignoreDurability) {
            ItemStack itemCopy = itemToCheck.copy();
            ItemStack filterCopy = filterItem.copy();
            itemCopy.setDamageValue(0);
            filterCopy.setDamageValue(0);
            return ItemStack.isSameItemSameComponents(itemCopy, filterCopy);
        }
        return ItemStack.isSameItemSameComponents(itemToCheck, filterItem);
    }
}