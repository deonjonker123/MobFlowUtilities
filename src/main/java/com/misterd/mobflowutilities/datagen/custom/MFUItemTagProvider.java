package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
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
                .add((MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get()).asItem());

        tag(Tags.Items.INGOTS)
                .add(MFUItems.GLOOMSTEEL_INGOT.get());

        tag(Tags.Items.RAW_MATERIALS)
                .add(MFUItems.RAW_GLOOMSTEEL.get());

        tag(MFUTags.Items.ORES_GLOOMSTEEL)
                .add((MFUBlocks.GLOOMSTEEL_STONE_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE.get()).asItem())
                .add((MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE.get()).asItem());

        tag(MFUTags.Items.INGOTS_GLOOMSTEEL)
                .add(MFUItems.GLOOMSTEEL_INGOT.get());

        tag(MFUTags.Items.NUGGETS_GLOOMSTEEL)
                .add(MFUItems.GLOOMSTEEL_NUGGET.get());

        tag(MFUTags.Items.RAW_MATERIALS_GLOOMSTEEL)
                .add(MFUItems.RAW_GLOOMSTEEL.get());

        tag(MFUTags.Items.MOBFLOWUTILITIES_MODULES)
                .add(MFUItems.BOA_MODULE.get())
                .add(MFUItems.FIRE_ASPECT_MODULE.get())
                .add(MFUItems.SHARPNESS_MODULE.get())
                .add(MFUItems.SMITE_MODULE.get())
                .add(MFUItems.LOOTING_MODULE.get())
                .add(MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get())
                .add(MFUItems.VOID_FILTER_MODULE.get());

        tag(MFUTags.Items.MOBFLOWUTILITIES_TOOLS)
                .add(MFUItems.PAD_WRENCH.get())
                .add(MFUItems.MOB_CATCHER.get());

        tag(ItemTags.SWORDS)
                .add(MFUItems.GLOOMSTEEL_SWORD.get());

        tag(ItemTags.PICKAXES)
                .add(MFUItems.GLOOMSTEEL_PICKAXE.get())
                .add(MFUItems.GLOOMSTEEL_PAXEL.get());

        tag(ItemTags.AXES)
                .add(MFUItems.GLOOMSTEEL_AXE.get())
                .add(MFUItems.GLOOMSTEEL_PAXEL.get());

        tag(ItemTags.SHOVELS)
                .add(MFUItems.GLOOMSTEEL_SHOVEL.get())
                .add(MFUItems.GLOOMSTEEL_PAXEL.get());

        tag(ItemTags.HOES)
                .add(MFUItems.GLOOMSTEEL_HOE.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(MFUItems.GLOOMSTEEL_HELMET.get())
                .add(MFUItems.GLOOMSTEEL_CHESTPLATE.get())
                .add(MFUItems.GLOOMSTEEL_LEGGINGS.get())
                .add(MFUItems.GLOOMSTEEL_BOOTS.get());
    }
}