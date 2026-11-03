package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.concurrent.CompletableFuture;

public class MFUEntityTagsProvider extends EntityTypeTagsProvider {

    public MFUEntityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    private static ResourceKey<EntityType<?>> key(EntityType<?> type) {
        return type.builtInRegistryHolder().key();
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(MFUTags.Entities.CANNOT_SAMPLE)
                .add(key(EntityTypes.ENDER_DRAGON))
                .add(key(EntityTypes.WITHER));

        tag(MFUTags.Entities.CANNOT_CATCH)
                .add(key(EntityTypes.ENDER_DRAGON))
                .add(key(EntityTypes.WITHER))
                .add(key(EntityTypes.ELDER_GUARDIAN))
                .add(key(EntityTypes.RAVAGER))
                .add(key(EntityTypes.WARDEN));
    }
}