package com.misterd.mobflowutilities.worldgen;

import com.misterd.mobflowutilities.MobFlowUtilities;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MFUBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_OVERWORLD_GLOOMSTEEL_ORE = registerKey("add_overworld_gloomsteel_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_GLOOMSTEEL_ORE = registerKey("add_nether_gloomsteel_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_GLOOMSTEEL_ORE = registerKey("add_end_gloomsteel_ore");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(ADD_OVERWORLD_GLOOMSTEEL_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(MFUPlacedFeatures.OVERWORLD_GLOOMSTEEL_ORE_PLACED_KEY),
                                placedFeatures.getOrThrow(MFUPlacedFeatures.OVERWORLD_GLOOMSTEEL_ORE_HIDDEN_KEY)
                        ),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(ADD_NETHER_GLOOMSTEEL_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_NETHER),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(MFUPlacedFeatures.NETHER_GLOOMSTEEL_ORE_PLACED_KEY)
                        ),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(ADD_END_GLOOMSTEEL_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_END),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(MFUPlacedFeatures.END_GLOOMSTEEL_ORE_PLACED_KEY)
                        ),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, name)
        );
    }
}