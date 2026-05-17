package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.fluid.MFUFluids;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MFUFluidTagsProvider extends FluidTagsProvider {

    public MFUFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(MFUTags.Fluids.EXPERIENCE)
                .add(MFUFluids.LIQUID_XP_SOURCE.get())
                .add(MFUFluids.LIQUID_XP_FLOWING.get());
    }
}