package com.misterd.mobflowutilities.event;

import com.misterd.mobflowutilities.block.custom.DamagePadBlock;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.SpreadPlayersCommand;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.TeleportCommand;

@EventBusSubscriber
public class AttackPadTeleportInhibitor {
    private static final double INHIBIT_RANGE = 6.0D;

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onEntityTeleport(EntityTeleportEvent event) {
        if (!event.getEntity().level().isClientSide() && !(event instanceof TeleportCommand) && !(event instanceof SpreadPlayersCommand) && event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity)event.getEntity();
            if (shouldBlockTeleportation(entity) && isNearAttackPad(entity)) {
                event.setCanceled(true);
            }

        }
    }

    private static boolean shouldBlockTeleportation(LivingEntity entity) {
        if (entity instanceof EnderMan) {
            return true;
        } else {
            return entity instanceof Player ? false : false;
        }
    }

    private static boolean isNearAttackPad(LivingEntity entity) {
        AABB searchArea = entity.getBoundingBox().inflate(6.0D, 6.0D, 6.0D);
        int minX = Mth.floor(searchArea.minX);
        int maxX = Mth.floor(searchArea.maxX);
        int minY = Mth.floor(searchArea.minY);
        int maxY = Mth.floor(searchArea.maxY);
        int minZ = Mth.floor(searchArea.minZ);
        int maxZ = Mth.floor(searchArea.maxZ);
        MutableBlockPos mutablePos = new MutableBlockPos();

        for(int x = minX; x <= maxX; ++x) {
            for(int y = minY; y <= maxY; ++y) {
                for(int z = minZ; z <= maxZ; ++z) {
                    mutablePos.set(x, y, z);
                    BlockState state = entity.level().getBlockState(mutablePos);
                    if (state.getBlock() instanceof DamagePadBlock) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
