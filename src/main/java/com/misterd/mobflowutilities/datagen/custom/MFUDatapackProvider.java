package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.worldgen.MFUBiomeModifiers;
import com.misterd.mobflowutilities.worldgen.MFUConfiguredFeatures;
import com.misterd.mobflowutilities.worldgen.MFUPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MFUDatapackProvider extends DatapackBuiltinEntriesProvider {

    private static final String MOD_ID = "mobflowutilities";

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, MFUConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, MFUPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, MFUBiomeModifiers::bootstrap);

    public MFUDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MOD_ID));
    }
}