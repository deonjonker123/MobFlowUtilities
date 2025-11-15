package com.misterd.mobflowutilities.component;

import com.misterd.mobflowutilities.component.custom.PadWrenchData;
import com.misterd.mobflowutilities.component.custom.VoidFilterData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MFUDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "mobflowutilities");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VoidFilterData>> VOID_FILTER_DATA =
            DATA_COMPONENT_TYPES.register("void_filter_data",
                    () -> DataComponentType.<VoidFilterData>builder()
                            .persistent(VoidFilterData.CODEC)
                            .cacheEncoding()
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PadWrenchData>> PAD_WRENCH_DATA =
            DATA_COMPONENT_TYPES.register("pad_wrench_data",
                    () -> DataComponentType.<PadWrenchData>builder()
                            .persistent(PadWrenchData.CODEC)
                            .networkSynchronized(ByteBufCodecs.fromCodec(PadWrenchData.CODEC))
                            .cacheEncoding()
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> ENTITY_DNA =
            DATA_COMPONENT_TYPES.register("entity_dna",
                    () -> DataComponentType.<ResourceLocation>builder()
                            .persistent(ResourceLocation.CODEC)
                            .networkSynchronized(ResourceLocation.STREAM_CODEC)
                            .cacheEncoding()
                            .build()
            );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}