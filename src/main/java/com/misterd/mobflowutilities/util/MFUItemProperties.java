package com.misterd.mobflowutilities.util;

import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.item.custom.MobCatcherItem;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class MFUItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(MFUItems.MOB_CATCHER.get(),
                ResourceLocation.fromNamespaceAndPath("mobflowutilities", "occupied"),
                (stack, level, entity, seed) -> {
                    if (stack.getItem() instanceof MobCatcherItem mobCatcher) {
                        return mobCatcher.hasCapturedMob(stack) ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                }
        );

        customBow(MFUItems.GLOOMSTEEL_BOW.get());
    }

    private static void customBow (Item item) {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"), (ClampedItemPropertyFunction)((p_344163_, p_344164_, p_344165_, p_344166_) -> {
            if (p_344165_ == null) {
                return 0.0F;
            } else {
                return p_344165_.getUseItem() != p_344163_ ? 0.0F : (float)(p_344163_.getUseDuration(p_344165_) - p_344165_.getUseItemRemainingTicks()) / 20.0F;
            }
        }));

        ItemProperties.register(
                item,
                ResourceLocation.withDefaultNamespace("pulling"),
                (p_174630_, p_174631_, p_174632_, p_174633_) -> p_174632_ != null && p_174632_.isUsingItem() && p_174632_.getUseItem() == p_174630_ ? 1.0F : 0.0F
        );
    }
}