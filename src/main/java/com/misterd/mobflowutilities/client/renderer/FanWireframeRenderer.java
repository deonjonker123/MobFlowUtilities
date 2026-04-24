package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.entity.custom.FanBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = "mobflowutilities", value = Dist.CLIENT)
public class FanWireframeRenderer {

    private static final ContextKey<AABB> WIREFRAME_KEY = new ContextKey<>(
            Identifier.fromNamespaceAndPath("mobflowutilities", "fan_wireframe")
    );

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
    public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event) {
        if (!showWireframe || activeFanPos == null) return;

        Level level = event.getLevel();
        BlockEntity be = level.getBlockEntity(activeFanPos);

        if (be instanceof FanBlockEntity fan) {
            AABB pushZone = fan.getPushZone();
            if (pushZone != null) event.getRenderState().setRenderData(WIREFRAME_KEY, pushZone);
        } else {
            clearWireframes();
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        LevelRenderState renderState = event.getLevelRenderState();
        AABB zone = renderState.getRenderData(WIREFRAME_KEY);
        if (zone == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Vec3 camPos = mc.gameRenderer.getMainCamera().position();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camPos.x(), -camPos.y(), -camPos.z());

        var bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.lines());
        ShapeRenderer.renderShape(
                poseStack, buffer,
                Shapes.create(zone),
                0.0, 0.0, 0.0,
                ARGB.colorFromFloat(0.8f, 1.0f, 0.5f, 0.0f),
                mc.gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth
        );
        bufferSource.endLastBatch();
        poseStack.popPose();
    }
}