package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class MFURecipeProvider extends RecipeProvider implements IConditionBuilder {

    public MFURecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.CONTROLLER.get())
                .pattern("GCG")
                .pattern("IBI")
                .pattern("GCG")
                .define('G', Items.IRON_INGOT)
                .define('I', Items.GOLD_INGOT)
                .define('C', Items.COMPARATOR)
                .define('B', Items.REDSTONE_BLOCK)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.COLLECTOR.get())
                .pattern("GEG")
                .pattern("PCP")
                .pattern("GHG")
                .define('G', Items.IRON_INGOT)
                .define('P', Items.ENDER_PEARL)
                .define('H', Items.HOPPER)
                .define('C', Tags.Items.CHESTS)
                .define('E', Items.ENDER_EYE)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.GENESIS_CHAMBER.get())
                .pattern("GSG")
                .pattern("PHP")
                .pattern("GSG")
                .define('G', Items.IRON_BLOCK)
                .define('P', Items.DIAMOND_BLOCK)
                .define('H', Items.PISTON)
                .define('S', Items.OBSIDIAN)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.DAMAGE_PAD.get(), 4)
                .pattern("IGI")
                .pattern("GSG")
                .pattern("IGI")
                .define('G', Items.DIAMOND)
                .define('S', Items.DIAMOND_SWORD)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_diamond_sword", has(Items.DIAMOND_SWORD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.FAST_FLOW_PAD.get(), 4)
                .pattern("SGS")
                .pattern("GQG")
                .pattern("SGS")
                .define('G', Items.IRON_INGOT)
                .define('S', Tags.Items.STONES)
                .define('Q', Items.SUGAR)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.FASTER_FLOW_PAD.get(), 4)
                .pattern("GFG")
                .pattern("FQF")
                .pattern("GFG")
                .define('G', Items.GOLD_INGOT)
                .define('Q', Items.SUGAR)
                .define('F', MFUBlocks.FAST_FLOW_PAD.get())
                .unlockedBy("has_fast_flow_pad", has(MFUBlocks.FAST_FLOW_PAD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.FASTEST_FLOW_PAD.get(), 4)
                .pattern("GFG")
                .pattern("FQF")
                .pattern("GFG")
                .define('G', Items.DIAMOND)
                .define('Q', Items.SUGAR)
                .define('F', MFUBlocks.FASTER_FLOW_PAD.get())
                .unlockedBy("has_faster_flow_pad", has(MFUBlocks.FASTER_FLOW_PAD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MFUItems.PAD_WRENCH.get())
                .pattern(" G ")
                .pattern(" SG")
                .pattern("S  ")
                .define('G', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.SHARPNESS_MODULE.get())
                .pattern("GSG")
                .pattern("SIS")
                .pattern("GSG")
                .define('G', Items.IRON_INGOT)
                .define('S', Items.DIAMOND_SWORD)
                .define('I', Items.IRON_BLOCK)
                .unlockedBy("has_diamond_sword", has(Items.DIAMOND_SWORD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.FIRE_ASPECT_MODULE.get())
                .pattern("GBG")
                .pattern("BFB")
                .pattern("GBG")
                .define('G', Items.IRON_INGOT)
                .define('B', Items.BLAZE_ROD)
                .define('F', Items.FLINT_AND_STEEL)
                .unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.SMITE_MODULE.get())
                .pattern("GRG")
                .pattern("RBR")
                .pattern("GRG")
                .define('G', Items.IRON_INGOT)
                .define('R', Items.ROTTEN_FLESH)
                .define('B', Items.BONE_BLOCK)
                .unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.BOA_MODULE.get())
                .pattern("GEG")
                .pattern("TST")
                .pattern("GEG")
                .define('G', Items.IRON_INGOT)
                .define('S', Items.SPIDER_EYE)
                .define('E', Items.FERMENTED_SPIDER_EYE)
                .define('T', Items.STRING)
                .unlockedBy("has_spider_eye", has(Items.SPIDER_EYE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.LOOTING_MODULE.get())
                .pattern("GLG")
                .pattern("DXD")
                .pattern("GLG")
                .define('G', Items.IRON_INGOT)
                .define('L', Items.LAPIS_BLOCK)
                .define('D', Items.DIAMOND_BLOCK)
                .define('X', Items.GOLD_BLOCK)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.VOID_FILTER_MODULE.get())
                .pattern("PPP")
                .pattern("OHO")
                .pattern("GOG")
                .define('G', Items.IRON_INGOT)
                .define('O', Tags.Items.OBSIDIANS)
                .define('P', Items.PAPER)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get())
                .pattern("GEG")
                .pattern("ERE")
                .pattern("GEG")
                .define('G', Items.IRON_INGOT)
                .define('E', Items.ENDER_EYE)
                .define('R', Items.OBSERVER)
                .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.SPEED_MODULE.get())
                .pattern("L#L")
                .pattern("#D#")
                .pattern("L#L")
                .define('L', Items.IRON_BLOCK)
                .define('D', Items.DIAMOND_BLOCK)
                .define('#', Items.OBSIDIAN)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MFUBlocks.COLLECTOR.get())
                .requires(MFUBlocks.COLLECTOR.get())
                .unlockedBy("has_collector", has(MFUBlocks.COLLECTOR))
                .save(recipeOutput, "mobflowutilities:collector_reset");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MFUItems.VOID_FILTER_MODULE.get())
                .requires(MFUItems.VOID_FILTER_MODULE.get())
                .unlockedBy("has_void_filter", has(MFUItems.VOID_FILTER_MODULE))
                .save(recipeOutput, "mobflowutilities:void_filter_reset");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.DARK_GLASS.get(), 8)
                .pattern("GXG")
                .pattern("XCX")
                .pattern("GXG")
                .define('G', Items.IRON_INGOT)
                .define('X', Tags.Items.GLASS_BLOCKS)
                .define('C', ItemTags.COALS)
                .unlockedBy("has_coal", has(Items.COAL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUBlocks.GLIMMER_LAMP.get(), 8)
                .pattern("GXG")
                .pattern("XSX")
                .pattern("GXG")
                .define('G', Items.IRON_INGOT)
                .define('X', Tags.Items.GLASS_BLOCKS)
                .define('S', Items.GLOWSTONE)
                .unlockedBy("has_glowstone", has(Items.GLOWSTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.MOB_CATCHER.get())
                .pattern("LEL")
                .pattern("EGE")
                .pattern("LYL")
                .define('G', Items.LAPIS_BLOCK)
                .define('E', Items.ENDER_PEARL)
                .define('Y', Items.ENDER_EYE)
                .define('L', Items.LEAD)
                .unlockedBy("has_lead", has(Items.LEAD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MFUItems.EMPTY_GENE_VIAL.get())
                .pattern(" X ")
                .pattern("B B")
                .pattern("BBB")
                .define('B', Tags.Items.GLASS_PANES)
                .define('X', ItemTags.WOODEN_BUTTONS)
                .unlockedBy("has_glass_pane", has(Tags.Items.GLASS_PANES))
                .save(recipeOutput);
    }
}