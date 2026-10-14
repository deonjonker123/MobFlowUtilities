package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class MFUEntityTagsProvider extends EntityTypeTagsProvider {

    public MFUEntityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(MFUTags.Entities.CANNOT_SAMPLE)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER);

        tag(MFUTags.Entities.CANNOT_CATCH)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.RAVAGER)
                .add(EntityType.WARDEN);

    }
}