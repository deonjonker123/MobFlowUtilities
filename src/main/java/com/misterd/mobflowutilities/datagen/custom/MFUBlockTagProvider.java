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
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(MFUBlocks.DAMAGE_PAD.get())
                .add(MFUBlocks.FAST_FLOW_PAD.get())
                .add(MFUBlocks.FASTER_FLOW_PAD.get())
                .add(MFUBlocks.FASTEST_FLOW_PAD.get())

                .add(MFUBlocks.CONTROLLER.get())
                .add(MFUBlocks.COLLECTOR.get())
                .add(MFUBlocks.GENESIS_CHAMBER.get())
                .add(MFUBlocks.FAN.get())

                .add(MFUBlocks.DARK_GLASS.get())
                .add(MFUBlocks.GLIMMER_LAMP.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(MFUBlocks.DARK_DIRT.get())
                .add(MFUBlocks.GLIMMER_GRASS.get());

        tag(MFUTags.Blocks.MOBFLOWUTILITIES_PADS)
                .add(MFUBlocks.DAMAGE_PAD.get())
                .add(MFUBlocks.FAST_FLOW_PAD.get())
                .add(MFUBlocks.FASTER_FLOW_PAD.get())
                .add(MFUBlocks.FASTEST_FLOW_PAD.get());

        tag(MFUTags.Blocks.MOBFLOWUTILITIES_MACHINES)
                .add(MFUBlocks.CONTROLLER.get())
                .add(MFUBlocks.COLLECTOR.get())
                .add(MFUBlocks.GENESIS_CHAMBER.get())
                .add(MFUBlocks.FAN.get());

        tag(BlockTags.WITHER_IMMUNE)
                .add(MFUBlocks.DARK_GLASS.get());
    }
}