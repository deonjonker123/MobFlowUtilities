package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
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

        tag(MFUTags.Items.GENESIS_CHAMBER_FUELS)
                .add(Items.COAL)
                .add(Items.CHARCOAL)
                .add(Items.COAL_BLOCK)
                .add(Items.BLAZE_ROD)
                .add(Items.LAVA_BUCKET);
    }
}