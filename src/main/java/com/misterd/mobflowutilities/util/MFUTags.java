package com.misterd.mobflowutilities.util;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class MFUTags {

    public static class Fluids {
        public static final TagKey<Fluid> EXPERIENCE = createTag("c", "experience");

        private static TagKey<Fluid> createTag(String namespace, String name) {
            return FluidTags.create(Identifier.fromNamespaceAndPath(namespace, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> MOBFLOWUTILITIES_PADS = createTag("mobflowutilities_pads");
        public static final TagKey<Block> MOBFLOWUTILITIES_MACHINES = createTag("mobflowutilities_machines");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("mobflowutilities", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> MOBFLOWUTILITIES_MODULES = createTag("mobflowutilities", "mobflowutilities_modules");
        public static final TagKey<Item> GENESIS_CHAMBER_FUELS = createTag("mobflowutilities", "genesis_chamber_fuels");
        public static final TagKey<Item> EXPERIENCE_BUCKET = createTag("c", "experience_bucket");
        public static final TagKey<Item> WRENCHES = createTag("c", "wrenches");
        public static final TagKey<Item> WRENCH = createTag("c", "tools/wrench");
        public static final TagKey<Item> TOOL = createTag("c", "tools");
        public static final TagKey<Item> GLOOM_SPORE_CRAFTING_ING = createTag("mobflowutilities", "gloom_spore_crafting_ing");
        public static final TagKey<Item> GLIMMER_SPROUT_CRAFTING_ING = createTag("mobflowutilities", "glimmer_sprout_crafting_ing");

        private static TagKey<Item> createTag(String namespace, String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(namespace, name));
        }
    }
}