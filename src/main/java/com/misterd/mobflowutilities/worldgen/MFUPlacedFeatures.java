package com.misterd.mobflowutilities.worldgen;

import com.misterd.mobflowutilities.MobFlowUtilities;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class MFUPlacedFeatures {

    public static final ResourceKey<PlacedFeature> OVERWORLD_GLOOMSTEEL_ORE_PLACED_KEY = registerKey("overworld_gloomsteel_ore_placed");
    public static final ResourceKey<PlacedFeature> OVERWORLD_GLOOMSTEEL_ORE_HIDDEN_KEY = registerKey("overworld_gloomsteel_ore_hidden");
    public static final ResourceKey<PlacedFeature> NETHER_GLOOMSTEEL_ORE_PLACED_KEY = registerKey("nether_gloomsteel_ore_placed");
    public static final ResourceKey<PlacedFeature> END_GLOOMSTEEL_ORE_PLACED_KEY = registerKey("end_gloomsteel_ore_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Overworld Gloomsteel ore: 4 veins per chunk, Y: -64 to 64
        register(context, OVERWORLD_GLOOMSTEEL_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(MFUConfiguredFeatures.OVERWORLD_GLOOMSTEEL_ORE_KEY),
                MFUOrePlacement.commonOrePlacement(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64))));

        // Hidden Overworld Gloomsteel ore: 2 veins per chunk, Y: -32 to 32 (more concentrated, air-exposed)
        register(context, OVERWORLD_GLOOMSTEEL_ORE_HIDDEN_KEY,
                configuredFeatures.getOrThrow(MFUConfiguredFeatures.OVERWORLD_GLOOMSTEEL_ORE_HIDDEN_KEY),
                MFUOrePlacement.commonOrePlacement(2,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(32))));

        // Nether Gloomsteel ore: 4 veins per chunk, Y: -64 to 80
        register(context, NETHER_GLOOMSTEEL_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(MFUConfiguredFeatures.NETHER_GLOOMSTEEL_ORE_KEY),
                MFUOrePlacement.commonOrePlacement(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        // End Gloomsteel ore: 4 veins per chunk, Y: -64 to 80
        register(context, END_GLOOMSTEEL_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(MFUConfiguredFeatures.END_GLOOMSTEEL_ORE_KEY),
                MFUOrePlacement.commonOrePlacement(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
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