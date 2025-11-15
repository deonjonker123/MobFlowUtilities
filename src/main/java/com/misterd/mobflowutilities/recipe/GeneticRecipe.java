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

public class GeneticRecipe implements Recipe<CraftingInput> {
    private final String group;

    public GeneticRecipe(String group) {
        this.group = group;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(Items.EGG);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(MFUItems.GENE_SAMPLE_VIAL.get()));
        ingredients.add(Ingredient.of(MFUItems.INCUBATION_CRYSTAL.get()));
        ingredients.add(Ingredient.of(Items.EMERALD));
        return ingredients;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasGeneSample = false;
        boolean hasPulseCore = false;
        boolean hasEmerald = false;
        ResourceLocation entityDNA = null;
        int itemCount = 0;

        for(int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                ++itemCount;
                if (stack.getItem() == MFUItems.GENE_SAMPLE_VIAL.get()) {
                    if (hasGeneSample) {
                        return false;
                    }

                    entityDNA = stack.get(MFUDataComponents.ENTITY_DNA.get());
                    if (entityDNA == null) {
                        return false;
                    }

                    hasGeneSample = true;
                } else if (stack.getItem() == MFUItems.INCUBATION_CRYSTAL.get()) {
                    if (hasPulseCore) {
                        return false;
                    }

                    hasPulseCore = true;
                } else {
                    if (stack.getItem() != Items.EMERALD) {
                        return false;
                    }

                    if (hasEmerald) {
                        return false;
                    }

                    hasEmerald = true;
                }
            }
        }

        if (itemCount == 3 && hasGeneSample && hasPulseCore && hasEmerald) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityDNA);
            boolean canCollect = GeneticHelper.canCollectDNA(entityType);
            return canCollect;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for(int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() == MFUItems.GENE_SAMPLE_VIAL.get()) {
                ResourceLocation entityDNA = stack.get(MFUDataComponents.ENTITY_DNA.get());
                if (entityDNA != null) {
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityDNA);
                    SpawnEggItem spawnEgg = GeneticHelper.getSpawnEgg(entityType);
                    if (spawnEgg != null) {
                        return new ItemStack(spawnEgg);
                    }
                }
                break;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
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
        return true;
    }

    public static class Serializer implements RecipeSerializer<GeneticRecipe> {
        public static final MapCodec<GeneticRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(GeneticRecipe::getGroup)
                ).apply(instance, GeneticRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, GeneticRecipe> STREAM_CODEC =
                StreamCodec.of(GeneticRecipe.Serializer::toNetwork, GeneticRecipe.Serializer::fromNetwork);

        @Override
        public MapCodec<GeneticRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GeneticRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, GeneticRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
        }

        public static GeneticRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            return new GeneticRecipe(group);
        }
    }
}