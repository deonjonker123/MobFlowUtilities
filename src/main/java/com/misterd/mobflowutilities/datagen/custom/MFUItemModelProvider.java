package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MFUItemModelProvider extends ItemModelProvider {

    public MFUItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MobFlowUtilities.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(MFUItems.BOA_MODULE.get());
        basicItem(MFUItems.FIRE_ASPECT_MODULE.get());
        basicItem(MFUItems.SHARPNESS_MODULE.get());
        basicItem(MFUItems.SMITE_MODULE.get());
        basicItem(MFUItems.LOOTING_MODULE.get());
        basicItem(MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get());
        basicItem(MFUItems.VOID_FILTER_MODULE.get());
        basicItem(MFUItems.SPEED_MODULE.get());
        basicItem(MFUItems.FAN_WIDTH_INCREASE_MODULE.get());
        basicItem(MFUItems.FAN_HEIGHT_INCREASE_MODULE.get());
        basicItem(MFUItems.FAN_DISTANCE_INCREASE_MODULE.get());

        basicItem(MFUItems.PAD_WRENCH.get());

        basicItem(MFUItems.GLOOM_SPORE.get());
        basicItem(MFUItems.GLIMMER_SPROUT.get());

        basicItem(MFUItems.EMPTY_GENE_VIAL.get());
        basicItem(MFUItems.GENE_SAMPLE_VIAL.get());
        basicItem(MFUItems.INCUBATION_ORB.get());
    }
}