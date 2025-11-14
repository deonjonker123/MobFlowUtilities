package com.misterd.mobflowutilities.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Custom recipe type for crafting spawn eggs using:
 * - Incubation Crystal (boss drop)
 * - Gene Sample Vial (from right-clicking mobs)
 * - Diamond or Emerald
 */
public class GeneticRecipe implements Recipe<CraftingInput> {

    private final String group;

    public GeneticRecipe(String group) {
        this.group = group;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        // Recipe matching logic handled by the custom crafting system
        // This is a special recipe type that doesn't use standard matching
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        // Assembly logic handled by the custom crafting system
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // Requires 3x3 crafting grid
        return width >= 3 && height >= 3;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        // Result is dynamic based on the gene sample used
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MFURecipeSerializers.GENETIC_RECIPE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MFURecipeSerializers.GENETIC_RECIPE_TYPE.get();
    }

    /**
     * Serializer for the Genetic Recipe
     */
    public static class Serializer implements RecipeSerializer<GeneticRecipe> {

        public static final MapCodec<GeneticRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(GeneticRecipe::getGroup)
                ).apply(instance, GeneticRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, GeneticRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<GeneticRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GeneticRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, GeneticRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
        }

        private static GeneticRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            return new GeneticRecipe(group);
        }
    }
}