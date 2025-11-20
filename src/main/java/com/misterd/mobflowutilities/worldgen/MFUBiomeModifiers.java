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
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MFUBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_OVERWORLD_MFU_ORE = registerKey("add_overworld_mfu_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_MFU_ORE = registerKey("add_nether_mfu_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_MFU_ORE = registerKey("add_end_mfu_ore");

    public static final ResourceKey<BiomeModifier> ADD_TREE_GLOOMWOOD = registerKey("add_tree_gloomwood");
    public static final ResourceKey<BiomeModifier> ADD_TREE_GLIMMERWOOD = registerKey("add_tree_glimmerwood");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(ADD_OVERWORLD_MFU_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(MFUPlacedFeatures.OVERWORLD_GLIMMERSTEEL_ORE_PLACED_KEY),
                                placedFeatures.getOrThrow(MFUPlacedFeatures.OVERWORLD_GLOOMSTEEL_ORE_PLACED_KEY)
                        ),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(ADD_NETHER_MFU_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_NETHER),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(MFUPlacedFeatures.NETHER_MFU_ORE_PLACED_KEY)
                        ),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(ADD_END_MFU_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_END),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(MFUPlacedFeatures.END_MFU_ORE_PLACED_KEY)
                        ),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(ADD_TREE_GLOOMWOOD,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(
                                biomes.getOrThrow(Biomes.DARK_FOREST),
                                biomes.getOrThrow(Biomes.SWAMP),
                                biomes.getOrThrow(Biomes.TAIGA),
                                biomes.getOrThrow(Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                                biomes.getOrThrow(Biomes.OLD_GROWTH_PINE_TAIGA),
                                biomes.getOrThrow(Biomes.CHERRY_GROVE),
                                biomes.getOrThrow(Biomes.WINDSWEPT_HILLS),
                                biomes.getOrThrow(Biomes.MEADOW)
                        ),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(MFUPlacedFeatures.GLOOMWOOD_PLACED_KEY)
                        ),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(ADD_TREE_GLIMMERWOOD,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(
                                biomes.getOrThrow(Biomes.FOREST),
                                biomes.getOrThrow(Biomes.FLOWER_FOREST),
                                biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS),
                                biomes.getOrThrow(Biomes.BIRCH_FOREST),
                                biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST),
                                biomes.getOrThrow(Biomes.SPARSE_JUNGLE),
                                biomes.getOrThrow(Biomes.MEADOW),
                                biomes.getOrThrow(Biomes.PLAINS)
                        ),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(MFUPlacedFeatures.GLIMMERWOOD_PLACED_KEY)
                        ),
                        GenerationStep.Decoration.VEGETAL_DECORATION
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