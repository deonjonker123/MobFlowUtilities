package com.misterd.mobflowutilities.event;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.item.custom.EmptyGeneVialItem;
import com.misterd.mobflowutilities.item.custom.MobCatcherItem;
import com.misterd.mobflowutilities.util.GeneticHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;

@EventBusSubscriber(modid = MobFlowUtilities.MODID)
public class MobCatcherEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(EntityInteract event) {

        Entity target = event.getTarget();
        if (target == null || target instanceof EnderDragon) return;

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && stack.getItem() instanceof EmptyGeneVialItem) {

            if (target instanceof LivingEntity living) {
                if (handleGeneVialInteraction(stack, player, living, hand)) {
                    event.setCanceled(true);
                }
            }
            return;
        }

        if (player.isShiftKeyDown() && stack.getItem() instanceof MobCatcherItem catcher) {

            if (target instanceof LivingEntity living) {

                if (living instanceof EnderDragon) {
                    event.setCanceled(true);
                    return;
                }

                InteractionResult result =
                        catcher.interactLivingEntity(stack, player, living, hand);

                if (result.consumesAction()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private static boolean handleGeneVialInteraction(
            ItemStack stack,
            Player player,
            LivingEntity target,
            InteractionHand hand
    ) {
        if (player.level().isClientSide()) return true;

        if (!GeneticHelper.canCollectDNA(target.getType())) {

            if (target instanceof Player) {
                player.sendSystemMessage(Component.translatable("item.mobflowutilities.empty_gene_vial.player_immunity"));
            } else if (GeneticHelper.getSpawnEgg(target.getType()) == null) {
                player.sendSystemMessage(Component.translatable("item.mobflowutilities.empty_gene_vial.no_spawn_egg"));
            } else {
                player.sendSystemMessage(Component.translatable("item.mobflowutilities.empty_gene_vial.blacklisted"));
            }

            return true;
        }

        Identifier entityKey = GeneticHelper.getEntityKey(target.getType());
        ItemStack filledVial = new ItemStack(MFUItems.GENE_SAMPLE_VIAL.get());
        filledVial.set(MFUDataComponents.ENTITY_DNA.get(), entityKey);

        player.setItemInHand(hand, filledVial);

        player.sendSystemMessage(Component.translatable("item.mobflowutilities.empty_gene_vial.collected", target.getDisplayName().getString()));

        return true;
    }
}