package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.Set;

public class MFULootTableProvider extends BlockLootSubProvider {

    public MFULootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf( MFUBlocks.CONTROLLER.get());
        dropSelf( MFUBlocks.GENESIS_CHAMBER.get());
        dropSelf( MFUBlocks.FAN.get());

        dropSelf( MFUBlocks.FAST_FLOW_PAD.get());
        dropSelf( MFUBlocks.FASTER_FLOW_PAD.get());
        dropSelf( MFUBlocks.FASTEST_FLOW_PAD.get());
        dropSelf( MFUBlocks.DAMAGE_PAD.get());

        dropSelf( MFUBlocks.DARK_GLASS.get());
        dropSelf( MFUBlocks.GLIMMER_LAMP.get());

        add(MFUBlocks.DARK_DIRT.get(),
                block -> createSilkTouchDispatchTable(
                        MFUBlocks.DARK_DIRT.get(),
                        applyExplosionDecay(block, LootItem.lootTableItem(Items.DIRT))
                ));

        add(MFUBlocks.GLIMMER_GRASS.get(),
                block -> createSilkTouchDispatchTable(
                        MFUBlocks.GLIMMER_GRASS.get(),
                        applyExplosionDecay(block, LootItem.lootTableItem(Items.DIRT))
                ));

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return MFUBlocks.BLOCKS.getEntries()
                .stream()
                .map(Holder::value)
                .toList();
    }
}