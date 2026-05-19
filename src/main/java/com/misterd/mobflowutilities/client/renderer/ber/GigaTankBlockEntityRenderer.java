package com.misterd.mobflowutilities.client.renderer.ber;

import com.misterd.mobflowutilities.entity.custom.GigaTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class GigaTankBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, GigaTankBlockEntityRenderer.RenderState> {

    private static final float INSET = 0.01f;
    private static final float MIN_X = 0.0625f + INSET;
    private static final float MAX_X = 0.9375f - INSET;
    private static final float MIN_Z = 0.0625f + INSET;
    private static final float MAX_Z = 0.9375f - INSET;
    private static final float MIN_Y = 0.0625f + INSET;
    private static final float MAX_Y_FULL = 0.9375f - INSET;

    public GigaTankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public static class RenderState extends BlockEntityRenderState {
        @Nullable Fluid fluid = null;
        float fillPercentage = 0f;
        int tintColor = -1;
        @Nullable TextureAtlasSprite sprite = null;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, RenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
        state.fluid = null;
        state.fillPercentage = 0f;
        state.tintColor = -1;
        state.sprite = null;

        FluidStacksResourceHandler tank = getTank(blockEntity);
        if (tank == null) return;

        FluidResource res = tank.getResource(0);
        if (res.isEmpty()) return;

        int amount = tank.getAmountAsInt(0);
        if (amount <= 0) return;

        Fluid fluid = res.getFluid();
        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
        FluidTintSource tintSource = model.fluidTintSource();

        state.fluid = fluid;
        state.fillPercentage = (float) amount / tank.getCapacityAsInt(0, res);
        state.tintColor = tintSource != null ? tintSource.color(fluid.defaultFluidState()) : -1;
        state.sprite = model.stillMaterial().sprite();
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.fluid == null || state.sprite == null || state.fillPercentage <= 0f) return;

        float x0 = MIN_X, x1 = MAX_X;
        float z0 = MIN_Z, z1 = MAX_Z;
        float y0 = MIN_Y;
        float y1 = MIN_Y + (MAX_Y_FULL - MIN_Y) * state.fillPercentage;

        int color = state.tintColor;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        int rawA = (color >> 24) & 0xFF;
        float a = rawA == 0 ? 1f : rawA / 255f;

        TextureAtlasSprite s = state.sprite;
        float u0 = s.getU0(), u1 = s.getU1();
        float v0 = s.getV0(), v1 = s.getV1();

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, consumer) -> {
            Matrix4f m = pose.pose();
            int ov = OverlayTexture.NO_OVERLAY;
            int li = state.lightCoords;

            quad(consumer, m, r, g, b, a, ov, li,
                    x0, y1, z0, u0, v0,
                    x0, y1, z1, u0, v1,
                    x1, y1, z1, u1, v1,
                    x1, y1, z0, u1, v0,
                    0, 1, 0);

            quad(consumer, m, r, g, b, a, ov, li,
                    x0, y0, z0, u0, v0,
                    x1, y0, z0, u1, v0,
                    x1, y0, z1, u1, v1,
                    x0, y0, z1, u0, v1,
                    0, -1, 0);

            quad(consumer, m, r, g, b, a, ov, li,
                    x0, y0, z0, u0, v0,
                    x0, y1, z0, u0, v1,
                    x1, y1, z0, u1, v1,
                    x1, y0, z0, u1, v0,
                    0, 0, -1);

            quad(consumer, m, r, g, b, a, ov, li,
                    x1, y0, z1, u0, v0,
                    x1, y1, z1, u0, v1,
                    x0, y1, z1, u1, v1,
                    x0, y0, z1, u1, v0,
                    0, 0, 1);

            quad(consumer, m, r, g, b, a, ov, li,
                    x0, y0, z1, u0, v0,
                    x0, y1, z1, u0, v1,
                    x0, y1, z0, u1, v1,
                    x0, y0, z0, u1, v0,
                    -1, 0, 0);

            quad(consumer, m, r, g, b, a, ov, li,
                    x1, y0, z0, u0, v0,
                    x1, y1, z0, u0, v1,
                    x1, y1, z1, u1, v1,
                    x1, y0, z1, u1, v0,
                    1, 0, 0);
        });
    }

    private static void quad(VertexConsumer c, Matrix4f m, float r, float g, float b, float a, int ov, int li,
                             float x0, float y0, float z0, float u0, float v0,
                             float x1, float y1, float z1, float u1, float v1,
                             float x2, float y2, float z2, float u2, float v2,
                             float x3, float y3, float z3, float u3, float v3,
                             float nx, float ny, float nz) {
        v(c, m, x0, y0, z0, r, g, b, a, u0, v0, ov, li, nx, ny, nz);
        v(c, m, x1, y1, z1, r, g, b, a, u1, v1, ov, li, nx, ny, nz);
        v(c, m, x2, y2, z2, r, g, b, a, u2, v2, ov, li, nx, ny, nz);
        v(c, m, x3, y3, z3, r, g, b, a, u3, v3, ov, li, nx, ny, nz);
    }

    private static void v(VertexConsumer c, Matrix4f m, float x, float y, float z, float r, float g, float b, float a, float u, float v, int ov, int li, float nx, float ny, float nz) {
        c.addVertex(m, x, y, z).setColor(r, g, b, a).setUv(u, v).setOverlay(ov).setLight(li).setNormal(nx, ny, nz);
    }

    @Nullable
    private FluidStacksResourceHandler getTank(T blockEntity) {
        if (blockEntity instanceof GigaTankBlockEntity e) return e.tank;
        return null;
    }
}