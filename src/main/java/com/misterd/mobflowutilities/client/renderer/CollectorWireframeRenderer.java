package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class CollectorWireframeRenderer {

    private static boolean showWireframes = false;
    private static BlockPos activeCollectorPos = null;

    public static void toggleWireframe(BlockPos collectorPos) {
        if (activeCollectorPos != null && activeCollectorPos.equals(collectorPos)) {
            showWireframes = false;
            activeCollectorPos = null;
        } else {
            showWireframes = true;
            activeCollectorPos = collectorPos;
        }
    }

    public static boolean isWireframeActive(BlockPos collectorPos) {
        return showWireframes && activeCollectorPos != null && activeCollectorPos.equals(collectorPos);
    }

    public static void clearWireframes() {
        showWireframes = false;
        activeCollectorPos = null;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!showWireframes || activeCollectorPos == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;

        if (player == null || level == null) return;

        BlockEntity be = level.getBlockEntity(activeCollectorPos);
        if (be instanceof CollectorBlockEntity collector) {
            AABB pickupZone = calculatePickupZone(collector);
            renderWireframe(event.getPoseStack(), pickupZone, event.getCamera().getPosition());
        } else {
            clearWireframes();
        }
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

    private static void renderWireframe(PoseStack poseStack, AABB aabb, Vec3 cameraPos) {
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, buffer, aabb, 0f, 1f, 1f, 0.8f);

        bufferSource.endBatch(RenderType.lines());
        poseStack.popPose();
    }
}