package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.entity.custom.GenesisChamberBlockEntity;
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
public class GenesisChamberWireframeRenderer {

    private static final ContextKey<AABB> WIREFRAME_KEY = new ContextKey<>(
            Identifier.fromNamespaceAndPath("mobflowutilities", "genesis_chamber_wireframe")
    );

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
    public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event) {
        if (!showWireframe || activeGenesisChamberPos == null) return;

        Level level = event.getLevel();
        BlockEntity be = level.getBlockEntity(activeGenesisChamberPos);

        if (be instanceof GenesisChamberBlockEntity gc) {
            event.getRenderState().setRenderData(WIREFRAME_KEY, calculateSpawnZone(gc));
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
                ARGB.colorFromFloat(0.8f, 0.8f, 0.4f, 1.0f),
                mc.gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth
        );
        bufferSource.endLastBatch();
        poseStack.popPose();
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