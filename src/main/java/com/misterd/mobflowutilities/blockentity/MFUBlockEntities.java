package com.misterd.mobflowutilities.blockentity;

import com.misterd.mobflowutilities.block.MFUBlocks;
import java.util.function.Supplier;

import com.misterd.mobflowutilities.blockentity.custom.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public class MFUBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "mobflowutilities");

    public static final Supplier<BlockEntityType<ControllerBlockEntity>> CONTROLLER_BE =
            BLOCK_ENTITIES.register("controller_be",
                    () -> BlockEntityType.Builder.of(ControllerBlockEntity::new,
                            MFUBlocks.CONTROLLER.get()).build(null));

    public static final Supplier<BlockEntityType<CollectorBlockEntity>> COLLECTOR_BE =
            BLOCK_ENTITIES.register("collector_be",
                    () -> BlockEntityType.Builder.of(CollectorBlockEntity::new,
                            MFUBlocks.COLLECTOR.get()).build(null));

    public static final Supplier<BlockEntityType<FanBlockEntity>> FAN_BE =
            BLOCK_ENTITIES.register("fan_be",
                    () -> BlockEntityType.Builder.of(FanBlockEntity::new,
                            MFUBlocks.FAN.get()).build(null));

    public static final Supplier<BlockEntityType<DamagePadBlockEntity>> DAMAGE_PAD_BE =
            BLOCK_ENTITIES.register("damage_pad_be",
                    () -> BlockEntityType.Builder.of(DamagePadBlockEntity::new,
                            MFUBlocks.DAMAGE_PAD.get()).build(null));

    public static final Supplier<BlockEntityType<FlowPadBlockEntity>> FLOW_PAD_BE =
            BLOCK_ENTITIES.register("flow_pad_be",
                    () -> BlockEntityType.Builder.of(FlowPadBlockEntity::new,
                            MFUBlocks.FAST_FLOW_PAD.get(),
                            MFUBlocks.FASTER_FLOW_PAD.get(),
                            MFUBlocks.FASTEST_FLOW_PAD.get()).build(null));

    public static final Supplier<BlockEntityType<GenesisChamberBlockEntity>> GENESIS_CHAMBER_BE =
            BLOCK_ENTITIES.register("genesis_chamber_be",
                    () -> BlockEntityType.Builder.of(GenesisChamberBlockEntity::new,
                            MFUBlocks.GENESIS_CHAMBER.get()).build(null));

    public static final Supplier<BlockEntityType<GigaTankBlockEntity>> GIGATANK_BE =
            BLOCK_ENTITIES.register("giga_tank_be", () -> BlockEntityType.Builder.of(
                    GigaTankBlockEntity::new, MFUBlocks.GIGATANK.get()).build(null));

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, COLLECTOR_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof CollectorBlockEntity collector) {
                        return collector.getItemHandler(direction);
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CONTROLLER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof ControllerBlockEntity controller) {
                        return (IItemHandler) controller.inventory;
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, MFUBlockEntities.COLLECTOR_BE.get(),
                (blockEntity, direction) -> blockEntity instanceof CollectorBlockEntity collector
                        ? collector.getFluidTank()
                        : null
        );

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, GENESIS_CHAMBER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof GenesisChamberBlockEntity genesisChamber) {
                        return genesisChamber.getItemHandler(direction);
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, GIGATANK_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof GigaTankBlockEntity gigaTankBlockEntity) {
                        return gigaTankBlockEntity.tank;
                    }
                    return null;
                });
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        eventBus.addListener(MFUBlockEntities::registerCapabilities);
    }
}
