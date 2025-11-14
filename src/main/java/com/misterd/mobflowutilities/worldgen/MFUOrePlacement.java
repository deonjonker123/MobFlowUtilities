package com.misterd.mobflowutilities.worldgen;

import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class MFUOrePlacement {

    /**
     * Creates a standard ore placement configuration
     * @param countPlacement How many veins per chunk
     * @param heightRange Vertical distribution of the ore
     * @return List of placement modifiers
     */
    public static List<PlacementModifier> orePlacement(PlacementModifier countPlacement, PlacementModifier heightRange) {
        return List.of(
                countPlacement,
                InSquarePlacement.spread(),
                heightRange,
                BiomeFilter.biome()
        );
    }

    /**
     * Creates a common ore placement with a fixed count per chunk
     * @param count Number of veins per chunk
     * @param heightRange Vertical distribution of the ore
     * @return List of placement modifiers
     */
    public static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    /**
     * Creates a rare ore placement with a rarity filter
     * @param chance 1 in X chance per chunk
     * @param heightRange Vertical distribution of the ore
     * @return List of placement modifiers
     */
    public static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier heightRange) {
        return orePlacement(RarityFilter.onAverageOnceEvery(chance), heightRange);
    }
}