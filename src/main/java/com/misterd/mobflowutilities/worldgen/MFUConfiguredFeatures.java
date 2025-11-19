package com.misterd.mobflowutilities.worldgen;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.OptionalInt;

public class MFUConfiguredFeatures {

    // Separated overworld ore keys
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_GLIMMERSTEEL_ORE_KEY = registerKey("overworld_glimmersteel_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_GLOOMSTEEL_ORE_KEY = registerKey("overworld_gloomsteel_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_MFU_ORE_KEY = registerKey("nether_mfu_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_MFU_ORE_KEY = registerKey("end_mfu_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOOMWOOD_KEY = registerKey("gloomwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLIMMERWOOD_KEY = registerKey("glimmerwood");

    public static final ResourceKey<ConfiguredFeature<?, ?>> UMBRAL_BERRY_BUSH_KEY = registerKey("umbral_berry_bush");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RADIANT_BERRY_BUSH_KEY = registerKey("radiant_berry_bush");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        TagMatchTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        TagMatchTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        BlockMatchTest netherReplaceables = new BlockMatchTest(Blocks.NETHERRACK);
        BlockMatchTest endReplaceables = new BlockMatchTest(Blocks.END_STONE);

        List<OreConfiguration.TargetBlockState> overworldGlimmersteelOres = List.of(
                OreConfiguration.target(stoneOreReplaceables, MFUBlocks.GLIMMERSTEEL_STONE_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateOreReplaceables, MFUBlocks.GLIMMERSTEEL_DEEPSLATE_ORE.get().defaultBlockState())
        );

        List<OreConfiguration.TargetBlockState> overworldGloomsteelOres = List.of(
                OreConfiguration.target(stoneOreReplaceables, MFUBlocks.GLOOMSTEEL_STONE_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateOreReplaceables, MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get().defaultBlockState())
        );

        register(context, OVERWORLD_GLIMMERSTEEL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldGlimmersteelOres, 12));
        register(context, OVERWORLD_GLOOMSTEEL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldGloomsteelOres, 12));

        List<OreConfiguration.TargetBlockState> netherOres = List.of(
                OreConfiguration.target(netherReplaceables, MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get().defaultBlockState()),
                OreConfiguration.target(netherReplaceables, MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE.get().defaultBlockState())
        );
        register(context, NETHER_MFU_ORE_KEY, Feature.ORE, new OreConfiguration(netherOres, 12));

        List<OreConfiguration.TargetBlockState> endOres = List.of(
                OreConfiguration.target(endReplaceables, MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get().defaultBlockState()),
                OreConfiguration.target(endReplaceables, MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE.get().defaultBlockState())
        );
        register(context, END_MFU_ORE_KEY, Feature.ORE, new OreConfiguration(endOres, 12));

        register(context, GLIMMERWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(MFUBlocks.GLIMMERWOOD_LOG.get()),
                new FancyTrunkPlacer(3, 11, 0),

                BlockStateProvider.simple(MFUBlocks.GLIMMERWOOD_LEAVES.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),

                new TwoLayersFeatureSize(1, 0, 2)).build());

        register(context, GLOOMWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(MFUBlocks.GLOOMWOOD_LOG.get()),
                new DarkOakTrunkPlacer(6, 2, 1),

                BlockStateProvider.simple(MFUBlocks.GLOOMWOOD_LEAVES.get()),
                new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)),

                new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())).ignoreVines().build()
        );

        register(context, UMBRAL_BERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(MFUBlocks.UMBRAL_BERRY_BUSH.get()
                                .defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3))
                        ), List.of(
                                Blocks.DIRT,
                                Blocks.PODZOL,
                                Blocks.MYCELIUM,
                                Blocks.GRASS_BLOCK,
                                Blocks.MUD
                        )));

        register(context, RADIANT_BERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(MFUBlocks.RADIANT_BERRY_BUSH.get()
                                .defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3))
                        ), List.of(
                                Blocks.DIRT,
                                Blocks.GRASS_BLOCK
                        )));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}