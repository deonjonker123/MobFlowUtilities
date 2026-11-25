package com.misterd.mobflowutilities.client.renderer.ber;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.custom.FanBlock;
import com.misterd.mobflowutilities.blockentity.custom.FanBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class FanBlockEntityRenderer implements BlockEntityRenderer<FanBlockEntity> {

    public static final ModelResourceLocation FAN_PROP = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "block/fan_prop"));

    private static final float DEGREES_PER_TICK = 18.0F;
    private static final int TICKS_PER_ROTATION = 20;

    public FanBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FanBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BakedModel prop = Minecraft.getInstance().getModelManager().getModel(FAN_PROP);
        if (prop == null) return;

        BlockState blockState = blockEntity.getBlockState();
        Direction facing = blockState.hasProperty(FanBlock.FACING) ? blockState.getValue(FanBlock.FACING) : Direction.NORTH;
        boolean powered = blockState.hasProperty(FanBlock.POWERED) && blockState.getValue(FanBlock.POWERED);
        boolean reversed = blockState.hasProperty(FanBlock.REVERSED) && blockState.getValue(FanBlock.REVERSED);

        float angle = 0.0F;
        Level level = blockEntity.getLevel();
        if (powered && level != null) {
            float ticks = (level.getGameTime() % TICKS_PER_ROTATION) + partialTick;
            angle = (ticks * DEGREES_PER_TICK) % 360.0F;
            if (reversed) angle = -angle;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        switch (facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            default -> {}
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.cutout()),
                null,
                prop,
                1.0F, 1.0F, 1.0F,
                combinedLight,
                combinedOverlay,
                ModelData.EMPTY,
                RenderType.cutout()
        );

        poseStack.popPose();
    }
}
