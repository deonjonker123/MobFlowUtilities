package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.recipe.GeneticRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MFURecipeProvider extends RecipeProvider implements IConditionBuilder {

    public MFURecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Gloomsteel smeltables
        List<ItemLike> GLOOMSTEEL_SMELTABLES = List.of(
                MFUItems.RAW_GLOOMSTEEL,
                MFUBlocks.GLOOMSTEEL_STONE_ORE,
                MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE,
                MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE,
                MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE
        );

        List<ItemLike> GLOOMSTEEL_RAW_BLOCK_SMELTABLES = List.of(MFUBlocks.RAW_GLOOMSTEEL_BLOCK);

        // Raw Gloomsteel block packing/unpacking
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.RAW_GLOOMSTEEL_BLOCK.get())
                .pattern("GGG")
                .pattern("GGG")
                .pattern("GGG")
                .define('G',  MFUItems.RAW_GLOOMSTEEL.get())
                .unlockedBy("has_raw_gloomsteel", has( MFUItems.RAW_GLOOMSTEEL))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,  MFUItems.RAW_GLOOMSTEEL.get(), 9)
                .requires( MFUBlocks.RAW_GLOOMSTEEL_BLOCK)
                .unlockedBy("has_raw_gloomsteel_block", has( MFUBlocks.RAW_GLOOMSTEEL_BLOCK))
                .save(recipeOutput);

        // Gloomsteel block packing/unpacking
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.GLOOMSTEEL_BLOCK.get())
                .pattern("GGG")
                .pattern("GGG")
                .pattern("GGG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,  MFUItems.GLOOMSTEEL_INGOT.get(), 9)
                .requires( MFUBlocks.GLOOMSTEEL_BLOCK)
                .unlockedBy("has_gloomsteel_block", has( MFUBlocks.GLOOMSTEEL_BLOCK))
                .save(recipeOutput);

        // Gloomsteel ingot from nuggets
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.GLOOMSTEEL_INGOT.get())
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N',  MFUItems.GLOOMSTEEL_NUGGET.get())
                .unlockedBy("has_gloomsteel_nugget", has( MFUItems.GLOOMSTEEL_NUGGET))
                .save(recipeOutput, "gloomsteel_ingot_from_nuggets");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,  MFUItems.GLOOMSTEEL_NUGGET.get(), 9)
                .requires( MFUItems.GLOOMSTEEL_INGOT)
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        // Gloomsteel smelting and blasting
        oreSmelting(recipeOutput, GLOOMSTEEL_SMELTABLES, RecipeCategory.MISC,
                 MFUItems.GLOOMSTEEL_INGOT.get(), 0.25F, 200, "gloomsteel");
        oreBlasting(recipeOutput, GLOOMSTEEL_SMELTABLES, RecipeCategory.MISC,
                 MFUItems.GLOOMSTEEL_INGOT.get(), 0.25F, 100, "gloomsteel");

        oreSmelting(recipeOutput, GLOOMSTEEL_RAW_BLOCK_SMELTABLES, RecipeCategory.MISC,
                 MFUBlocks.GLOOMSTEEL_BLOCK.get(), 0.5F, 300, "gloomsteel");
        oreBlasting(recipeOutput, GLOOMSTEEL_RAW_BLOCK_SMELTABLES, RecipeCategory.MISC,
                 MFUBlocks.GLOOMSTEEL_BLOCK.get(), 0.5F, 150, "gloomsteel");

        // Controller
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.CONTROLLER.get())
                .pattern("GCG")
                .pattern("IBI")
                .pattern("GCG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('I',  Items.IRON_INGOT)
                .define('C',  Items.COMPARATOR)
                .define('B',  Items.REDSTONE_BLOCK)
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        // Collector
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.COLLECTOR.get())
                .pattern("GEG")
                .pattern("PCP")
                .pattern("GHG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('P',  Items.ENDER_PEARL)
                .define('H',  Items.HOPPER)
                .define('C', Tags.Items.CHESTS)
                .define('E',  Items.ENDER_EYE)
                .unlockedBy("has_ender_eye", has( Items.ENDER_EYE))
                .save(recipeOutput);

        // Kill Pad
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.DAMAGE_PAD.get(), 4)
                .pattern("GSG")
                .pattern("AQA")
                .pattern("GSG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.DIAMOND_SWORD)
                .define('A',  Items.DIAMOND_AXE)
                .define('Q',  Items.QUARTZ_BLOCK)
                .unlockedBy("has_quartz", has( Items.QUARTZ))
                .save(recipeOutput);

        // Flow Pads (tiered progression)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.FAST_FLOW_PAD.get(), 8)
                .pattern("IGI")
                .pattern("GQG")
                .pattern("SSS")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S', Tags.Items.STONES)
                .define('Q',  Items.QUARTZ_BLOCK)
                .define('I',  Items.IRON_INGOT)
                .unlockedBy("has_quartz", has( Items.QUARTZ))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.FASTER_FLOW_PAD.get(), 8)
                .pattern("DGD")
                .pattern("GFG")
                .pattern("SSS")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('D',  Items.GOLD_BLOCK)
                .define('S', Tags.Items.STONES)
                .define('F',  MFUBlocks.FAST_FLOW_PAD.get())
                .unlockedBy("has_fast_flow_pad", has( MFUBlocks.FAST_FLOW_PAD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.FASTEST_FLOW_PAD.get(), 8)
                .pattern("DGD")
                .pattern("GFG")
                .pattern("SSS")
                .define('G',  MFUBlocks.GLOOMSTEEL_BLOCK.get())
                .define('D',  Items.DIAMOND_BLOCK)
                .define('S', Tags.Items.STONES)
                .define('F',  MFUBlocks.FASTER_FLOW_PAD.get())
                .unlockedBy("has_faster_flow_pad", has( MFUBlocks.FASTER_FLOW_PAD))
                .save(recipeOutput);

        // Pad Wrench
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,  MFUItems.PAD_WRENCH.get())
                .pattern("  G")
                .pattern(" S ")
                .pattern("G  ")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.STICK)
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        // Controller upgrade modules
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.SHARPNESS_MODULE.get())
                .pattern("GSG")
                .pattern("SIS")
                .pattern("GSG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.DIAMOND_SWORD)
                .define('I',  Items.IRON_BLOCK)
                .unlockedBy("has_diamond_sword", has( Items.DIAMOND_SWORD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.FIRE_ASPECT_MODULE.get())
                .pattern("GBG")
                .pattern("BFB")
                .pattern("GBG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('B',  Items.BLAZE_ROD)
                .define('F',  Items.FLINT_AND_STEEL)
                .unlockedBy("has_blaze_rod", has( Items.BLAZE_ROD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.SMITE_MODULE.get())
                .pattern("GRG")
                .pattern("RBR")
                .pattern("GRG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('R',  Items.ROTTEN_FLESH)
                .define('B',  Items.BONE_BLOCK)
                .unlockedBy("has_rotten_flesh", has( Items.ROTTEN_FLESH))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.BOA_MODULE.get())
                .pattern("GEG")
                .pattern("TST")
                .pattern("GEG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.SPIDER_EYE)
                .define('E',  Items.FERMENTED_SPIDER_EYE)
                .define('T',  Items.STRING)
                .unlockedBy("has_spider_eye", has( Items.SPIDER_EYE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.LOOTING_MODULE.get())
                .pattern("GLG")
                .pattern("DXD")
                .pattern("GLG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('L',  Items.LAPIS_BLOCK)
                .define('D',  Items.DIAMOND_BLOCK)
                .define('X',  Items.GOLD_BLOCK)
                .unlockedBy("has_gold_ingot", has( Items.GOLD_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.VOID_FILTER_MODULE.get())
                .pattern("PPP")
                .pattern("OHO")
                .pattern("GOG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('O', Tags.Items.OBSIDIANS)
                .define('P',  Items.PAPER)
                .define('H',  Items.HOPPER)
                .unlockedBy("has_hopper", has( Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get())
                .pattern("GEG")
                .pattern("ERE")
                .pattern("GEG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('E',  Items.ENDER_EYE)
                .define('R',  Items.OBSERVER)
                .unlockedBy("has_ender_eye", has( Items.ENDER_EYE))
                .save(recipeOutput);

        // Reset recipes (shapeless to clear data)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,  MFUBlocks.COLLECTOR.get())
                .requires( MFUBlocks.COLLECTOR.get())
                .unlockedBy("has_collector", has( MFUBlocks.COLLECTOR))
                .save(recipeOutput, "mobflowutilities:collector_reset");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,  MFUItems.VOID_FILTER_MODULE.get())
                .requires( MFUItems.VOID_FILTER_MODULE.get())
                .unlockedBy("has_void_filter", has( MFUItems.VOID_FILTER_MODULE))
                .save(recipeOutput, "mobflowutilities:void_filter_reset");

        // Decorative blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.DARK_GLASS.get(), 8)
                .pattern("GXG")
                .pattern("XCX")
                .pattern("GXG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('X', Tags.Items.GLASS_BLOCKS)
                .define('C', ItemTags.COALS)
                .unlockedBy("has_coal", has( Items.COAL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUBlocks.GLIMMER_LAMP.get(), 8)
                .pattern("GXG")
                .pattern("XSX")
                .pattern("GXG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('X', Tags.Items.GLASS_BLOCKS)
                .define('S',  Items.GLOWSTONE)
                .unlockedBy("has_glowstone", has( Items.GLOWSTONE))
                .save(recipeOutput);

        // Mob Catcher
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.MOB_CATCHER.get())
                .pattern("LEL")
                .pattern("EGE")
                .pattern("LYL")
                .define('G',  MFUBlocks.GLOOMSTEEL_BLOCK.get())
                .define('E',  Items.ENDER_PEARL)
                .define('Y',  Items.ENDER_EYE)
                .define('L',  Items.LEAD)
                .unlockedBy("has_lead", has( Items.LEAD))
                .save(recipeOutput);

        // Empty Gene Vial
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  MFUItems.EMPTY_GENE_VIAL.get())
                .pattern(" X ")
                .pattern("B B")
                .pattern("BGB")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('B', Tags.Items.GLASS_PANES)
                .define('X', ItemTags.WOODEN_BUTTONS)
                .unlockedBy("has_glass_pane", has(Tags.Items.GLASS_PANES))
                .save(recipeOutput);

        // Gloomsteel tools
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,  MFUItems.GLOOMSTEEL_SWORD.get())
                .pattern(" G ")
                .pattern(" G ")
                .pattern(" S ")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.STICK)
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,  MFUItems.GLOOMSTEEL_PICKAXE.get())
                .pattern("GGG")
                .pattern(" S ")
                .pattern(" S ")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.STICK)
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,  MFUItems.GLOOMSTEEL_AXE.get())
                .pattern("GG ")
                .pattern("GS ")
                .pattern(" S ")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.STICK)
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,  MFUItems.GLOOMSTEEL_SHOVEL.get())
                .pattern(" G ")
                .pattern(" S ")
                .pattern(" S ")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.STICK)
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,  MFUItems.GLOOMSTEEL_HOE.get())
                .pattern("GG ")
                .pattern(" S ")
                .pattern(" S ")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .define('S',  Items.STICK)
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        // Gloomsteel Paxel
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,  MFUItems.GLOOMSTEEL_PAXEL.get())
                .pattern("ASP")
                .pattern(" # ")
                .pattern(" # ")
                .define('P',  MFUItems.GLOOMSTEEL_PICKAXE.get())
                .define('S',  MFUItems.GLOOMSTEEL_SHOVEL.get())
                .define('#',  Items.STICK)
                .define('A',  MFUItems.GLOOMSTEEL_AXE.get())
                .unlockedBy("has_gloomsteel_pickaxe", has( MFUItems.GLOOMSTEEL_PICKAXE))
                .unlockedBy("has_gloomsteel_axe", has( MFUItems.GLOOMSTEEL_AXE))
                .unlockedBy("has_gloomsteel_shovel", has( MFUItems.GLOOMSTEEL_SHOVEL))
                .save(recipeOutput);

        // Gloomsteel Hammer
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,  MFUItems.GLOOMSTEEL_HAMMER.get())
                .pattern("GSG")
                .pattern(" S ")
                .pattern(" S ")
                .define('G',  MFUBlocks.GLOOMSTEEL_BLOCK.get())
                .define('S',  Items.STICK)
                .unlockedBy("has_gloomsteel_block", has( MFUBlocks.GLOOMSTEEL_BLOCK.get()))
                .save(recipeOutput);

        // Gloomsteel armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,  MFUItems.GLOOMSTEEL_HELMET.get())
                .pattern("GGG")
                .pattern("G G")
                .pattern("   ")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,  MFUItems.GLOOMSTEEL_CHESTPLATE.get())
                .pattern("G G")
                .pattern("GGG")
                .pattern("GGG")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,  MFUItems.GLOOMSTEEL_LEGGINGS.get())
                .pattern("GGG")
                .pattern("G G")
                .pattern("G G")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,  MFUItems.GLOOMSTEEL_BOOTS.get())
                .pattern("   ")
                .pattern("G G")
                .pattern("G G")
                .define('G',  MFUItems.GLOOMSTEEL_INGOT.get())
                .unlockedBy("has_gloomsteel_ingot", has( MFUItems.GLOOMSTEEL_INGOT))
                .save(recipeOutput);

        // Genetic recipe (custom recipe type - assuming this still exists)
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath("mobflowutilities", "genetic_recipe"),
                new GeneticRecipe("mfu_genetic"),
                null
        );
    }

    /**
     * Helper method for ore smelting recipes
     */
    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> ingredients,
                                      RecipeCategory category, ItemLike result,
                                      float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE,
                net.minecraft.world.item.crafting.SmeltingRecipe::new,
                ingredients, category, result, experience, cookingTime, group, "_from_smelting");
    }

    /**
     * Helper method for ore blasting recipes
     */
    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> ingredients,
                                      RecipeCategory category, ItemLike result,
                                      float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE,
                net.minecraft.world.item.crafting.BlastingRecipe::new,
                ingredients, category, result, experience, cookingTime, group, "_from_blasting");
    }

    /**
     * Generic cooking recipe helper
     */
    protected static <T extends AbstractCookingRecipe> void oreCooking(
            RecipeOutput recipeOutput,
            RecipeSerializer<T> cookingSerializer,
            AbstractCookingRecipe.Factory<T> factory,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group,
            String recipeName) {

        for (ItemLike ingredient : ingredients) {
            SimpleCookingRecipeBuilder.generic(
                            Ingredient.of(ingredient),
                            category,
                            result,
                            experience,
                            cookingTime,
                            cookingSerializer,
                            factory)
                    .group(group)
                    .unlockedBy(getHasName(ingredient), has(ingredient))
                    .save(recipeOutput, "mobflowutilities:" + getItemName(result) + recipeName + "_" + getItemName(ingredient));
        }
    }
}