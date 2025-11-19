package com.misterd.mobflowutilities.item.equipment;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

public class GloomsteelGreatbowItem extends BowItem {

    private static final float VELOCITY_MULTIPLIER = 1.3f;
    private static final float DRAW_SPEED_MULTIPLIER = 1.3f;
    private static final float EXTRA_CRIT_CHANCE = 0.05f;
    private static final int ENCHANTABILITY = 25;

    private final Random random = new Random();

    public GloomsteelGreatbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @Nullable LivingEntity entity) {
        return (int) (super.getUseDuration(stack, entity) / DRAW_SPEED_MULTIPLIER);
    }

    @Override
    public int getEnchantmentValue() {
        return ENCHANTABILITY;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        ItemStack ammoStack = player.getProjectile(stack);
        boolean hasInfiniteArrows = player.hasInfiniteMaterials();

        if (ammoStack.isEmpty() && !hasInfiniteArrows) return;

        int charge = this.getUseDuration(stack, entity) - timeLeft;

        float power = charge / 20f;
        power *= DRAW_SPEED_MULTIPLIER;
        if (power > 1.0f) power = 1.0f;

        if (!level.isClientSide) {
            AbstractArrow arrow = createProjectile(level, player, stack, ammoStack, power);
            level.addFreshEntity(arrow);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0f,
                1.0f / (random.nextFloat() * 0.4f + 1.2f)
        );

        if (!hasInfiniteArrows && !player.getAbilities().instabuild) {
            ammoStack.shrink(1);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
    }

    protected AbstractArrow createProjectile(Level level, LivingEntity shooter, ItemStack bowStack, ItemStack ammoStack, float power) {
        ArrowItem arrowItem = ammoStack.getItem() instanceof ArrowItem ? (ArrowItem) ammoStack.getItem() : (ArrowItem) Items.ARROW;
        AbstractArrow arrow = arrowItem.createArrow(level, ammoStack, shooter, bowStack);

        arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(),
                0.0f, power * 3.0f * VELOCITY_MULTIPLIER, 1.0f);

        if (power >= 1.0f || random.nextFloat() < EXTRA_CRIT_CHANCE) {
            arrow.setCritArrow(true);
        }

        return arrow;
    }
}