package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MFUBlockTagProvider extends BlockTagsProvider {

    public MFUBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MobFlowUtilities.MODID);
    }

    private static ResourceKey<Block> key(Block block) {
        return block.builtInRegistryHolder().key();
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(key(MFUBlocks.DAMAGE_PAD.get()))
                .add(key(MFUBlocks.FAST_FLOW_PAD.get()))
                .add(key(MFUBlocks.FASTER_FLOW_PAD.get()))
                .add(key(MFUBlocks.FASTEST_FLOW_PAD.get()))

                .add(key(MFUBlocks.CONTROLLER.get()))
                .add(key(MFUBlocks.COLLECTOR.get()))
                .add(key(MFUBlocks.GENESIS_CHAMBER.get()))
                .add(key(MFUBlocks.GENESIS_INFUSER.get()))
                .add(key(MFUBlocks.FAN.get()))

                .add(key(MFUBlocks.DARK_GLASS.get()))
                .add(key(MFUBlocks.GLIMMER_LAMP.get()))
                .add(key(MFUBlocks.GIGATANK.get()));

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(key(MFUBlocks.DARK_DIRT.get()))
                .add(key(MFUBlocks.GLIMMER_GRASS.get()));

        tag(MFUTags.Blocks.MOBFLOWUTILITIES_PADS)
                .add(key(MFUBlocks.DAMAGE_PAD.get()))
                .add(key(MFUBlocks.FAST_FLOW_PAD.get()))
                .add(key(MFUBlocks.FASTER_FLOW_PAD.get()))
                .add(key(MFUBlocks.FASTEST_FLOW_PAD.get()));

        tag(MFUTags.Blocks.MOBFLOWUTILITIES_MACHINES)
                .add(key(MFUBlocks.CONTROLLER.get()))
                .add(key(MFUBlocks.COLLECTOR.get()))
                .add(key(MFUBlocks.GENESIS_CHAMBER.get()))
                .add(key(MFUBlocks.FAN.get()));

        tag(BlockTags.WITHER_IMMUNE)
                .add(key(MFUBlocks.DARK_GLASS.get()));
    }
}