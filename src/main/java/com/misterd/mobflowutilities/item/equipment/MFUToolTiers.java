package com.misterd.mobflowutilities.item.equipment;

import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.MFUTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class MFUToolTiers {

    public static final Tier GLOOMSTEEL = new SimpleTier(
            MFUTags.Blocks.INCORRECT_FOR_GLOOMSTEEL_TOOL,
            999,
            7.0F,
            3.0F,
            18,
            () -> Ingredient.of(MFUItems.GLOOMSTEEL_INGOT)
    );

    public static final Tier GLIMMERSTEEL = new SimpleTier(
            MFUTags.Blocks.INCORRECT_FOR_GLIMMERSTEEL_TOOL,
            999,
            7.0F,
            3.0F,
            18,
            () -> Ingredient.of(MFUItems.GLIMMERSTEEL_INGOT)
    );
}
