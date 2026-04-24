package com.misterd.mobflowutilities.item.custom;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class EmptyGeneVialItem extends Item {
    public EmptyGeneVialItem(Properties properties) {
        super(properties);
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("item.mobflowutilities.empty_gene_vial.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
        adder.accept(Component.literal(""));
        adder.accept(Component.translatable("item.mobflowutilities.empty_gene_vial.usage").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, display, adder, flag);
    }
}
