package com.misterd.mobflowutilities.client.renderer.ber;

import com.misterd.mobflowutilities.entity.custom.GenesisChamberBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class GenesisChamberBlockEntityRenderer
        implements BlockEntityRenderer<GenesisChamberBlockEntity, GenesisChamberBlockEntityRenderer.RenderState> {

    private static final float CHAMBER_WIDTH  = 0.75F;
    private static final float CHAMBER_HEIGHT = 0.55F;
    private static final float CHAMBER_DEPTH  = 0.75F;

    private final EntityRenderDispatcher entityRenderDispatcher;

    public GenesisChamberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.entityRenderer();
    }

    // --- Render state ---

    public static class RenderState extends BlockEntityRenderState {
        @Nullable EntityRenderState entityRenderState;
        @Nullable EntityRenderer<Entity, EntityRenderState> entityRenderer;
        float entityWidth;
        float entityHeight;
        int tickCount;
        float partialTick;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    // --- Extract (game thread) ---

    @Override
    public void extractRenderState(
            GenesisChamberBlockEntity blockEntity,
            RenderState state,
            float partialTicks,
            Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
        state.entityRenderState = null;
        state.entityRenderer = null;

        ItemStack eggStack = blockEntity.getStack(0);
        if (!(eggStack.getItem() instanceof SpawnEggItem spawnEgg)) return;

        EntityType<?> type = spawnEgg.getType(eggStack);
        if (type == null) return;

        Entity entity = blockEntity.getOrCreateRenderedEntity(type);
        if (entity == null) return;

        tickEntity(blockEntity, entity);

        @SuppressWarnings("unchecked")
        EntityRenderer<Entity, EntityRenderState> renderer =
                (EntityRenderer<Entity, EntityRenderState>) entityRenderDispatcher.getRenderer(entity);
        if (renderer == null) return;

        EntityRenderState entityState = renderer.createRenderState();
        renderer.extractRenderState(entity, entityState, partialTicks);

        state.entityRenderState = entityState;
        state.entityRenderer    = renderer;
        state.entityWidth       = entity.getBbWidth();
        state.entityHeight      = entity.getBbHeight();
        state.tickCount         = entity.tickCount;
        state.partialTick       = partialTicks;
    }

    // --- Submit (render thread) ---

    @Override
    public void submit(
            RenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState cameraState
    ) {
        if (state.entityRenderState == null || state.entityRenderer == null) return;

        poseStack.pushPose();
        applyEntityPose(state, poseStack);

        @SuppressWarnings("unchecked")
        EntityRenderer<Entity, EntityRenderState> typedRenderer =
                (EntityRenderer<Entity, EntityRenderState>) state.entityRenderer;
        typedRenderer.submit(state.entityRenderState, poseStack, collector, cameraState);

        poseStack.popPose();
    }

    // --- Helpers ---

    private static void tickEntity(GenesisChamberBlockEntity blockEntity, Entity entity) {
        entity.tickCount = (int) blockEntity.getLevel().getGameTime();
        if (entity instanceof LivingEntity living) {
            if (living.tickCount % 2 == 0) living.tick();
        } else {
            entity.tick();
        }
    }

    private static void applyEntityPose(RenderState state, PoseStack poseStack) {
        poseStack.translate(0.5, 0.41, 0.5);

        float paddingFactor = 0.8f;
        float scaleX = (CHAMBER_WIDTH  * paddingFactor) / state.entityWidth;
        float scaleY = (CHAMBER_HEIGHT * paddingFactor) / state.entityHeight;
        float scaleZ = (CHAMBER_DEPTH  * paddingFactor) / state.entityWidth;

        float scale = Math.min(Math.min(scaleX, scaleY), scaleZ);
        scale = Math.min(scale, 0.6f);

        poseStack.scale(scale, scale, scale);

        float rotation = (state.tickCount + state.partialTick) * 2.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation % 360));
    }
}