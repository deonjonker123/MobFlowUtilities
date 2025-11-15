package com.misterd.mobflowutilities.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;

public class GeneticHelper {
    public static boolean canCollectDNA(EntityType<?> entityType) {
        if (entityType == EntityType.PLAYER) {
            return false;
        } else if (SpawnEggItem.byId(entityType) == null) {
            return false;
        } else {
            return !isBlacklisted(entityType);
        }
    }

    private static boolean isBlacklisted(EntityType<?> entityType) {
        return entityType == EntityType.ENDER_DRAGON || entityType == EntityType.WITHER;
    }

    public static SpawnEggItem getSpawnEgg(EntityType<?> entityType) {
        return SpawnEggItem.byId(entityType);
    }

    public static ResourceLocation getEntityKey(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }
}
