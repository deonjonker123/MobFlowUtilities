package com.misterd.mobflowutilities.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MFUTags {

    public static class Blocks {
        public static final TagKey<Block> MOBFLOWUTILITIES_PADS = createTag("mobflowutilities_pads");
        public static final TagKey<Block> MOBFLOWUTILITIES_MACHINES = createTag("mobflowutilities_machines");
        public static final TagKey<Block> ORE_BLOCKS_MFU = createTag("ores/mfu");
        public static final TagKey<Block> RAW_BLOCKS_MFU = createTag("raw_blocks/mfu");
        public static final TagKey<Block> NEEDS_GLOOMSTEEL_TOOL = createTag("needs_gloomsteel_tool");
        public static final TagKey<Block> NEEDS_GLIMMERSTEEL_TOOL = createTag("needs_glimmersteel_tool");
        public static final TagKey<Block> INCORRECT_FOR_GLOOMSTEEL_TOOL = createTag("incorrect_for_gloomsteel_tool");
        public static final TagKey<Block> INCORRECT_FOR_GLIMMERSTEEL_TOOL = createTag("incorrect_for_glimmersteel_tool");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("mobflowutilities", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ORES_MFU = createTag("ores/mfu");
        public static final TagKey<Item> INGOTS_MFU = createTag("ingots/mfu");
        public static final TagKey<Item> NUGGETS_MFU = createTag("nuggets/mfu");
        public static final TagKey<Item> RAW_MATERIALS_MFU = createTag("raw_materials/mfu");
        public static final TagKey<Item> GLOOMWOOD_LOGS = createTag("gloomwood_logs");
        public static final TagKey<Item> GLIMMERWOOD_LOGS = createTag("glimmerwood_logs");
        public static final TagKey<Item> GLOOMWOOD_LOGS_FOR_WOOD = createTag("gloomwood_logs_for_wood");
        public static final TagKey<Item> GLIMMERWOOD_LOGS_FOR_WOOD = createTag("glimmerwood_logs_for_wood");
        public static final TagKey<Item> MOBFLOWUTILITIES_TOOLS = createTag("mobflowutilities_tools");
        public static final TagKey<Item> MOBFLOWUTILITIES_MODULES = createTag("mobflowutilities_modules");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("mobflowutilities", name));
        }
    }
}
