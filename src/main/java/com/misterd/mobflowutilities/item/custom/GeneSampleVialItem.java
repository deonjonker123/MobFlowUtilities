package com.misterd.mobflowutilities.item.custom;

import java.util.List;
import java.util.Optional;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class GeneSampleVialItem extends Item {

    public GeneSampleVialItem(Properties properties) {
        super(properties);
    }

    public boolean hasDNA(ItemStack stack) {
        return stack.has(MFUDataComponents.ENTITY_DNA.get());
    }

    public Optional<EntityType<?>> getStoredEntityType(ItemStack stack) {
        ResourceLocation entityKey = stack.get(MFUDataComponents.ENTITY_DNA.get());
        return entityKey == null ? Optional.empty() : BuiltInRegistries.ENTITY_TYPE.getOptional(entityKey);
    }

    public String getStoredEntityName(ItemStack stack) {
        return getStoredEntityType(stack)
                .map(entityType -> entityType.getDescription().getString())
                .orElse("Unknown");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ResourceLocation entityKey = stack.get(MFUDataComponents.ENTITY_DNA.get());

        if (stack.has(MFUDataComponents.ENTITY_DNA.get()) && entityKey != null) {
            String entityName = getStoredEntityName(stack);
            tooltipComponents.add(Component.translatable("item.mobflowutilities.gene_sample_vial.contains", entityName)
                    .withStyle(ChatFormatting.GREEN));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("item.mobflowutilities.gene_sample_vial.usage")
                    .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
        } else {
            tooltipComponents.add(Component.translatable("item.mobflowutilities.gene_sample_vial.empty")
                    .withStyle(ChatFormatting.RED));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasDNA(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return hasDNA(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return hasDNA(stack) ? 13 : 0;
    }
}