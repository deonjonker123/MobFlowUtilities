package com.misterd.mobflowutilities.event;

import java.util.HashSet;
import java.util.Set;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.item.equipment.GloomsteelHammerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;

@EventBusSubscriber(modid = MobFlowUtilities.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GloomsteelHammerEventHandler {

    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

    @SubscribeEvent
    public static void onHammerUsage(BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();
        Item item = mainHandItem.getItem();

        if (!(item instanceof GloomsteelHammerItem hammer)) {
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        BlockPos initialBlockPos = event.getPos();
        if (HARVESTED_BLOCKS.contains(initialBlockPos)) {
            return;
        }

        for (BlockPos pos : GloomsteelHammerItem.getBlocksToBeDestroyed(1, initialBlockPos, serverPlayer)) {
            if (!pos.equals(initialBlockPos) && hammer.isCorrectToolForDrops(mainHandItem, event.getLevel().getBlockState(pos))) {
                HARVESTED_BLOCKS.add(pos);
                serverPlayer.gameMode.destroyBlock(pos);
                HARVESTED_BLOCKS.remove(pos);
            }
        }
    }
}