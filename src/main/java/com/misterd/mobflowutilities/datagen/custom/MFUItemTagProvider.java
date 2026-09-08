package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.fluid.MFUFluids;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MFUItemTagProvider extends ItemTagsProvider {

    public MFUItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MobFlowUtilities.MODID);
    }

    private static ResourceKey<Item> key(Item item) {
        return item.builtInRegistryHolder().key();
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MFUTags.Items.MOBFLOWUTILITIES_MODULES)
                .add(key(MFUItems.BOA_MODULE.get()))
                .add(key(MFUItems.FIRE_ASPECT_MODULE.get()))
                .add(key(MFUItems.SHARPNESS_MODULE.get()))
                .add(key(MFUItems.SMITE_MODULE.get()))
                .add(key(MFUItems.LOOTING_MODULE.get()))
                .add(key(MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get()));

        tag(MFUTags.Items.WRENCH)
                .add(key(MFUItems.PAD_WRENCH.get()));

        tag(MFUTags.Items.WRENCHES)
                .add(key(MFUItems.PAD_WRENCH.get()));

        tag(MFUTags.Items.TOOL)
                .add(key(MFUItems.PAD_WRENCH.get()));

        tag(MFUTags.Items.GENESIS_CHAMBER_FUELS)
                .add(key(Items.COAL))
                .add(key(Items.CHARCOAL))
                .add(key(Items.COAL_BLOCK))
                .add(key(Items.BLAZE_ROD))
                .add(key(Items.LAVA_BUCKET))
                .add(key(MFUItems.INFUSED_COAL.get()))
                .add(key(MFUItems.INFUSED_CHARCOAL.get()))
                .addTag(ItemTags.COALS);

        tag(MFUTags.Items.GLIMMER_SPROUT_CRAFTING_ING)
                .add(key(Items.FEATHER))
                .add(key(Items.LEATHER))
                .add(key(Items.RABBIT_HIDE))
                .add(key(Items.RABBIT_FOOT))
                .addTag(ItemTags.WOOL)
                .add(key(Items.INK_SAC))
                .add(key(Items.EGG))
                .add(key(Items.GLOW_INK_SAC));

        tag(MFUTags.Items.GLOOM_SPORE_CRAFTING_ING)
                .add(key(Items.ROTTEN_FLESH))
                .add(key(Items.BONE))
                .add(key(Items.GUNPOWDER))
                .add(key(Items.STRING))
                .add(key(Items.SPIDER_EYE))
                .add(key(Items.SKELETON_SKULL))
                .add(key(Items.ZOMBIE_HEAD))
                .add(key(Items.WITHER_SKELETON_SKULL))
                .add(key(Items.CREEPER_HEAD));
    }
}