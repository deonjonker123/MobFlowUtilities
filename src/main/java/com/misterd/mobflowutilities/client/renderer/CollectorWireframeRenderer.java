package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.blockentity.custom.CollectorBlockEntity;
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
public class CollectorWireframeRenderer {

    private static boolean showWireframe = false;
    private static BlockPos activeCollectorPos = null;

    public static void toggleWireframe(BlockPos collectorPos) {
        if (activeCollectorPos != null && activeCollectorPos.equals(collectorPos)) {
            showWireframe = false;
            activeCollectorPos = null;
        } else {
            showWireframe = true;
            activeCollectorPos = collectorPos;
        }
    }

    public static boolean isWireframeActive(BlockPos collectorPos) {
        return showWireframe && activeCollectorPos != null && activeCollectorPos.equals(collectorPos);
    }

    public static void clearWireframes() {
        showWireframe = false;
        activeCollectorPos = null;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!showWireframe || activeCollectorPos == null) return;

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        BlockEntity be = level.getBlockEntity(activeCollectorPos);
        if (!(be instanceof CollectorBlockEntity collector)) {
            clearWireframes();
            return;
        }

        AABB zone = calculatePickupZone(collector);
        Gizmos.cuboid(zone, GizmoStyle.stroke(ARGB.colorFromFloat(0.8f, 0f, 1f, 1f)));
    }

    private static AABB calculatePickupZone(CollectorBlockEntity collector) {
        BlockPos pos = collector.getBlockPos();
        int range = collector.getPickupRange();
        return new AABB(
                pos.getX() - range + collector.getEastWestOffset(),
                pos.getY() - range + collector.getDownUpOffset(),
                pos.getZ() - range + collector.getNorthSouthOffset(),
                pos.getX() + range + 1 + collector.getEastWestOffset(),
                pos.getY() + range + 1 + collector.getDownUpOffset(),
                pos.getZ() + range + 1 + collector.getNorthSouthOffset()
        );
    }
}