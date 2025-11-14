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
        public static final TagKey<Block> ORE_BLOCKS_GLOOMSTEEL = createTag("ores/gloomsteel");
        public static final TagKey<Block> RAW_BLOCKS_GLOOMSTEEL = createTag("raw_blocks/gloomsteel");
        public static final TagKey<Block> NEEDS_GLOOMSTEEL_TOOL = createTag("needs_gloomsteel_tool");
        public static final TagKey<Block> INCORRECT_FOR_GLOOMSTEEL_TOOL = createTag("incorrect_for_gloomsteel_tool");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("mobflowutilities", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ORES_GLOOMSTEEL = createTag("ores/gloomsteel");
        public static final TagKey<Item> INGOTS_GLOOMSTEEL = createTag("ingots/gloomsteel");
        public static final TagKey<Item> NUGGETS_GLOOMSTEEL = createTag("nuggets/gloomsteel");
        public static final TagKey<Item> RAW_MATERIALS_GLOOMSTEEL = createTag("raw_materials/gloomsteel");
        public static final TagKey<Item> MOBFLOWUTILITIES_TOOLS = createTag("mobflowutilities_tools");
        public static final TagKey<Item> MOBFLOWUTILITIES_MODULES = createTag("mobflowutilities_modules");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("mobflowutilities", name));
        }
    }
}
