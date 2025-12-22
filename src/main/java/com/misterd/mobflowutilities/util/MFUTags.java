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

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("mobflowutilities", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> MOBFLOWUTILITIES_TOOLS = createTag("mobflowutilities_tools");
        public static final TagKey<Item> MOBFLOWUTILITIES_MODULES = createTag("mobflowutilities_modules");
        public static final TagKey<Item> GENESIS_CHAMBER_FUELS = createTag("genesis_chamber_fuels");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("mobflowutilities", name));
        }
    }
}
