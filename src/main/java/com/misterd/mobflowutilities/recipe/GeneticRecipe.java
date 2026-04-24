package com.misterd.mobflowutilities.recipe;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.util.GeneticHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;

public class GeneticRecipe implements CraftingRecipe {

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(
                Ingredient.of(MFUItems.GENE_SAMPLE_VIAL.get()),
                Ingredient.of(MFUItems.INCUBATION_ORB.get()),
                Ingredient.of(Items.EMERALD)
        ));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasGeneSample = false;
        boolean hasIncubationOrb = false;
        boolean hasEmerald = false;
        Identifier entityDNA = null;
        int itemCount = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            itemCount++;

            if (stack.getItem() == MFUItems.GENE_SAMPLE_VIAL.get()) {
                if (hasGeneSample) return false;
                entityDNA = stack.get(MFUDataComponents.ENTITY_DNA.get());
                if (entityDNA == null) return false;
                hasGeneSample = true;
            } else if (stack.getItem() == MFUItems.INCUBATION_ORB.get()) {
                if (hasIncubationOrb) return false;
                hasIncubationOrb = true;
            } else if (stack.getItem() == Items.EMERALD) {
                if (hasEmerald) return false;
                hasEmerald = true;
            } else {
                return false;
            }
        }

        if (itemCount != 3 || !hasGeneSample || !hasIncubationOrb || !hasEmerald) return false;

        Identifier dna = entityDNA;
        return BuiltInRegistries.ENTITY_TYPE.get(dna)
                .map(holder -> GeneticHelper.canCollectDNA(holder.value()))
                .orElse(false);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() != MFUItems.GENE_SAMPLE_VIAL.get()) continue;

            Identifier entityDNA = stack.get(MFUDataComponents.ENTITY_DNA.get());
            if (entityDNA == null) break;

            return BuiltInRegistries.ENTITY_TYPE.get(entityDNA).map(holder -> {
                Item egg = GeneticHelper.getSpawnEgg(holder.value());
                return egg != null ? new ItemStack(egg) : ItemStack.EMPTY;
            }).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessCraftingRecipeDisplay(
                List.of(
                        Ingredient.of(MFUItems.GENE_SAMPLE_VIAL.get()).display(),
                        Ingredient.of(MFUItems.INCUBATION_ORB.get()).display(),
                        Ingredient.of(Items.EMERALD).display()
                ),
                new SlotDisplay.ItemSlotDisplay(Items.EGG),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
        ));
    }

    @Override
    public RecipeSerializer<GeneticRecipe> getSerializer() {
        return MFURecipeSerializers.GENETIC_RECIPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    // --- Serializer ---

    public static final MapCodec<GeneticRecipe> CODEC = MapCodec.unit(GeneticRecipe::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, GeneticRecipe> STREAM_CODEC =
            StreamCodec.of((buf, recipe) -> {}, buf -> new GeneticRecipe());
}