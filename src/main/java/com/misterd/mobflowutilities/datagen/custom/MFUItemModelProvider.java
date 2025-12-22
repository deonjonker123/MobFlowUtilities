package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;

public class MFUItemModelProvider extends ItemModelProvider {

    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = new LinkedHashMap<>();

    static {
        TRIM_MATERIALS.put(TrimMaterials.QUARTZ, 0.1F);
        TRIM_MATERIALS.put(TrimMaterials.IRON, 0.2F);
        TRIM_MATERIALS.put(TrimMaterials.NETHERITE, 0.3F);
        TRIM_MATERIALS.put(TrimMaterials.REDSTONE, 0.4F);
        TRIM_MATERIALS.put(TrimMaterials.COPPER, 0.5F);
        TRIM_MATERIALS.put(TrimMaterials.GOLD, 0.6F);
        TRIM_MATERIALS.put(TrimMaterials.EMERALD, 0.7F);
        TRIM_MATERIALS.put(TrimMaterials.DIAMOND, 0.8F);
        TRIM_MATERIALS.put(TrimMaterials.LAPIS, 0.9F);
        TRIM_MATERIALS.put(TrimMaterials.AMETHYST, 1.0F);
    }

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

        basicItem(MFUItems.PAD_WRENCH.get());

        basicItem(MFUItems.GLOOM_SPORE.get());
        basicItem(MFUItems.GLIMMER_SPROUT.get());

        basicItem(MFUItems.EMPTY_GENE_VIAL.get());
        basicItem(MFUItems.GENE_SAMPLE_VIAL.get());
        basicItem(MFUItems.INCUBATION_ORB.get());
    }
}