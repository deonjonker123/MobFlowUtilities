package com.misterd.mobflowutilities.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class MFUFoodProperties {
    public static final FoodProperties UMBRAL_BERRY = new FoodProperties.Builder().nutrition(1)
            .saturationModifier(0.1f).fast()
            .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 60, 0), 0.5f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 60, 0), 0.3f)
            .build();

    public static final FoodProperties RADIANT_BERRY = new FoodProperties.Builder().nutrition(3)
            .saturationModifier(0.4f).fast()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 60, 0), 0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0), 0.5f)
            .build();
}
