package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MFUBlockStateProvider extends BlockStateProvider {

    public MFUBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MobFlowUtilities.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Gloomsteel blocks
        blockWithItem(MFUBlocks.GLOOMSTEEL_BLOCK);
        blockWithItem(MFUBlocks.RAW_GLOOMSTEEL_BLOCK);

        // Gloomsteel ores
        blockWithItem(MFUBlocks.GLOOMSTEEL_STONE_ORE);
        blockWithItem(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE);
        blockWithItem(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE);
        blockWithItem(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE);

        // Decorative blocks
        blockWithItem(MFUBlocks.GLIMMER_LAMP);
        blockWithItem(MFUBlocks.DARK_GLASS);
    }

    /**
     * Generates a simple block model and item model for the given block.
     * Uses the cube_all model with the block's texture.
     */
    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}