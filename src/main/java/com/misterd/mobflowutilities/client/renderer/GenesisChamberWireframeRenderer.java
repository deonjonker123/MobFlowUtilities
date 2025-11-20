package com.misterd.mobflowutilities.client.renderer;

import com.misterd.mobflowutilities.entity.custom.GenesisChamberBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
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
public class GenesisChamberWireframeRenderer {

    private static boolean showWireframes = false;
    private static BlockPos activeGenesisChamberPos = null;

    public static void toggleWireframe(BlockPos genesisChamberPos) {
        if (activeGenesisChamberPos != null && activeGenesisChamberPos.equals(genesisChamberPos)) {
            showWireframes = false;
            activeGenesisChamberPos = null;
        } else {
            showWireframes = true;
            activeGenesisChamberPos = genesisChamberPos;
        }
    }

    public static boolean isWireframeActive(BlockPos genesisChamberPos) {
        return showWireframes && activeGenesisChamberPos != null && activeGenesisChamberPos.equals(genesisChamberPos);
    }

    public static void clearWireframes() {
        showWireframes = false;
        activeGenesisChamberPos = null;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!showWireframes || activeGenesisChamberPos == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;

        if (player == null || level == null) return;

        BlockEntity be = level.getBlockEntity(activeGenesisChamberPos);
        if (be instanceof GenesisChamberBlockEntity genesisChamber) {
            AABB spawnZone = calculateSpawnZone(genesisChamber);
            renderWireframe(event.getPoseStack(), spawnZone, event.getCamera().getPosition());
        } else {
            clearWireframes();
        }
    }

    private static AABB calculateSpawnZone(GenesisChamberBlockEntity genesisChamber) {
        BlockPos pos = genesisChamber.getBlockPos();

        ItemStack radiusModules = genesisChamber.inventory.getStackInSlot(3);
        int moduleCount = Math.min(5, radiusModules.getCount());
        int radius = 2 + moduleCount;

        double minX = pos.getX() - radius + genesisChamber.getEastWestOffset();
        double minY = pos.getY() + genesisChamber.getDownUpOffset();
        double minZ = pos.getZ() - radius + genesisChamber.getNorthSouthOffset();

        double maxX = pos.getX() + radius + 1 + genesisChamber.getEastWestOffset();
        double maxY = pos.getY() + 5.0 + genesisChamber.getDownUpOffset();
        double maxZ = pos.getZ() + radius + 1 + genesisChamber.getNorthSouthOffset();

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static void renderWireframe(PoseStack poseStack, AABB aabb, Vec3 cameraPos) {
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, buffer, aabb, 0.8f, 0.4f, 1.0f, 0.8f);

        bufferSource.endBatch(RenderType.lines());
        poseStack.popPose();
    }
}