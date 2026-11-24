package com.misterd.mobflowutilities.client.renderer.ber;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.custom.FanBlock;
import com.misterd.mobflowutilities.blockentity.custom.FanBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FanBlockEntityRenderer implements BlockEntityRenderer<FanBlockEntity, FanBlockEntityRenderer.RenderState> {

    public static final Identifier FAN_PROP_MODEL = Identifier.fromNamespaceAndPath(MobFlowUtilities.MODID, "block/fan_prop");
    public static final StandaloneModelKey<BlockStateModelPart> FAN_PROP = new StandaloneModelKey<>(() -> FAN_PROP_MODEL.toString());

    private static final float DEGREES_PER_TICK = 18.0F;
    private static final int TICKS_PER_ROTATION = 20;
    private static final int[] NO_TINT = new int[0];

    public FanBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public static class RenderState extends BlockEntityRenderState {
        Direction facing = Direction.NORTH;
        float angle = 0.0F;
        @Nullable BlockStateModelPart prop = null;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(FanBlockEntity blockEntity, RenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);

        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.hasProperty(FanBlock.FACING) ? blockState.getValue(FanBlock.FACING) : Direction.NORTH;

        boolean powered = blockState.hasProperty(FanBlock.POWERED) && blockState.getValue(FanBlock.POWERED);
        Level level = blockEntity.getLevel();
        if (powered && level != null) {
            float ticks = (level.getGameTime() % TICKS_PER_ROTATION) + partialTick;
            state.angle = (ticks * DEGREES_PER_TICK) % 360.0F;
        } else {
            state.angle = 0.0F;
        }

        state.prop = Minecraft.getInstance().getModelManager().getStandaloneModel(FAN_PROP);
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.prop == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        switch (state.facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            default -> {}
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.angle));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        collector.submitBlockModel(poseStack, RenderTypes.cutoutMovingBlock(), List.of(state.prop), NO_TINT, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}