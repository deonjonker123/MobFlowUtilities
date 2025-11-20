package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
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
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(MFUBlocks.DAMAGE_PAD.get())
                .add(MFUBlocks.FAST_FLOW_PAD.get())
                .add(MFUBlocks.FASTER_FLOW_PAD.get())
                .add(MFUBlocks.FASTEST_FLOW_PAD.get())

                .add(MFUBlocks.CONTROLLER.get())
                .add(MFUBlocks.COLLECTOR.get())
                .add(MFUBlocks.GENESIS_CHAMBER.get())

                .add(MFUBlocks.GLOOMSTEEL_BLOCK.get())
                .add(MFUBlocks.GLOOMSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get())
                .add(MFUBlocks.RAW_GLOOMSTEEL_BLOCK.get())

                .add(MFUBlocks.GLIMMERSTEEL_BLOCK.get())
                .add(MFUBlocks.GLIMMERSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE.get())
                .add(MFUBlocks.RAW_GLIMMERSTEEL_BLOCK.get())

                .add(MFUBlocks.DARK_GLASS.get())
                .add(MFUBlocks.GLIMMER_LAMP.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(MFUBlocks.GLOOMWOOD_PLANKS.get())
                .add(MFUBlocks.GLIMMERWOOD_PLANKS.get())
                .add(MFUBlocks.GLOOMWOOD_STAIRS.get())
                .add(MFUBlocks.GLOOMWOOD_SLAB.get())
                .add(MFUBlocks.GLOOMWOOD_BUTTON.get())
                .add(MFUBlocks.GLOOMWOOD_PRESSURE_PLATE.get())
                .add(MFUBlocks.GLOOMWOOD_FENCE.get())
                .add(MFUBlocks.GLOOMWOOD_FENCE_GATE.get())
                .add(MFUBlocks.GLOOMWOOD_DOOR.get())
                .add(MFUBlocks.GLOOMWOOD_TRAPDOOR.get())

                .add(MFUBlocks.GLIMMERWOOD_STAIRS.get())
                .add(MFUBlocks.GLIMMERWOOD_SLAB.get())
                .add(MFUBlocks.GLIMMERWOOD_BUTTON.get())
                .add(MFUBlocks.GLIMMERWOOD_PRESSURE_PLATE.get())
                .add(MFUBlocks.GLIMMERWOOD_FENCE.get())
                .add(MFUBlocks.GLIMMERWOOD_FENCE_GATE.get())
                .add(MFUBlocks.GLIMMERWOOD_DOOR.get())
                .add(MFUBlocks.GLIMMERWOOD_TRAPDOOR.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(MFUBlocks.DARK_DIRT.get())
                .add(MFUBlocks.GLIMMER_GRASS.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(MFUBlocks.GLOOMSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get())

                .add(MFUBlocks.GLIMMERSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE.get());

        tag(MFUTags.Blocks.MOBFLOWUTILITIES_PADS)
                .add(MFUBlocks.DAMAGE_PAD.get())
                .add(MFUBlocks.FAST_FLOW_PAD.get())
                .add(MFUBlocks.FASTER_FLOW_PAD.get())
                .add(MFUBlocks.FASTEST_FLOW_PAD.get());

        tag(MFUTags.Blocks.ORE_BLOCKS_MFU)
                .add(MFUBlocks.GLOOMSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get())

                .add(MFUBlocks.GLIMMERSTEEL_STONE_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_DEEPSLATE_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE.get())
                .add(MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE.get());

        tag(MFUTags.Blocks.MOBFLOWUTILITIES_MACHINES)
                .add(MFUBlocks.CONTROLLER.get())
                .add(MFUBlocks.COLLECTOR.get())
                .add(MFUBlocks.GENESIS_CHAMBER.get());

        tag(MFUTags.Blocks.RAW_BLOCKS_MFU)
                .add(MFUBlocks.RAW_GLOOMSTEEL_BLOCK.get())
                .add(MFUBlocks.RAW_GLIMMERSTEEL_BLOCK.get());

        tag(MFUTags.Blocks.NEEDS_GLOOMSTEEL_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL);

        tag(MFUTags.Blocks.NEEDS_GLIMMERSTEEL_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL);

        tag(MFUTags.Blocks.INCORRECT_FOR_GLOOMSTEEL_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                .remove(MFUTags.Blocks.NEEDS_GLOOMSTEEL_TOOL);

        tag(MFUTags.Blocks.INCORRECT_FOR_GLIMMERSTEEL_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                .remove(MFUTags.Blocks.NEEDS_GLOOMSTEEL_TOOL)
                .remove(MFUTags.Blocks.NEEDS_GLIMMERSTEEL_TOOL);

        tag(BlockTags.LOGS_THAT_BURN)
                .add(MFUBlocks.GLOOMWOOD_LOG.get())
                .add(MFUBlocks.STRIPPED_GLOOMWOOD_LOG.get())
                .add(MFUBlocks.GLOOMWOOD.get())
                .add(MFUBlocks.STRIPPED_GLOOMWOOD.get())

                .add(MFUBlocks.GLIMMERWOOD_LOG.get())
                .add(MFUBlocks.STRIPPED_GLIMMERWOOD_LOG.get())
                .add(MFUBlocks.GLIMMERWOOD.get())
                .add(MFUBlocks.STRIPPED_GLIMMERWOOD.get());

        tag(BlockTags.WOODEN_STAIRS)
                .add(MFUBlocks.GLIMMERWOOD_STAIRS.get())
                .add(MFUBlocks.GLOOMWOOD_STAIRS.get());

        tag(BlockTags.WOODEN_SLABS)
                .add(MFUBlocks.GLOOMWOOD_SLAB.get())
                .add(MFUBlocks.GLIMMERWOOD_SLAB.get());

        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(MFUBlocks.GLOOMWOOD_PRESSURE_PLATE.get())
                .add(MFUBlocks.GLIMMERWOOD_PRESSURE_PLATE.get());

        tag(BlockTags.WOODEN_BUTTONS)
                .add(MFUBlocks.GLOOMWOOD_BUTTON.get())
                .add(MFUBlocks.GLIMMERWOOD_BUTTON.get());

        tag(BlockTags.WOODEN_FENCES)
                .add(MFUBlocks.GLOOMWOOD_FENCE.get())
                .add(MFUBlocks.GLOOMWOOD_FENCE_GATE.get())
                .add(MFUBlocks.GLIMMERWOOD_FENCE.get())
                .add(MFUBlocks.GLIMMERWOOD_FENCE_GATE.get());

        tag(BlockTags.WOODEN_DOORS)
                .add(MFUBlocks.GLOOMWOOD_DOOR.get())
                .add(MFUBlocks.GLIMMERWOOD_DOOR.get());

        tag(BlockTags.WOODEN_TRAPDOORS)
                .add(MFUBlocks.GLOOMWOOD_TRAPDOOR.get())
                .add(MFUBlocks.GLIMMERWOOD_TRAPDOOR.get());
    }
}