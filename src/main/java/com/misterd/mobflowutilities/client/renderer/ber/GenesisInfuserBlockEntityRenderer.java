package com.misterd.mobflowutilities.client.renderer.ber;

import com.misterd.mobflowutilities.blockentity.custom.GenesisInfuserBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class GenesisInfuserBlockEntityRenderer implements BlockEntityRenderer<GenesisInfuserBlockEntity> {

    private static final float INSET = 0.01f;
    private static final float MIN_X = 0.25f + INSET;
    private static final float MAX_X = 0.75f - INSET;
    private static final float MIN_Z = 0.25f + INSET;
    private static final float MAX_Z = 0.75f - INSET;
    private static final float MIN_Y = 0.5f + INSET;
    private static final float MAX_Y_FULL = 0.875f - INSET;

    public GenesisInfuserBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GenesisInfuserBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        FluidStack fluid = blockEntity.getFluid();
        if (fluid.isEmpty() || fluid.getAmount() <= 0) return;

        float fill = Math.min(1f, (float) fluid.getAmount() / GenesisInfuserBlockEntity.TANK_CAPACITY);

        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(extensions.getStillTexture(fluid));

        int color = extensions.getTintColor(fluid);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        int rawA = (color >> 24) & 0xFF;
        float a = rawA == 0 ? 1f : rawA / 255f;

        float x0 = MIN_X;
        float x1 = MAX_X;
        float z0 = MIN_Z;
        float z1 = MAX_Z;
        float y0 = MIN_Y;
        float y1 = MIN_Y + (MAX_Y_FULL - MIN_Y) * fill;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        VertexConsumer c = bufferSource.getBuffer(RenderType.translucentMovingBlock());
        Matrix4f m = poseStack.last().pose();
        int ov = combinedOverlay;
        int li = combinedLight;

        quad(c, m, r, g, b, a, ov, li,
                x0, y1, z0, u0, v0,
                x0, y1, z1, u0, v1,
                x1, y1, z1, u1, v1,
                x1, y1, z0, u1, v0,
                0, 1, 0);

        quad(c, m, r, g, b, a, ov, li,
                x0, y0, z0, u0, v0,
                x1, y0, z0, u1, v0,
                x1, y0, z1, u1, v1,
                x0, y0, z1, u0, v1,
                0, -1, 0);

        quad(c, m, r, g, b, a, ov, li,
                x0, y0, z0, u0, v0,
                x0, y1, z0, u0, v1,
                x1, y1, z0, u1, v1,
                x1, y0, z0, u1, v0,
                0, 0, -1);

        quad(c, m, r, g, b, a, ov, li,
                x1, y0, z1, u0, v0,
                x1, y1, z1, u0, v1,
                x0, y1, z1, u1, v1,
                x0, y0, z1, u1, v0,
                0, 0, 1);

        quad(c, m, r, g, b, a, ov, li,
                x0, y0, z1, u0, v0,
                x0, y1, z1, u0, v1,
                x0, y1, z0, u1, v1,
                x0, y0, z0, u1, v0,
                -1, 0, 0);

        quad(c, m, r, g, b, a, ov, li,
                x1, y0, z0, u0, v0,
                x1, y1, z0, u0, v1,
                x1, y1, z1, u1, v1,
                x1, y0, z1, u1, v0,
                1, 0, 0);
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
}
