package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.component.custom.PadWrenchData;
import com.misterd.mobflowutilities.entity.custom.ControllerBlockEntity;
import com.misterd.mobflowutilities.entity.custom.DamagePadBlockEntity;
import com.misterd.mobflowutilities.item.custom.PadWrenchItem;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = "mobflowutilities", value = Dist.CLIENT)
public class PadWrenchWireframeRenderer {

    private record WireframeBox(AABB aabb, int color) {}

    private static final ContextKey<List<WireframeBox>> WIREFRAME_KEY = new ContextKey<>(
            Identifier.fromNamespaceAndPath("mobflowutilities", "pad_wrench_wireframe")
    );

    @SubscribeEvent
    public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack wrenchStack = getWrenchStack(mc);
        if (wrenchStack == null) return;

        Level level = event.getLevel();
        PadWrenchData data = wrenchStack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);
        List<WireframeBox> boxes = new ArrayList<>();

        if (data.selectedController() != null) {
            BlockEntity be = level.getBlockEntity(data.selectedController());
            if (be instanceof ControllerBlockEntity) {
                boxes.add(new WireframeBox(new AABB(data.selectedController()), ARGB.colorFromFloat(0.8f, 0f, 0.8f, 1f)));
            }
            if (be instanceof ControllerBlockEntity controller) {
                for (BlockPos padPos : controller.getLinkedPads()) {
                    BlockEntity padBe = level.getBlockEntity(padPos);
                    if (padBe instanceof DamagePadBlockEntity pad && pad.isLinked()) {
                        boxes.add(new WireframeBox(new AABB(
                                padPos.getX(), padPos.getY(), padPos.getZ(),
                                padPos.getX() + 1.0, padPos.getY() + 0.0625, padPos.getZ() + 1.0
                        ), ARGB.colorFromFloat(0.6f, 0f, 1f, 0f)));
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
                boxes.add(new WireframeBox(
                        new AABB(minX, minY, minZ, maxX + 1.0, maxY + 0.0625, maxZ + 1.0),
                        ARGB.colorFromFloat(0.6f, 1f, 1f, 1f)
                ));
            }
        }

        if (!boxes.isEmpty()) event.getRenderState().setRenderData(WIREFRAME_KEY, boxes);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        LevelRenderState renderState = event.getLevelRenderState();
        List<WireframeBox> boxes = renderState.getRenderData(WIREFRAME_KEY);
        if (boxes == null || boxes.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Vec3 camPos = mc.gameRenderer.getMainCamera().position();
        PoseStack poseStack = event.getPoseStack();
        var bufferSource = mc.renderBuffers().bufferSource();
        float lineWidth = mc.gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth;

        poseStack.pushPose();
        poseStack.translate(-camPos.x(), -camPos.y(), -camPos.z());

        VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.lines());
        for (WireframeBox box : boxes) {
            ShapeRenderer.renderShape(
                    poseStack, buffer,
                    Shapes.create(box.aabb().move(-camPos.x(), -camPos.y(), -camPos.z())),
                    0.0, 0.0, 0.0,
                    box.color(),
                    lineWidth
            );
        }
        bufferSource.endLastBatch();

        poseStack.popPose();
    }

    private static ItemStack getWrenchStack(Minecraft mc) {
        ItemStack main = mc.player.getMainHandItem();
        if (main.getItem() instanceof PadWrenchItem) return main;
        ItemStack off = mc.player.getOffhandItem();
        if (off.getItem() instanceof PadWrenchItem) return off;
        return null;
    }
}