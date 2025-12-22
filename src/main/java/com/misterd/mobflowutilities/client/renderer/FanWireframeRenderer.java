package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.entity.custom.FanBlockEntity;
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
public class FanWireframeRenderer {

    private static boolean showWireframes = false;
    private static BlockPos activeFanPos = null;

    public static void toggleWireframe(BlockPos fanPos) {
        if (activeFanPos != null && activeFanPos.equals(fanPos)) {
            showWireframes = false;
            activeFanPos = null;
        } else {
            showWireframes = true;
            activeFanPos = fanPos;
        }
    }

    public static boolean isWireframeActive(BlockPos fanPos) {
        return showWireframes && activeFanPos != null && activeFanPos.equals(fanPos);
    }

    public static void clearWireframes() {
        showWireframes = false;
        activeFanPos = null;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!showWireframes || activeFanPos == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;

        if (player == null || level == null) return;

        BlockEntity be = level.getBlockEntity(activeFanPos);
        if (be instanceof FanBlockEntity fan) {
            AABB pushZone = fan.getPushZone();
            if (pushZone != null) {
                renderWireframe(event.getPoseStack(), pushZone, event.getCamera().getPosition());
            }
        } else {
            clearWireframes();
        }
    }

    private static void renderWireframe(PoseStack poseStack, AABB aabb, Vec3 cameraPos) {
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, buffer, aabb, 1.0f, 0.5f, 0.0f, 0.8f);

        bufferSource.endBatch(RenderType.lines());
        poseStack.popPose();
    }
}