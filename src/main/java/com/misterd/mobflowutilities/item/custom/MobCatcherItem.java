package com.misterd.mobflowutilities.item.custom;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        } else if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        } else if (this.hasCapturedMob(stack)) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.already_occupied"), true);
            return InteractionResult.FAIL;
        } else if (this.isBossMob(target)) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.boss_immunity"), true);
            return InteractionResult.FAIL;
        } else if (target instanceof Player) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.player_immunity"), true);
            return InteractionResult.FAIL;
        } else if (this.captureMob(stack, target, player)) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.captured", new Object[]{target.getDisplayName().getString()}), true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.2F);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (!level.isClientSide() && player != null) {
            if (!player.isShiftKeyDown()) {
                return InteractionResult.PASS;
            } else if (!this.hasCapturedMob(stack)) {
                player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.empty"), true);
                return InteractionResult.FAIL;
            } else {
                BlockState state = level.getBlockState(pos);
                if (!state.isSolidRender(level, pos)) {
                    player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.invalid_surface"), true);
                    return InteractionResult.FAIL;
                } else {
                    BlockPos spawnPos = pos.above();
                    if (level.getBlockState(spawnPos).isAir() && level.getBlockState(spawnPos.above()).isAir()) {
                        if (this.releaseMob(stack, (ServerLevel)level, spawnPos, player)) {
                            player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.released"), true);
                            level.playSound(null, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F);
                            return InteractionResult.SUCCESS;
                        } else {
                            return InteractionResult.FAIL;
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.no_space"), true);
                        return InteractionResult.FAIL;
                    }
                }
            }
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    private boolean isBossMob(LivingEntity entity) {
        return entity instanceof WitherBoss || entity instanceof EnderDragon || entity instanceof ElderGuardian || entity instanceof Ravager || entity instanceof Warden || entity.getType().getCategory().getName().equals("boss") || entity.getPersistentData().getBoolean("IsBoss");
    }

    public boolean hasCapturedMob(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().contains("StoredEntity");
    }

    private boolean captureMob(ItemStack stack, LivingEntity target, Player player) {
        try {
            CompoundTag entityTag = new CompoundTag();
            target.save(entityTag);
            CompoundTag itemTag = new CompoundTag();
            itemTag.put("StoredEntity", entityTag);
            itemTag.putString("EntityType", EntityType.getKey(target.getType()).toString());
            itemTag.putString("DisplayName", target.getDisplayName().getString());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(itemTag));
            target.discard();
            return true;
        } catch (Exception e) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.capture_failed"), true);
            return false;
        }
    }

    private boolean releaseMob(ItemStack stack, ServerLevel level, BlockPos spawnPos, Player player) {
        try {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData == null) {
                return false;
            } else {
                CompoundTag itemTag = customData.copyTag();
                if (!itemTag.contains("StoredEntity")) {
                    return false;
                } else {
                    CompoundTag entityTag = itemTag.getCompound("StoredEntity");
                    String entityTypeString = itemTag.getString("EntityType");
                    Optional<EntityType<?>> entityTypeOpt = EntityType.byString(entityTypeString);
                    if (entityTypeOpt.isEmpty()) {
                        return false;
                    } else {
                        EntityType<?> entityType = entityTypeOpt.get();
                        Entity entity = entityType.create(level);
                        if (entity == null) {
                            return false;
                        } else {
                            entity.load(entityTag);
                            entity.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                            level.addFreshEntity(entity);
                            stack.remove(DataComponents.CUSTOM_DATA);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            player.displayClientMessage(Component.translatable("item.mobflowutilities.mob_catcher.release_failed"), true);
            return false;
        }
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (this.hasCapturedMob(stack)) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag itemTag = customData.copyTag();
                String displayName = itemTag.getString("DisplayName");
                String entityType = itemTag.getString("EntityType");
                tooltipComponents.add(Component.literal(""));
                tooltipComponents.add(Component.translatable("item.mobflowutilities.mob_catcher.contains", new Object[]{displayName}).withStyle(ChatFormatting.GREEN));
                tooltipComponents.add(Component.literal("Type: " + entityType).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("item.mobflowutilities.mob_catcher.empty_tooltip").withStyle(ChatFormatting.GRAY));
        }

        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable("item.mobflowutilities.mob_catcher.usage.capture").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.translatable("item.mobflowutilities.mob_catcher.usage.release").withStyle(ChatFormatting.YELLOW));
    }

    public boolean isFoil(ItemStack stack) {
        return this.hasCapturedMob(stack);
    }

    public boolean isBarVisible(ItemStack stack) {
        return this.hasCapturedMob(stack);
    }

    public int getBarWidth(ItemStack stack) {
        return this.hasCapturedMob(stack) ? 13 : 0;
    }

    public int getBarColor(ItemStack stack) {
        return this.hasCapturedMob(stack) ? 5635925 : 16777215;
    }
}
