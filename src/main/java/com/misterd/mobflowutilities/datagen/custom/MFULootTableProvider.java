package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class MFULootTableProvider extends BlockLootSubProvider {

    public MFULootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // Standard block drops (drop themselves)
        dropSelf( MFUBlocks.GLOOMSTEEL_BLOCK.get());
        dropSelf( MFUBlocks.RAW_GLOOMSTEEL_BLOCK.get());
        dropSelf( MFUBlocks.CONTROLLER.get());
        dropSelf( MFUBlocks.FAST_FLOW_PAD.get());
        dropSelf( MFUBlocks.FASTER_FLOW_PAD.get());
        dropSelf( MFUBlocks.FASTEST_FLOW_PAD.get());
        dropSelf( MFUBlocks.DAMAGE_PAD.get());
        dropSelf( MFUBlocks.DARK_GLASS.get());
        dropSelf( MFUBlocks.GLIMMER_LAMP.get());

        // Ore drops (drop raw gloomsteel)
        add(MFUBlocks.GLOOMSTEEL_STONE_ORE.get(),
                block -> createOreDrop( MFUBlocks.GLOOMSTEEL_STONE_ORE.get(),
                        MFUItems.RAW_GLOOMSTEEL.get()));

        add(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get(),
                block -> createOreDrop( MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get(),
                        MFUItems.RAW_GLOOMSTEEL.get()));

        add(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get(),
                block -> createOreDrop( MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get(),
                        MFUItems.RAW_GLOOMSTEEL.get()));

        add(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get(),
                block -> createOreDrop( MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get(),
                        MFUItems.RAW_GLOOMSTEEL.get()));

        // Special spawn blocks (drop dirt when broken)
        add(MFUBlocks.DARK_DIRT.get(),
                block -> LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(Items.DIRT))));

        add(MFUBlocks.GLIMMER_GRASS.get(),
                block -> LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(Items.DIRT))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return MFUBlocks.BLOCKS.getEntries()
                .stream()
                .map(Holder::value)
                .toList();
    }
}