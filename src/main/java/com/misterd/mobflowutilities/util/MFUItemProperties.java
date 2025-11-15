package com.misterd.mobflowutilities.util;

import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.item.custom.MobCatcherItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

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
    }
}