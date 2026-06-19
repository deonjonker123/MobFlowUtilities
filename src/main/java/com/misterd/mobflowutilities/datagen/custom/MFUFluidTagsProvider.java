package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.fluid.MFUFluids;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.CompletableFuture;

public class MFUFluidTagsProvider extends FluidTagsProvider {

    public MFUFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    private static ResourceKey<Fluid> key(Fluid fluid) {
        return fluid.builtInRegistryHolder().key();
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(MFUTags.Fluids.EXPERIENCE)
                .add(key(MFUFluids.LIQUID_XP_SOURCE.get()))
                .add(key(MFUFluids.LIQUID_XP_FLOWING.get()));
    }
}