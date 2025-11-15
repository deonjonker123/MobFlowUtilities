package com.misterd.mobflowutilities.recipe;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.GeneticHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
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
        ItemStack geneSample = ItemStack.EMPTY;
        ItemStack incubationCrystal = ItemStack.EMPTY;
        ItemStack gemstone = ItemStack.EMPTY;
        int itemCount = 0;

        // Check all slots in the crafting grid
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            itemCount++;

            // Check for gene sample vial
            if (stack.is(MFUItems.GENE_SAMPLE_VIAL.get())) {
                if (!geneSample.isEmpty()) {
                    return false; // Multiple gene samples
                }
                geneSample = stack;
            }
            // Check for incubation crystal
            else if (stack.is(MFUItems.INCUBATION_CRYSTAL.get())) {
                if (!incubationCrystal.isEmpty()) {
                    return false; // Multiple crystals
                }
                incubationCrystal = stack;
            }
            // Check for emerald or diamond
            else if (stack.is(Items.EMERALD) || stack.is(Items.DIAMOND)) {
                if (!gemstone.isEmpty()) {
                    return false; // Multiple gemstones
                }
                gemstone = stack;
            }
            // Unknown item in the grid
            else {
                return false;
            }
        }

        // Must have exactly 3 items
        if (itemCount != 3) {
            return false;
        }

        // All three required items must be present
        if (geneSample.isEmpty() || incubationCrystal.isEmpty() || gemstone.isEmpty()) {
            return false;
        }

        // Gene sample must have valid DNA data
        ResourceLocation entityKey = geneSample.get(MFUDataComponents.ENTITY_DNA.get());
        if (entityKey == null) {
            return false;
        }

        // Entity must have a spawn egg
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityKey);
        if (entityType == null) {
            return false;
        }

        SpawnEggItem spawnEgg = GeneticHelper.getSpawnEgg(entityType);
        return spawnEgg != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack geneSample = ItemStack.EMPTY;

        // Find the gene sample vial in the crafting grid
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(MFUItems.GENE_SAMPLE_VIAL.get())) {
                geneSample = stack;
                break;
            }
        }

        if (geneSample.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Get the entity type from the gene sample
        ResourceLocation entityKey = geneSample.get(MFUDataComponents.ENTITY_DNA.get());
        if (entityKey == null) {
            return ItemStack.EMPTY;
        }

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityKey);
        if (entityType == null) {
            return ItemStack.EMPTY;
        }

        // Get the spawn egg
        SpawnEggItem spawnEgg = GeneticHelper.getSpawnEgg(entityType);
        if (spawnEgg == null) {
            return ItemStack.EMPTY;
        }

        // Return the spawn egg
        return new ItemStack(spawnEgg);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // Can be crafted in 2x2 or 3x3 grid
        return width >= 2 && height >= 2;
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

    @Override
    public boolean isSpecial() {
        // This is a special recipe (dynamic output)
        return true;
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