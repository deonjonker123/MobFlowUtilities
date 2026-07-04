package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.component.custom.PadWrenchData;
import com.misterd.mobflowutilities.blockentity.custom.ControllerBlockEntity;
import com.misterd.mobflowutilities.blockentity.custom.DamagePadBlockEntity;
import com.misterd.mobflowutilities.item.custom.PadWrenchItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "mobflowutilities", value = Dist.CLIENT)
public class PadWrenchWireframeRenderer {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack wrenchStack = getWrenchStack(mc);
        if (wrenchStack == null) return;

        Level level = mc.level;
        if (level == null) return;

        PadWrenchData data = wrenchStack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);

        if (data.selectedController() != null) {
            BlockEntity be = level.getBlockEntity(data.selectedController());
            if (be instanceof ControllerBlockEntity) {
                Gizmos.cuboid(new AABB(data.selectedController()),
                        GizmoStyle.stroke(ARGB.colorFromFloat(0.8f, 0f, 0.8f, 1f)));
            }
            if (be instanceof ControllerBlockEntity controller) {
                for (BlockPos padPos : controller.getLinkedPads()) {
                    BlockEntity padBe = level.getBlockEntity(padPos);
                    if (padBe instanceof DamagePadBlockEntity pad && pad.isLinked()) {
                        AABB padBox = new AABB(
                                padPos.getX(), padPos.getY(), padPos.getZ(),
                                padPos.getX() + 1.0, padPos.getY() + 0.0625, padPos.getZ() + 1.0
                        );
                        Gizmos.cuboid(padBox, GizmoStyle.stroke(ARGB.colorFromFloat(0.6f, 0f, 1f, 0f)));
                    }
                }
            }
        }

        if (data.firstMultiPos() != null && data.selectionMode() == PadWrenchData.SelectionMode.MULTI) {
            HitResult hitResult = mc.hitResult;
            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos first = data.firstMultiPos();
                BlockPos current = blockHitResult.getBlockPos();
                int minX = Math.min(first.getX(), current.getX());
                int maxX = Math.max(first.getX(), current.getX());
                int minY = Math.min(first.getY(), current.getY());
                int maxY = Math.max(first.getY(), current.getY());
                int minZ = Math.min(first.getZ(), current.getZ());
                int maxZ = Math.max(first.getZ(), current.getZ());
                AABB previewBox = new AABB(minX, minY, minZ, maxX + 1.0, maxY + 0.0625, maxZ + 1.0);
                Gizmos.cuboid(previewBox, GizmoStyle.stroke(ARGB.colorFromFloat(0.6f, 1f, 1f, 1f)));
            }
        }
    }

    private static ItemStack getWrenchStack(Minecraft mc) {
        ItemStack main = mc.player.getMainHandItem();
        if (main.getItem() instanceof PadWrenchItem) return main;
        ItemStack off = mc.player.getOffhandItem();
        if (off.getItem() instanceof PadWrenchItem) return off;
        return null;
    }
}