package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class MFUDataMapProvider extends DataMapProvider {

    public MFUDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(MFUItems.INFUSED_COAL.getId(), new FurnaceFuel(12_800), false)
                .add(MFUItems.INFUSED_CHARCOAL.getId(), new FurnaceFuel(12_800), false);

    }
}
