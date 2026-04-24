package com.misterd.mobflowutilities.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class GeneticHelper {

    public static boolean canCollectDNA(EntityType<?> entityType) {
        if (entityType == EntityType.PLAYER) return false;
        if (!hasSpawnEgg(entityType)) return false;
        return !isBlacklisted(entityType);
    }

    private static boolean isBlacklisted(EntityType<?> entityType) {
        return entityType == EntityType.ENDER_DRAGON || entityType == EntityType.WITHER;
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