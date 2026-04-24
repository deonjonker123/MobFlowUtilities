package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class MFUItemModelProvider extends ModelProvider {

    public MFUItemModelProvider(PackOutput output) {
        super(output, MobFlowUtilities.MODID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(MFUItems.BOA_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.FIRE_ASPECT_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.SHARPNESS_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.SMITE_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.LOOTING_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.VOID_FILTER_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.SPEED_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.FAN_WIDTH_INCREASE_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.FAN_HEIGHT_INCREASE_MODULE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.FAN_DISTANCE_INCREASE_MODULE.get(),ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(MFUItems.PAD_WRENCH.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.MOB_CATCHER.get(),ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(MFUItems.GLOOM_SPORE.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.GLIMMER_SPROUT.get(),ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(MFUItems.EMPTY_GENE_VIAL.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.GENE_SAMPLE_VIAL.get(),ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MFUItems.INCUBATION_ORB.get(),ModelTemplates.FLAT_ITEM);
    }
}