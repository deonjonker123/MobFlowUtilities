package com.misterd.mobflowutilities.item.custom;

import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MobCatcherItem extends Item {

    private static final String STORED_ENTITY_TAG = "StoredEntity";
    private static final String ENTITY_TYPE_TAG = "EntityType";
    private static final String ENTITY_DISPLAY_NAME_TAG = "DisplayName";

    public MobCatcherItem(Properties properties) {
        super(properties);
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide()) return InteractionResult.SUCCESS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (hasCapturedMob(stack)) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.already_occupied"));
            return InteractionResult.FAIL;
        }
        if (isBossMob(target)) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.boss_immunity"));
            return InteractionResult.FAIL;
        }
        if (target instanceof Player) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.player_immunity"));
            return InteractionResult.FAIL;
        }
        if (captureMob(stack, target, player)) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.captured", target.getDisplayName().getString()));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.2F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide() || player == null) return InteractionResult.SUCCESS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!hasCapturedMob(stack)) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.empty"));
            return InteractionResult.FAIL;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.isSolidRender()) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.invalid_surface"));
            return InteractionResult.FAIL;
        }

        BlockPos spawnPos = pos.above();
        if (!level.getBlockState(spawnPos).isAir() || !level.getBlockState(spawnPos.above()).isAir()) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.no_space"));
            return InteractionResult.FAIL;
        }

        if (releaseMob(stack, (ServerLevel) level, spawnPos, player)) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.released"));
            level.playSound(null, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private boolean isBossMob(LivingEntity entity) {
        return entity instanceof WitherBoss
                || entity instanceof EnderDragon
                || entity instanceof ElderGuardian
                || entity instanceof Ravager
                || entity instanceof Warden
                || entity.getType().getCategory().getName().equals("boss")
                || entity.getPersistentData().getBooleanOr("IsBoss", false);
    }

    public boolean hasCapturedMob(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().contains(STORED_ENTITY_TAG);
    }

    private boolean captureMob(ItemStack stack, LivingEntity target, Player player) {
        try {
            TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            target.saveWithoutId(output);
            CompoundTag entityTag = output.buildResult();
            CompoundTag itemTag = new CompoundTag();
            itemTag.put(STORED_ENTITY_TAG, entityTag);
            itemTag.putString(ENTITY_TYPE_TAG, EntityType.getKey(target.getType()).toString());
            itemTag.putString(ENTITY_DISPLAY_NAME_TAG, target.getDisplayName().getString());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(itemTag));
            target.discard();
            return true;
        } catch (Exception e) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.capture_failed"));
            return false;
        }
    }

    private boolean releaseMob(ItemStack stack, ServerLevel level, BlockPos spawnPos, Player player) {
        try {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData == null) return false;

            CompoundTag itemTag = customData.copyTag();
            if (!itemTag.contains(STORED_ENTITY_TAG)) return false;

            CompoundTag entityTag = itemTag.getCompoundOrEmpty(STORED_ENTITY_TAG);
            String entityTypeString = itemTag.getStringOr(ENTITY_TYPE_TAG, "");
            if (entityTypeString.isEmpty()) return false;

            Optional<EntityType<?>> entityTypeOpt = EntityType.byString(entityTypeString);
            if (entityTypeOpt.isEmpty()) return false;

            EntityType<?> entityType = entityTypeOpt.get();
            Entity entity = entityType.create(level, EntitySpawnReason.LOAD);
            if (entity == null) return false;

            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), entityTag);
            entity.load(input);
            entity.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            level.addFreshEntity(entity);
            stack.remove(DataComponents.CUSTOM_DATA);
            return true;
        } catch (Exception e) {
            player.sendSystemMessage(Component.translatable("item.mobflowutilities.mob_catcher.release_failed"));
            return false;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
        if (hasCapturedMob(stack)) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag itemTag = customData.copyTag();
                String displayName = itemTag.getStringOr(ENTITY_DISPLAY_NAME_TAG, "");
                String entityType = itemTag.getStringOr(ENTITY_TYPE_TAG, "");
                adder.accept(Component.literal(""));
                adder.accept(Component.translatable("item.mobflowutilities.mob_catcher.contains", displayName).withStyle(ChatFormatting.GREEN));
                adder.accept(Component.literal("Type: " + entityType).withStyle(ChatFormatting.GRAY));
            }
        } else {
            adder.accept(Component.literal(""));
            adder.accept(Component.translatable("item.mobflowutilities.mob_catcher.empty_tooltip").withStyle(ChatFormatting.GRAY));
        }
        adder.accept(Component.literal(""));
        adder.accept(Component.translatable("item.mobflowutilities.mob_catcher.usage.capture").withStyle(ChatFormatting.YELLOW));
        adder.accept(Component.translatable("item.mobflowutilities.mob_catcher.usage.release").withStyle(ChatFormatting.YELLOW));
    }

    @Override public boolean isFoil(ItemStack stack) { return hasCapturedMob(stack); }
    @Override public boolean isBarVisible(ItemStack stack) { return hasCapturedMob(stack); }
    @Override public int getBarWidth(ItemStack stack) { return hasCapturedMob(stack) ? 13 : 0; }
    @Override public int getBarColor(ItemStack stack) { return hasCapturedMob(stack) ? 5635925 : 16777215; }
}