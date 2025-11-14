package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MFUBlockTagProvider extends BlockTagsProvider {

    public MFUBlockTagProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MobFlowUtilities.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Blocks mineable with pickaxe
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(MFUBlocks.DAMAGE_PAD.get())
                .add(MFUBlocks.FAST_FLOW_PAD.get())
                .add(MFUBlocks.FASTER_FLOW_PAD.get())
                .add(MFUBlocks.FASTEST_FLOW_PAD.get())
                .add(MFUBlocks.CONTROLLER.get())
                .add(MFUBlocks.COLLECTOR.get())
                .add(MFUBlocks.GLOOMSTEEL_BLOCK.get())
                .add(MFUBlocks.GLOOMSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get())
                .add(MFUBlocks.RAW_GLOOMSTEEL_BLOCK.get())
                .add(MFUBlocks.GLIMMER_LAMP.get())
                .add(MFUBlocks.DARK_DIRT.get())
                .add(MFUBlocks.GLIMMER_GRASS.get());

        // Gloomsteel ores require iron tool or better
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(MFUBlocks.GLOOMSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get());

        // Mod-specific tag: All pads
        tag(MFUTags.Blocks.MOBFLOWUTILITIES_PADS)
                .add(MFUBlocks.DAMAGE_PAD.get())
                .add(MFUBlocks.FAST_FLOW_PAD.get())
                .add(MFUBlocks.FASTER_FLOW_PAD.get())
                .add(MFUBlocks.FASTEST_FLOW_PAD.get());

        // Mod-specific tag: Gloomsteel ore blocks
        tag(MFUTags.Blocks.ORE_BLOCKS_GLOOMSTEEL)
                .add(MFUBlocks.GLOOMSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get());

        // Mod-specific tag: MFU machines
        tag(MFUTags.Blocks.MOBFLOWUTILITIES_MACHINES)
                .add(MFUBlocks.CONTROLLER.get())
                .add(MFUBlocks.COLLECTOR.get());

        // Mod-specific tag: Raw Gloomsteel blocks
        tag(MFUTags.Blocks.RAW_BLOCKS_GLOOMSTEEL)
                .add(MFUBlocks.RAW_GLOOMSTEEL_BLOCK.get());

        // Gloomsteel tools can mine everything diamond tools can
        tag(MFUTags.Blocks.NEEDS_GLOOMSTEEL_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL);

        // Define what Gloomsteel tools cannot mine
        tag(MFUTags.Blocks.INCORRECT_FOR_GLOOMSTEEL_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                .remove(MFUTags.Blocks.NEEDS_GLOOMSTEEL_TOOL);
    }
}