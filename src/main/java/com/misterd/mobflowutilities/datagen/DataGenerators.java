package com.misterd.mobflowutilities.datagen;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.datagen.custom.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MobFlowUtilities.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherRegistryEntries(GatherDataRegistryEntriesEvent event) {
        event.add(Registries.LOOT_TABLE, new LootTableProvider(
                        Set.of(),
                        List.of(new LootTableProvider.SubProviderEntry(MFULootTableProvider::new, LootContextParamSets.BLOCK))
                ))
                .add(MFURecipeProvider.create());
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        addProviders(event);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Server event) {
        addProviders(event);
    }

    private static void addProviders(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();

        BlockTagsProvider blockTagsProvider = new MFUBlockTagProvider(packOutput, lookupProvider);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, new MFUItemTagProvider(packOutput, lookupProvider));

        generator.addProvider(true, new MFUItemModelProvider(packOutput));
        generator.addProvider(true, new MFUFluidTagsProvider(packOutput, lookupProvider));
    }
}