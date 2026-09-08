package com.misterd.mobflowutilities.client.renderer.ber;

import com.misterd.mobflowutilities.blockentity.custom.GigaTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class GigaTankBlockEntityRenderer <T extends BlockEntity> implements BlockEntityRenderer<T> {

    public GigaTankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        FluidTank tank = getTank(blockEntity);
        if (tank == null || tank.isEmpty()) {
            return;
        }

        FluidStack fluidStack = tank.getFluid();
        if (fluidStack.isEmpty()) {
            return;
        }

        int capacity = tank.getCapacity();
        int amount = tank.getFluidAmount();
        float fillPercentage = (float) amount / capacity;

        float minX = 0.025f;
        float minY = 0.0625f;
        float minZ = 0.025f;
        float maxX = 0.975f;
        float maxZ = 0.975f;

        float fluidHeight = minY + (0.9375f - minY) * fillPercentage;

        renderFluid(fluidStack.getFluid(), poseStack, bufferSource,
                minX, minY, minZ, maxX, fluidHeight, maxZ,
                combinedLight, combinedOverlay);
    }

    private FluidTank getTank(T blockEntity) {
        if (blockEntity instanceof GigaTankBlockEntity giga) {
            return giga.tank;
        }
        return null;
    }

    private void renderFluid(Fluid fluid, PoseStack poseStack, MultiBufferSource bufferSource, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int combinedLight, int combinedOverlay) {

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite stillTexture = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidTypeExtensions.getStillTexture());

        int color = fluidTypeExtensions.getTintColor();
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        float alpha = ((color >> 24) & 0xFF) / 255f;

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucentMovingBlock());
        Matrix4f matrix = poseStack.last().pose();

        float u0 = stillTexture.getU0();
        float u1 = stillTexture.getU1();
        float v0 = stillTexture.getV0();
        float v1 = stillTexture.getV1();

        float uWidth = u1 - u0;
        float vHeight = v1 - v0;

        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 1, 0);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight * (maxZ - minZ)).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 1, 0);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxX - minX), v0 + vHeight * (maxZ - minZ)).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 1, 0);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxX - minX), v0).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 1, 0);

        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, -1, 0);
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxX - minX), v0).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, -1, 0);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxX - minX), v0 + vHeight * (maxZ - minZ)).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, -1, 0);
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight * (maxZ - minZ)).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, -1, 0);

        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight * (1 - (maxY - minY))).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 0, -1);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 0, -1);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxX - minX), v0 + vHeight).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 0, -1);
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxX - minX), v0 + vHeight * (1 - (maxY - minY))).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 0, -1);

        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight * (1 - (maxY - minY))).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 0, 1);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxX - minX), v0 + vHeight * (1 - (maxY - minY))).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 0, 1);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxX - minX), v0 + vHeight).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 0, 1);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 0, 1);

        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight * (1 - (maxY - minY))).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(-1, 0, 0);
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxZ - minZ), v0 + vHeight * (1 - (maxY - minY))).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(-1, 0, 0);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxZ - minZ), v0 + vHeight).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(-1, 0, 0);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(-1, 0, 0);

        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight * (1 - (maxY - minY))).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(1, 0, 0);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha)
                .setUv(u0, v0 + vHeight).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(1, 0, 0);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxZ - minZ), v0 + vHeight).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(1, 0, 0);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha)
                .setUv(u0 + uWidth * (maxZ - minZ), v0 + vHeight * (1 - (maxY - minY))).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(1, 0, 0);
    }
}
