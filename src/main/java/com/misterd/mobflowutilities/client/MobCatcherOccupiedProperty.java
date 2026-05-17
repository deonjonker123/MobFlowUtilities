package com.misterd.mobflowutilities.client;

import com.misterd.mobflowutilities.item.custom.MobCatcherItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class MobCatcherOccupiedProperty implements ConditionalItemModelProperty {

    public static final MobCatcherOccupiedProperty INSTANCE = new MobCatcherOccupiedProperty();
    public static final MapCodec<MobCatcherOccupiedProperty> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext context) {
        return stack.getItem() instanceof MobCatcherItem catcher && catcher.hasCapturedMob(stack);
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }
}