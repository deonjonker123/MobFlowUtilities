package com.misterd.mobflowutilities.item.custom;

import com.misterd.mobflowutilities.gui.custom.VoidFilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class VoidFilterItem extends Item {

    public VoidFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {

            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inv, p) -> new VoidFilterMenu(containerId, inv, stack),
                            Component.translatable("gui.mobflowutilities.void_filter")
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("item.mobflowutilities.void_filter_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));

        super.appendHoverText(stack, context, display, adder, flag);
    }
}