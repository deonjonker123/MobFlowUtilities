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
        dropSelf( MFUBlocks.GLOOMSTEEL_BLOCK.get());
        dropSelf( MFUBlocks.RAW_GLOOMSTEEL_BLOCK.get());

        dropSelf( MFUBlocks.GLIMMERSTEEL_BLOCK.get());
        dropSelf( MFUBlocks.RAW_GLIMMERSTEEL_BLOCK.get());

        dropSelf( MFUBlocks.CONTROLLER.get());
        dropSelf( MFUBlocks.FAST_FLOW_PAD.get());
        dropSelf( MFUBlocks.FASTER_FLOW_PAD.get());
        dropSelf( MFUBlocks.FASTEST_FLOW_PAD.get());
        dropSelf( MFUBlocks.DAMAGE_PAD.get());

        dropSelf( MFUBlocks.DARK_GLASS.get());
        dropSelf( MFUBlocks.GLIMMER_LAMP.get());

        dropSelf( MFUBlocks.GLIMMERWOOD.get());
        dropSelf( MFUBlocks.GLIMMERWOOD_LOG.get());
        dropSelf( MFUBlocks.STRIPPED_GLIMMERWOOD.get());
        dropSelf( MFUBlocks.STRIPPED_GLIMMERWOOD_LOG.get());
        dropSelf( MFUBlocks.GLIMMERWOOD_PLANKS.get());
        dropSelf( MFUBlocks.GLIMMERWOOD_SAPLING.get());

        dropSelf( MFUBlocks.GLOOMWOOD.get());
        dropSelf( MFUBlocks.GLOOMWOOD_LOG.get());
        dropSelf( MFUBlocks.STRIPPED_GLOOMWOOD.get());
        dropSelf( MFUBlocks.STRIPPED_GLOOMWOOD_LOG.get());
        dropSelf( MFUBlocks.GLOOMWOOD_PLANKS.get());
        dropSelf( MFUBlocks.GLOOMWOOD_SAPLING.get());

        dropSelf(MFUBlocks.GLOOMWOOD_STAIRS.get());
        add (MFUBlocks.GLOOMWOOD_SLAB.get(), block -> createSlabItemTable(MFUBlocks.GLOOMWOOD_SLAB.get()));
        dropSelf(MFUBlocks.GLOOMWOOD_PRESSURE_PLATE.get());
        dropSelf(MFUBlocks.GLOOMWOOD_BUTTON.get());
        dropSelf(MFUBlocks.GLOOMWOOD_FENCE.get());
        dropSelf(MFUBlocks.GLOOMWOOD_FENCE_GATE.get());
        dropSelf(MFUBlocks.GLOOMWOOD_TRAPDOOR.get());
        add (MFUBlocks.GLOOMWOOD_DOOR.get(), block -> createDoorTable(MFUBlocks.GLOOMWOOD_DOOR.get()));

        dropSelf(MFUBlocks.GLIMMERWOOD_STAIRS.get());
        add (MFUBlocks.GLIMMERWOOD_SLAB.get(), block -> createSlabItemTable(MFUBlocks.GLIMMERWOOD_SLAB.get()));
        dropSelf(MFUBlocks.GLIMMERWOOD_PRESSURE_PLATE.get());
        dropSelf(MFUBlocks.GLIMMERWOOD_BUTTON.get());
        dropSelf(MFUBlocks.GLIMMERWOOD_FENCE.get());
        dropSelf(MFUBlocks.GLIMMERWOOD_FENCE_GATE.get());
        dropSelf(MFUBlocks.GLIMMERWOOD_TRAPDOOR.get());
        add (MFUBlocks.GLIMMERWOOD_DOOR.get(), block -> createDoorTable(MFUBlocks.GLIMMERWOOD_DOOR.get()));

        this.add(MFUBlocks.GLIMMERWOOD_LEAVES.get(), block -> createLeavesDrops(block, MFUBlocks.GLIMMERWOOD_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        this.add(MFUBlocks.GLOOMWOOD_LEAVES.get(), block -> createLeavesDrops(block, MFUBlocks.GLOOMWOOD_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

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

        add(MFUBlocks.GLIMMERSTEEL_STONE_ORE.get(),
                block -> createOreDrop( MFUBlocks.GLIMMERSTEEL_STONE_ORE.get(),
                        MFUItems.RAW_GLIMMERSTEEL.get()));

        add(MFUBlocks.GLIMMERSTEEL_DEEPSLATE_ORE.get(),
                block -> createOreDrop( MFUBlocks.GLIMMERSTEEL_DEEPSLATE_ORE.get(),
                        MFUItems.RAW_GLIMMERSTEEL.get()));

        add(MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE.get(),
                block -> createOreDrop( MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE.get(),
                        MFUItems.RAW_GLIMMERSTEEL.get()));

        add(MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE.get(),
                block -> createOreDrop( MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE.get(),
                        MFUItems.RAW_GLIMMERSTEEL.get()));

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