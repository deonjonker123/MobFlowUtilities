package com.misterd.mobflowutilities.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class EntityTickingHelper {

    private EntityTickingHelper() {}

    public static boolean isTicking(ServerLevel level, Entity entity) {
        return entity instanceof Player || level.isPositionEntityTicking(entity.blockPosition());
    }
}