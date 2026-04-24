package com.misterd.mobflowutilities.entity;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import java.util.function.Supplier;

import com.misterd.mobflowutilities.entity.custom.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public class MFUBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MobFlowUtilities.MODID);

    public static final Supplier<BlockEntityType<ControllerBlockEntity>> CONTROLLER_BE =
            BLOCK_ENTITIES.register("controller_be",
                    () -> new BlockEntityType<>(ControllerBlockEntity::new, MFUBlocks.CONTROLLER.get()));

    public static final Supplier<BlockEntityType<CollectorBlockEntity>> COLLECTOR_BE =
            BLOCK_ENTITIES.register("collector_be",
                    () -> new BlockEntityType<>(CollectorBlockEntity::new, MFUBlocks.COLLECTOR.get()));

    public static final Supplier<BlockEntityType<FanBlockEntity>> FAN_BE =
            BLOCK_ENTITIES.register("fan_be",
                    () -> new BlockEntityType<>(FanBlockEntity::new, MFUBlocks.FAN.get()));

    public static final Supplier<BlockEntityType<DamagePadBlockEntity>> DAMAGE_PAD_BE =
            BLOCK_ENTITIES.register("damage_pad_be",
                    () -> new BlockEntityType<>(DamagePadBlockEntity::new, MFUBlocks.DAMAGE_PAD.get()));

    public static final Supplier<BlockEntityType<FlowPadBlockEntity>> FLOW_PAD_BE =
            BLOCK_ENTITIES.register("flow_pad_be",
                    () -> new BlockEntityType<>(FlowPadBlockEntity::new,
                            MFUBlocks.FAST_FLOW_PAD.get(),
                            MFUBlocks.FASTER_FLOW_PAD.get(),
                            MFUBlocks.FASTEST_FLOW_PAD.get()));

    public static final Supplier<BlockEntityType<GenesisChamberBlockEntity>> GENESIS_CHAMBER_BE =
            BLOCK_ENTITIES.register("genesis_chamber_be",
                    () -> new BlockEntityType<>(GenesisChamberBlockEntity::new,
                            MFUBlocks.GENESIS_CHAMBER.get()));

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(Capabilities.Item.BLOCK, COLLECTOR_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof CollectorBlockEntity collector) {
                        return collector.getItemHandler(direction);
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.Item.BLOCK, CONTROLLER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof ControllerBlockEntity controller) {
                        return controller.inventory;
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.Item.BLOCK, GENESIS_CHAMBER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof GenesisChamberBlockEntity genesisChamber) {
                        return genesisChamber.getItemHandler(direction);
                    }
                    return null;
                });
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        eventBus.addListener(MFUBlockEntities::registerCapabilities);
    }
}
