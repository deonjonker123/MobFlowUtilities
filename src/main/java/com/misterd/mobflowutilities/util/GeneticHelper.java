package com.misterd.mobflowutilities.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class GeneticHelper {

    public static boolean canCollectDNA(EntityType<?> entityType) {
        if (entityType == EntityTypes.PLAYER) return false;
        if (!hasSpawnEgg(entityType)) return false;
        return !isBlacklisted(entityType);
    }

    private static boolean isBlacklisted(EntityType<?> entityType) {
        return entityType == EntityTypes.ENDER_DRAGON || entityType == EntityTypes.WITHER;
    }

    public static boolean hasSpawnEgg(EntityType<?> entityType) {
        return SpawnEggItem.byId(entityType).isPresent();
    }

    public static Item getSpawnEgg(EntityType<?> entityType) {
        return SpawnEggItem.byId(entityType)
                .map(holder -> holder.value())
                .orElse(null);
    }

    public static Identifier getEntityKey(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }
}