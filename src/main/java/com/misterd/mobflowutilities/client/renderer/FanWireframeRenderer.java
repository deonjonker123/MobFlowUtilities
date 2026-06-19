package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.entity.custom.FanBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "mobflowutilities", value = Dist.CLIENT)
public class FanWireframeRenderer {

    private static boolean showWireframe = false;
    private static BlockPos activeFanPos = null;

    public static void toggleWireframe(BlockPos fanPos) {
        if (activeFanPos != null && activeFanPos.equals(fanPos)) {
            showWireframe = false;
            activeFanPos = null;
        } else {
            showWireframe = true;
            activeFanPos = fanPos;
        }
    }

    public static boolean isWireframeActive(BlockPos fanPos) {
        return showWireframe && activeFanPos != null && activeFanPos.equals(fanPos);
    }

    public static void clearWireframes() {
        showWireframe = false;
        activeFanPos = null;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!showWireframe || activeFanPos == null) return;

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        BlockEntity be = level.getBlockEntity(activeFanPos);
        if (!(be instanceof FanBlockEntity fan)) {
            clearWireframes();
            return;
        }

        AABB pushZone = fan.getPushZone();
        if (pushZone == null) return;

        Gizmos.cuboid(pushZone, GizmoStyle.stroke(ARGB.colorFromFloat(0.8f, 1.0f, 0.5f, 0.0f)));
    }
}