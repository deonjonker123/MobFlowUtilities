package com.misterd.mobflowutilities.worldgen.trees;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.worldgen.MFUConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class MFUTreeGrowers {
    public static final TreeGrower GLOOMWOOD = new TreeGrower(MobFlowUtilities.MODID + ":gloomwood", Optional.empty(), Optional.of(MFUConfiguredFeatures.GLOOMWOOD_KEY), Optional.empty());
    public static final TreeGrower GLIMMERWOOD = new TreeGrower(MobFlowUtilities.MODID + ":glimmerwood", Optional.empty(), Optional.of(MFUConfiguredFeatures.GLIMMERWOOD_KEY), Optional.empty());
}
