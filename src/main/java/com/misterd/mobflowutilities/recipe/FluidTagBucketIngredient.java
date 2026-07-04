package com.misterd.mobflowutilities.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FluidTagBucketIngredient implements ICustomIngredient {

    public static final MapCodec<FluidTagBucketIngredient> CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    TagKey.codec(Registries.FLUID).fieldOf("fluid_tag").forGetter(i -> i.fluidTag)
            ).apply(inst, FluidTagBucketIngredient::new));

    private final TagKey<Fluid> fluidTag;

    public FluidTagBucketIngredient(TagKey<Fluid> fluidTag) {
        this.fluidTag = fluidTag;
    }

    public TagKey<Fluid> fluidTag() {
        return fluidTag;
    }

    private Stream<Holder<Fluid>> tagContents() {
        return StreamSupport.stream(
                BuiltInRegistries.FLUID.getTagOrEmpty(fluidTag).spliterator(),
                false
        );
    }

    @Override
    public Stream<Holder<Item>> items() {
        return tagContents()
                .map(h -> h.value().getBucket())
                .filter(item -> item != null && item != Items.AIR)
                .map(Item::builtInRegistryHolder);
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item stackItem = stack.getItem();
        return tagContents().anyMatch(h -> h.value().getBucket() == stackItem);
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public IngredientType<?> getType() {
        return MFURecipeSerializers.FLUID_TAG_BUCKET.get();
    }
}