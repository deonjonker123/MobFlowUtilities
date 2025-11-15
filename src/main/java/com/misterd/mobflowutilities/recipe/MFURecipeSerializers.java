package com.misterd.mobflowutilities.recipe;

import com.misterd.mobflowutilities.MobFlowUtilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MFURecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MobFlowUtilities.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MobFlowUtilities.MODID);

    public static final Supplier<RecipeSerializer<GeneticRecipe>> GENETIC_RECIPE =
            RECIPE_SERIALIZERS.register("genetic_recipe", GeneticRecipe.Serializer::new);

    public static final Supplier<RecipeType<GeneticRecipe>> GENETIC_RECIPE_TYPE =
            RECIPE_TYPES.register("genetic_recipe", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return MobFlowUtilities.MODID + ":genetic_recipe";
                }
            });

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}