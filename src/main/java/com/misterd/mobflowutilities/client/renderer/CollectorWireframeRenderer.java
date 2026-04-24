package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = "mobflowutilities", value = Dist.CLIENT)
public class CollectorWireframeRenderer {

    private static final ContextKey<AABB> WIREFRAME_KEY = new ContextKey<>(
            Identifier.fromNamespaceAndPath("mobflowutilities", "collector_wireframe")
    );

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
    public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event) {
        if (!showWireframe || activeCollectorPos == null) return;

        Level level = event.getLevel();
        BlockEntity be = level.getBlockEntity(activeCollectorPos);

        if (be instanceof CollectorBlockEntity collector) {
            event.getRenderState().setRenderData(WIREFRAME_KEY, calculatePickupZone(collector));
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
                ARGB.colorFromFloat(0.8f, 0f, 1f, 1f),
                mc.gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth
        );
        bufferSource.endLastBatch();
        poseStack.popPose();
    }

    // --- Helper ---

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