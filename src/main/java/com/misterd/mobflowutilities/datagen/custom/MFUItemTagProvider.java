package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.fluid.MFUFluids;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MFUItemTagProvider extends ItemTagsProvider {

    public MFUItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MobFlowUtilities.MODID);
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

        tag(MFUTags.Items.EXPERIENCE_BUCKET)
                .add(MFUFluids.LIQUID_XP_BUCKET.get());

        tag(MFUTags.Items.GLIMMER_SPROUT_CRAFTING_ING)
                .add(Items.FEATHER)
                .add(Items.LEATHER)
                .add(Items.RABBIT_HIDE)
                .add(Items.RABBIT_FOOT)
                .addTag(ItemTags.WOOL)
                .add(Items.INK_SAC)
                .add(Items.EGG)
                .add(Items.GLOW_INK_SAC);

        tag(MFUTags.Items.GLOOM_SPORE_CRAFTING_ING)
                .add(Items.ROTTEN_FLESH)
                .add(Items.BONE)
                .add(Items.GUNPOWDER)
                .add(Items.STRING)
                .add(Items.SPIDER_EYE)
                .add(Items.SKELETON_SKULL)
                .add(Items.ZOMBIE_HEAD)
                .add(Items.WITHER_SKELETON_SKULL)
                .add(Items.CREEPER_HEAD);
    }
}