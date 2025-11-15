package com.misterd.mobflowutilities.item.custom;

import com.misterd.mobflowutilities.gui.custom.VoidFilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class VoidFilterItem extends Item {

    public VoidFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {

            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inv, p) -> new VoidFilterMenu(containerId, inv, stack),
                            Component.translatable("gui.mobflowutilities.void_filter")
                    )
            );
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(
                Component.translatable("item.mobflowutilities.void_filter_module.subtitle")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
        );

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}