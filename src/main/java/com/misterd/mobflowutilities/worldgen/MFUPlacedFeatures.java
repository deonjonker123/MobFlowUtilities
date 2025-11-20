package com.misterd.mobflowutilities.worldgen;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class MFUPlacedFeatures {

    public static final ResourceKey<PlacedFeature> OVERWORLD_GLIMMERSTEEL_ORE_PLACED_KEY = registerKey("overworld_glimmersteel_ore_placed");
    public static final ResourceKey<PlacedFeature> OVERWORLD_GLOOMSTEEL_ORE_PLACED_KEY = registerKey("overworld_gloomsteel_ore_placed");

    public static final ResourceKey<PlacedFeature> NETHER_MFU_ORE_PLACED_KEY = registerKey("nether_mfu_ore_placed");
    public static final ResourceKey<PlacedFeature> END_MFU_ORE_PLACED_KEY = registerKey("end_mfu_ore_placed");

    public static final ResourceKey<PlacedFeature> GLOOMWOOD_PLACED_KEY = registerKey("gloomwood_placed");
    public static final ResourceKey<PlacedFeature> GLIMMERWOOD_PLACED_KEY = registerKey("glimmerwood_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, OVERWORLD_GLIMMERSTEEL_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(MFUConfiguredFeatures.OVERWORLD_GLIMMERSTEEL_ORE_KEY),
                MFUOrePlacement.commonOrePlacement(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(1), VerticalAnchor.absolute(64))));

        register(context, OVERWORLD_GLOOMSTEEL_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(MFUConfiguredFeatures.OVERWORLD_GLOOMSTEEL_ORE_KEY),
                MFUOrePlacement.commonOrePlacement(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));

        register(context, NETHER_MFU_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(MFUConfiguredFeatures.NETHER_MFU_ORE_KEY),
                MFUOrePlacement.commonOrePlacement(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, END_MFU_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(MFUConfiguredFeatures.END_MFU_ORE_KEY),
                MFUOrePlacement.commonOrePlacement(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, GLOOMWOOD_PLACED_KEY, configuredFeatures.getOrThrow(MFUConfiguredFeatures.GLOOMWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.05F, 1), MFUBlocks.GLOOMWOOD_SAPLING.get()));

        register(context, GLIMMERWOOD_PLACED_KEY, configuredFeatures.getOrThrow(MFUConfiguredFeatures.GLIMMERWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.05F, 1), MFUBlocks.GLIMMERWOOD_SAPLING.get()));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}