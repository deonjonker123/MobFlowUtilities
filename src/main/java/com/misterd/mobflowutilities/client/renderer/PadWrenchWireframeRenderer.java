package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.component.custom.PadWrenchData;
import com.misterd.mobflowutilities.entity.custom.ControllerBlockEntity;
import com.misterd.mobflowutilities.entity.custom.DamagePadBlockEntity;
import com.misterd.mobflowutilities.item.custom.PadWrenchItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class PadWrenchWireframeRenderer {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;

        if (player == null || level == null) return;

        ItemStack wrenchStack = player.getMainHandItem().getItem() instanceof PadWrenchItem
                ? player.getMainHandItem()
                : player.getOffhandItem().getItem() instanceof PadWrenchItem
                ? player.getOffhandItem()
                : null;

        if (wrenchStack == null) return;

        PadWrenchData data = wrenchStack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        if (data.selectedController() != null) {
            renderControllerWireframe(poseStack, cameraPos, data.selectedController(), level);

            BlockEntity be = level.getBlockEntity(data.selectedController());
            if (be instanceof ControllerBlockEntity controller) {
                renderLinkedPadsWireframe(poseStack, cameraPos, controller.getLinkedPads(), level);
            }
        }

        if (data.firstMultiPos() != null && data.selectionMode() == PadWrenchData.SelectionMode.MULTI) {
            renderMultiSelectionWireframe(poseStack, cameraPos, data.firstMultiPos(), level, minecraft);
        }
    }

    private static void renderControllerWireframe(PoseStack poseStack, Vec3 cameraPos, BlockPos controllerPos, Level level) {
        if (level.getBlockEntity(controllerPos) instanceof ControllerBlockEntity) {
            AABB aabb = new AABB(controllerPos);
            renderWireframeBox(poseStack, cameraPos, aabb, 0f, 0.8f, 1f, 0.8f);
        }
    }

    private static void renderLinkedPadsWireframe(PoseStack poseStack, Vec3 cameraPos, Set<BlockPos> linkedPads, Level level) {
        for (BlockPos padPos : linkedPads) {
            BlockEntity be = level.getBlockEntity(padPos);
            if (be instanceof DamagePadBlockEntity attackPad && attackPad.isLinked()) {
                AABB aabb = new AABB(
                        padPos.getX(), padPos.getY(), padPos.getZ(),
                        padPos.getX() + 1.0, padPos.getY() + 0.0625, padPos.getZ() + 1.0
                );
                renderWireframeBox(poseStack, cameraPos, aabb, 0f, 1f, 0f, 0.6f);
            }
        }
    }

    private static void renderMultiSelectionWireframe(PoseStack poseStack, Vec3 cameraPos, BlockPos firstPos, Level level, Minecraft minecraft) {
        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return;

        BlockPos currentPos = blockHitResult.getBlockPos();
        int minX = Math.min(firstPos.getX(), currentPos.getX());
        int maxX = Math.max(firstPos.getX(), currentPos.getX());
        int minY = Math.min(firstPos.getY(), currentPos.getY());
        int maxY = Math.max(firstPos.getY(), currentPos.getY());
        int minZ = Math.min(firstPos.getZ(), currentPos.getZ());
        int maxZ = Math.max(firstPos.getZ(), currentPos.getZ());

        AABB selectionArea = new AABB(minX, minY, minZ, maxX + 1.0, maxY + 0.0625, maxZ + 1.0);
        renderWireframeBox(poseStack, cameraPos, selectionArea, 1f, 1f, 1f, 0.6f);
    }

    private static void renderWireframeBox(PoseStack poseStack, Vec3 cameraPos, AABB aabb, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Minecraft minecraft = Minecraft.getInstance();
        BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, buffer, aabb, red, green, blue, alpha);
        bufferSource.endBatch(RenderType.lines());

        poseStack.popPose();
    }
}