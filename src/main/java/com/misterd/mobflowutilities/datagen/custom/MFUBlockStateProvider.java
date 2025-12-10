package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;

public class MFUBlockStateProvider extends BlockStateProvider {

    public MFUBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MobFlowUtilities.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(MFUBlocks.GLOOMSTEEL_BLOCK);
        blockWithItem(MFUBlocks.RAW_GLOOMSTEEL_BLOCK);

        blockWithItem(MFUBlocks.GLIMMERSTEEL_BLOCK);
        blockWithItem(MFUBlocks.RAW_GLIMMERSTEEL_BLOCK);

        blockWithItem(MFUBlocks.GLOOMSTEEL_STONE_ORE);
        blockWithItem(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE);
        blockWithItem(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE);

        blockWithItem(MFUBlocks.GLIMMERSTEEL_STONE_ORE);
        blockWithItem(MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE);
        blockWithItem(MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE);

        blockWithItem(MFUBlocks.GLIMMER_LAMP);
        blockWithItem(MFUBlocks.DARK_GLASS);

        logBlock((RotatedPillarBlock) MFUBlocks.GLOOMWOOD_LOG.get());
        logBlock((RotatedPillarBlock) MFUBlocks.STRIPPED_GLOOMWOOD_LOG.get());
        axisBlock((RotatedPillarBlock)MFUBlocks.GLOOMWOOD.get(), blockTexture(MFUBlocks.GLOOMWOOD_LOG.get()), blockTexture(MFUBlocks.GLOOMWOOD_LOG.get()));
        axisBlock((RotatedPillarBlock)MFUBlocks.STRIPPED_GLOOMWOOD.get(), blockTexture(MFUBlocks.STRIPPED_GLOOMWOOD_LOG.get()), blockTexture(MFUBlocks.STRIPPED_GLOOMWOOD_LOG.get()));

        logBlock((RotatedPillarBlock) MFUBlocks.GLIMMERWOOD_LOG.get());
        logBlock((RotatedPillarBlock) MFUBlocks.STRIPPED_GLIMMERWOOD_LOG.get());
        axisBlock((RotatedPillarBlock)MFUBlocks.GLIMMERWOOD.get(), blockTexture(MFUBlocks.GLIMMERWOOD_LOG.get()), blockTexture(MFUBlocks.GLIMMERWOOD_LOG.get()));
        axisBlock((RotatedPillarBlock)MFUBlocks.STRIPPED_GLIMMERWOOD.get(), blockTexture(MFUBlocks.STRIPPED_GLIMMERWOOD_LOG.get()), blockTexture(MFUBlocks.STRIPPED_GLIMMERWOOD_LOG.get()));

        blockItem(MFUBlocks.GLOOMWOOD_LOG);
        blockItem(MFUBlocks.STRIPPED_GLOOMWOOD_LOG);
        blockItem(MFUBlocks.GLOOMWOOD);
        blockItem(MFUBlocks.STRIPPED_GLOOMWOOD);

        blockItem(MFUBlocks.GLIMMERWOOD_LOG);
        blockItem(MFUBlocks.STRIPPED_GLIMMERWOOD_LOG);
        blockItem(MFUBlocks.GLIMMERWOOD);
        blockItem(MFUBlocks.STRIPPED_GLIMMERWOOD);

        blockWithItem(MFUBlocks.GLOOMWOOD_PLANKS);
        blockWithItem(MFUBlocks.GLIMMERWOOD_PLANKS);

        leavesBlock(MFUBlocks.GLOOMWOOD_LEAVES);
        leavesBlock(MFUBlocks.GLIMMERWOOD_LEAVES);

        saplingBlock(MFUBlocks.GLOOMWOOD_SAPLING);
        saplingBlock(MFUBlocks.GLIMMERWOOD_SAPLING);

        stairsBlock(MFUBlocks.GLOOMWOOD_STAIRS.get(), blockTexture(MFUBlocks.GLOOMWOOD_PLANKS.get()));
        slabBlock(MFUBlocks.GLOOMWOOD_SLAB.get(), blockTexture(MFUBlocks.GLOOMWOOD_PLANKS.get()), blockTexture(MFUBlocks.GLOOMWOOD_PLANKS.get()));
        buttonBlock(MFUBlocks.GLOOMWOOD_BUTTON.get(), blockTexture(MFUBlocks.GLOOMWOOD_PLANKS.get()));
        pressurePlateBlock(MFUBlocks.GLOOMWOOD_PRESSURE_PLATE.get(), blockTexture(MFUBlocks.GLOOMWOOD_PLANKS.get()));
        fenceBlock(MFUBlocks.GLOOMWOOD_FENCE.get(), blockTexture(MFUBlocks.GLOOMWOOD_PLANKS.get()));
        fenceGateBlock(MFUBlocks.GLOOMWOOD_FENCE_GATE.get(), blockTexture(MFUBlocks.GLOOMWOOD_PLANKS.get()));
        doorBlockWithRenderType(MFUBlocks.GLOOMWOOD_DOOR.get(), modLoc("block/gloomwood_door_bottom"), modLoc("block/gloomwood_door_top"), "cutout");
        trapdoorBlockWithRenderType(MFUBlocks.GLOOMWOOD_TRAPDOOR.get(), modLoc("block/gloomwood_trapdoor"), true, "cutout");
        blockItem(MFUBlocks.GLOOMWOOD_STAIRS);
        blockItem(MFUBlocks.GLOOMWOOD_SLAB);
        blockItem(MFUBlocks.GLOOMWOOD_PRESSURE_PLATE);
        blockItem(MFUBlocks.GLOOMWOOD_FENCE_GATE);
        blockItem(MFUBlocks.GLOOMWOOD_TRAPDOOR, "_bottom");

        stairsBlock(MFUBlocks.GLIMMERWOOD_STAIRS.get(), blockTexture(MFUBlocks.GLIMMERWOOD_PLANKS.get()));
        slabBlock(MFUBlocks.GLIMMERWOOD_SLAB.get(), blockTexture(MFUBlocks.GLIMMERWOOD_PLANKS.get()), blockTexture(MFUBlocks.GLIMMERWOOD_PLANKS.get()));
        buttonBlock(MFUBlocks.GLIMMERWOOD_BUTTON.get(), blockTexture(MFUBlocks.GLIMMERWOOD_PLANKS.get()));
        pressurePlateBlock(MFUBlocks.GLIMMERWOOD_PRESSURE_PLATE.get(), blockTexture(MFUBlocks.GLIMMERWOOD_PLANKS.get()));
        fenceBlock(MFUBlocks.GLIMMERWOOD_FENCE.get(), blockTexture(MFUBlocks.GLIMMERWOOD_PLANKS.get()));
        fenceGateBlock(MFUBlocks.GLIMMERWOOD_FENCE_GATE.get(), blockTexture(MFUBlocks.GLIMMERWOOD_PLANKS.get()));
        doorBlockWithRenderType(MFUBlocks.GLIMMERWOOD_DOOR.get(), modLoc("block/glimmerwood_door_bottom"), modLoc("block/glimmerwood_door_top"), "cutout");
        trapdoorBlockWithRenderType(MFUBlocks.GLIMMERWOOD_TRAPDOOR.get(), modLoc("block/glimmerwood_trapdoor"), true, "cutout");
        blockItem(MFUBlocks.GLIMMERWOOD_STAIRS);
        blockItem(MFUBlocks.GLIMMERWOOD_SLAB);
        blockItem(MFUBlocks.GLIMMERWOOD_PRESSURE_PLATE);
        blockItem(MFUBlocks.GLIMMERWOOD_FENCE_GATE);
        blockItem(MFUBlocks.GLIMMERWOOD_TRAPDOOR, "_bottom");
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("mobflowutilities:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("mobflowutilities:block/" + deferredBlock.getId().getPath() + appendix));
    }

    private void leavesBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), ResourceLocation.parse("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void saplingBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }
}