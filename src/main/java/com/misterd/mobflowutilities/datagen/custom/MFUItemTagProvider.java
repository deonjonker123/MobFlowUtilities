package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MFUItemTagProvider extends ItemTagsProvider {

    public MFUItemTagProvider(PackOutput output,
                              CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                              @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, "mobflowutilities", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Items.ORES)
                .add((MFUBlocks.GLOOMSTEEL_STONE_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get()).asItem())

                .add((MFUBlocks.GLIMMERSTEEL_STONE_ORE.get()).asItem())
                .add((MFUBlocks.GLIMMERSTEEL_DEEPSLATE_ORE.get()).asItem())
                .add((MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE.get()).asItem())
                .add((MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE.get()).asItem());

        tag(Tags.Items.INGOTS)
                .add(MFUItems.GLOOMSTEEL_INGOT.get())
                .add(MFUItems.GLIMMERSTEEL_INGOT.get());

        tag(Tags.Items.RAW_MATERIALS)
                .add(MFUItems.RAW_GLOOMSTEEL.get())
                .add(MFUItems.RAW_GLIMMERSTEEL.get());

        tag(MFUTags.Items.ORES_MFU)
                .add((MFUBlocks.GLOOMSTEEL_STONE_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get()).asItem())

                .add((MFUBlocks.GLIMMERSTEEL_STONE_ORE.get()).asItem())
                .add((MFUBlocks.GLIMMERSTEEL_DEEPSLATE_ORE.get()).asItem())
                .add((MFUBlocks.GLIMMERSTEEL_NETHERRACK_ORE.get()).asItem())
                .add((MFUBlocks.GLIMMERSTEEL_ENDSTONE_ORE.get()).asItem());

        tag(MFUTags.Items.INGOTS_MFU)
                .add(MFUItems.GLOOMSTEEL_INGOT.get())
                .add(MFUItems.GLIMMERSTEEL_INGOT.get());

        tag(MFUTags.Items.NUGGETS_MFU)
                .add(MFUItems.GLOOMSTEEL_NUGGET.get())
                .add(MFUItems.GLIMMERSTEEL_NUGGET.get());

        tag(MFUTags.Items.RAW_MATERIALS_MFU)
                .add(MFUItems.RAW_GLOOMSTEEL.get())
                .add(MFUItems.RAW_GLIMMERSTEEL.get());

        tag(MFUTags.Items.MOBFLOWUTILITIES_MODULES)
                .add(MFUItems.BOA_MODULE.get())
                .add(MFUItems.FIRE_ASPECT_MODULE.get())
                .add(MFUItems.SHARPNESS_MODULE.get())
                .add(MFUItems.SMITE_MODULE.get())
                .add(MFUItems.LOOTING_MODULE.get())
                .add(MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get())
                .add(MFUItems.VOID_FILTER_MODULE.get());

        tag(MFUTags.Items.GLOOMWOOD_LOGS)
                .add(MFUBlocks.GLOOMWOOD_LOG.asItem())
                .add(MFUBlocks.STRIPPED_GLOOMWOOD_LOG.asItem())
                .add(MFUBlocks.GLOOMWOOD.asItem())
                .add(MFUBlocks.STRIPPED_GLOOMWOOD.asItem());

        tag(MFUTags.Items.GLIMMERWOOD_LOGS)
                .add(MFUBlocks.GLIMMERWOOD_LOG.asItem())
                .add(MFUBlocks.STRIPPED_GLIMMERWOOD_LOG.asItem())
                .add(MFUBlocks.GLIMMERWOOD.asItem())
                .add(MFUBlocks.STRIPPED_GLIMMERWOOD.asItem());

        tag(MFUTags.Items.GLOOMWOOD_LOGS_FOR_WOOD)
                .add(MFUBlocks.GLOOMWOOD_LOG.asItem());

        tag(MFUTags.Items.GLIMMERWOOD_LOGS_FOR_WOOD)
                .add(MFUBlocks.GLIMMERWOOD_LOG.asItem());

        tag(MFUTags.Items.MOBFLOWUTILITIES_TOOLS)
                .add(MFUItems.PAD_WRENCH.get())
                .add(MFUItems.MOB_CATCHER.get());

        tag(ItemTags.SWORDS)
                .add(MFUItems.GLOOMSTEEL_SWORD.get())
                .add(MFUItems.GLIMMERSTEEL_SWORD.get());

        tag(ItemTags.PICKAXES)
                .add(MFUItems.GLOOMSTEEL_PICKAXE.get())
                .add(MFUItems.GLOOMSTEEL_PAXEL.get())

                .add(MFUItems.GLIMMERSTEEL_PICKAXE.get())
                .add(MFUItems.GLIMMERSTEEL_PAXEL.get());

        tag(ItemTags.AXES)
                .add(MFUItems.GLOOMSTEEL_AXE.get())
                .add(MFUItems.GLOOMSTEEL_PAXEL.get())

                .add(MFUItems.GLIMMERSTEEL_AXE.get())
                .add(MFUItems.GLIMMERSTEEL_PAXEL.get());

        tag(ItemTags.SHOVELS)
                .add(MFUItems.GLOOMSTEEL_SHOVEL.get())
                .add(MFUItems.GLOOMSTEEL_PAXEL.get())

                .add(MFUItems.GLIMMERSTEEL_SHOVEL.get())
                .add(MFUItems.GLIMMERSTEEL_PAXEL.get());

        tag(ItemTags.HOES)
                .add(MFUItems.GLOOMSTEEL_HOE.get())
                .add(MFUItems.GLIMMERSTEEL_HOE.get());

        tag(ItemTags.FOX_FOOD)
                .add(MFUItems.UMBRAL_BERRIES.get())
                .add(MFUItems.RADIANT_BERRIES.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(MFUItems.GLIMMERSTEEL_HELMET.get())
                .add(MFUItems.GLIMMERSTEEL_CHESTPLATE.get())
                .add(MFUItems.GLIMMERSTEEL_LEGGINGS.get())
                .add(MFUItems.GLIMMERSTEEL_BOOTS.get())

                .add(MFUItems.GLOOMSTEEL_HELMET.get())
                .add(MFUItems.GLOOMSTEEL_CHESTPLATE.get())
                .add(MFUItems.GLOOMSTEEL_LEGGINGS.get())
                .add(MFUItems.GLOOMSTEEL_BOOTS.get());

        tag(ItemTags.LOGS_THAT_BURN)
                .add(MFUBlocks.GLOOMWOOD_LOG.asItem())
                .add(MFUBlocks.STRIPPED_GLOOMWOOD_LOG.asItem())
                .add(MFUBlocks.GLOOMWOOD.asItem())
                .add(MFUBlocks.STRIPPED_GLOOMWOOD.asItem())

                .add(MFUBlocks.GLIMMERWOOD_LOG.asItem())
                .add(MFUBlocks.STRIPPED_GLIMMERWOOD_LOG.asItem())
                .add(MFUBlocks.GLIMMERWOOD.asItem())
                .add(MFUBlocks.STRIPPED_GLIMMERWOOD.asItem());

        tag(ItemTags.PLANKS)
                .add(MFUBlocks.GLOOMWOOD_PLANKS.asItem())
                .add(MFUBlocks.GLIMMERWOOD_PLANKS.asItem());

        tag(ItemTags.WOODEN_STAIRS)
                .add(MFUBlocks.GLIMMERWOOD_STAIRS.asItem())
                .add(MFUBlocks.GLOOMWOOD_STAIRS.asItem());

        tag(ItemTags.WOODEN_SLABS)
                .add(MFUBlocks.GLOOMWOOD_SLAB.asItem())
                .add(MFUBlocks.GLIMMERWOOD_SLAB.asItem());

        tag(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(MFUBlocks.GLOOMWOOD_PRESSURE_PLATE.asItem())
                .add(MFUBlocks.GLIMMERWOOD_PRESSURE_PLATE.asItem());

        tag(ItemTags.WOODEN_BUTTONS)
                .add(MFUBlocks.GLOOMWOOD_BUTTON.asItem())
                .add(MFUBlocks.GLIMMERWOOD_BUTTON.asItem());

        tag(ItemTags.WOODEN_FENCES)
                .add(MFUBlocks.GLOOMWOOD_FENCE.asItem())
                .add(MFUBlocks.GLOOMWOOD_FENCE_GATE.asItem())
                .add(MFUBlocks.GLIMMERWOOD_FENCE.asItem())
                .add(MFUBlocks.GLIMMERWOOD_FENCE_GATE.asItem());

        tag(ItemTags.WOODEN_DOORS)
                .add(MFUBlocks.GLOOMWOOD_DOOR.asItem())
                .add(MFUBlocks.GLIMMERWOOD_DOOR.asItem());

        tag(ItemTags.WOODEN_TRAPDOORS)
                .add(MFUBlocks.GLOOMWOOD_TRAPDOOR.asItem())
                .add(MFUBlocks.GLIMMERWOOD_TRAPDOOR.asItem());

        tag(MFUTags.Items.GENESIS_CHAMBER_FUELS)
                .add(Items.COAL)
                .add(Items.CHARCOAL)
                .add(Items.COAL_BLOCK)
                .add(Items.BLAZE_ROD)
                .add(Items.LAVA_BUCKET);
    }
}