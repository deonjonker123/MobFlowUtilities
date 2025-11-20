package com.misterd.mobflowutilities.client.renderer.ber;

import com.misterd.mobflowutilities.entity.custom.GenesisChamberBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class GenesisChamberBlockEntityRenderer implements BlockEntityRenderer<GenesisChamberBlockEntity> {

    public GenesisChamberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public void render(GenesisChamberBlockEntity genesisChamberBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        ItemStack eggStack = genesisChamberBlockEntity.inventory.getStackInSlot(0);
        if (!(eggStack.getItem() instanceof SpawnEggItem spawnEgg)) {
            return;
        }

        EntityType<?> type = spawnEgg.getType(eggStack);
        if (type == null) return;

        Entity entity = genesisChamberBlockEntity.getOrCreateRenderedEntity(type);
        if (entity == null) return;

        tickEntity(genesisChamberBlockEntity, entity);

        poseStack.pushPose();
        prepareEntityPose(entity, poseStack);

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.getRenderer(entity).render(entity, 0, partialTicks, poseStack, buffer, combinedLight);

        poseStack.popPose();
    }

    private void tickEntity(GenesisChamberBlockEntity genesisChamberBlockEntity, Entity entity) {
        entity.tickCount = (int) genesisChamberBlockEntity.getLevel().getGameTime();

        if (entity instanceof LivingEntity living) {
            if (living.tickCount % 2 == 0) {
                living.tick();
            }
        } else {
            entity.tick();
        }
    }

    private void prepareEntityPose(Entity entity, PoseStack poseStack) {
        poseStack.translate(0.5, 0.4, 0.5);

        float scale = Math.min(0.7f / entity.getBbWidth(), 0.7f / entity.getBbHeight());
        poseStack.scale(scale, scale, scale);

        float rotation = (entity.tickCount % 360);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
    }
}