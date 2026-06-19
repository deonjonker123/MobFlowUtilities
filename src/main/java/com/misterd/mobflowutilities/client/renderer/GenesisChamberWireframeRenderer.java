package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.entity.custom.GenesisChamberBlockEntity;
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
public class GenesisChamberWireframeRenderer {

    private static boolean showWireframe = false;
    private static BlockPos activeGenesisChamberPos = null;

    public static void toggleWireframe(BlockPos genesisChamberPos) {
        if (activeGenesisChamberPos != null && activeGenesisChamberPos.equals(genesisChamberPos)) {
            showWireframe = false;
            activeGenesisChamberPos = null;
        } else {
            showWireframe = true;
            activeGenesisChamberPos = genesisChamberPos;
        }
    }

    public static boolean isWireframeActive(BlockPos genesisChamberPos) {
        return showWireframe && activeGenesisChamberPos != null && activeGenesisChamberPos.equals(genesisChamberPos);
    }

    public static void clearWireframes() {
        showWireframe = false;
        activeGenesisChamberPos = null;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!showWireframe || activeGenesisChamberPos == null) return;

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        BlockEntity be = level.getBlockEntity(activeGenesisChamberPos);
        if (!(be instanceof GenesisChamberBlockEntity gc)) {
            clearWireframes();
            return;
        }

        AABB zone = calculateSpawnZone(gc);
        Gizmos.cuboid(zone, GizmoStyle.stroke(ARGB.colorFromFloat(0.8f, 0.8f, 0.4f, 1.0f)));
    }

    private static AABB calculateSpawnZone(GenesisChamberBlockEntity gc) {
        BlockPos pos = gc.getBlockPos();
        int moduleCount = Math.min(5, gc.getStack(3).getCount());
        int radius = 2 + moduleCount;
        return new AABB(
                pos.getX() - radius + gc.getEastWestOffset(),
                pos.getY() + gc.getDownUpOffset(),
                pos.getZ() - radius + gc.getNorthSouthOffset(),
                pos.getX() + radius + 1 + gc.getEastWestOffset(),
                pos.getY() + 5.0 + gc.getDownUpOffset(),
                pos.getZ() + radius + 1 + gc.getNorthSouthOffset()
        );
    }
}