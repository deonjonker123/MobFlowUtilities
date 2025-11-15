package com.misterd.mobflowutilities.worldgen;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class MFUConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_GLOOMSTEEL_ORE_KEY = registerKey("overworld_gloomsteel_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_GLOOMSTEEL_ORE_HIDDEN_KEY = registerKey("overworld_gloomsteel_ore_hidden");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_GLOOMSTEEL_ORE_KEY = registerKey("nether_gloomsteel_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_GLOOMSTEEL_ORE_KEY = registerKey("end_gloomsteel_ore");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        TagMatchTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        TagMatchTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        BlockMatchTest netherReplaceables = new BlockMatchTest(Blocks.NETHERRACK);
        BlockMatchTest endReplaceables = new BlockMatchTest(Blocks.END_STONE);

        List<OreConfiguration.TargetBlockState> overworldGloomsteelOres = List.of(
                OreConfiguration.target(stoneOreReplaceables, MFUBlocks.GLOOMSTEEL_STONE_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateOreReplaceables, MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get().defaultBlockState())
        );

        OreConfiguration hiddenGloomsteelConfig = new OreConfiguration(overworldGloomsteelOres, 12, 0.9F);

        register(context, OVERWORLD_GLOOMSTEEL_ORE_HIDDEN_KEY, Feature.ORE, hiddenGloomsteelConfig);
        register(context, OVERWORLD_GLOOMSTEEL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldGloomsteelOres, 12));

        register(context, NETHER_GLOOMSTEEL_ORE_KEY, Feature.ORE,
                new OreConfiguration(netherReplaceables, MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get().defaultBlockState(), 12));

        register(context, END_GLOOMSTEEL_ORE_KEY, Feature.ORE,
                new OreConfiguration(endReplaceables, MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get().defaultBlockState(), 12));
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