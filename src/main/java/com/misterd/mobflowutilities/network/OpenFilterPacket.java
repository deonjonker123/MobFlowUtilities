package com.misterd.mobflowutilities.network;

import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
import com.misterd.mobflowutilities.gui.custom.VoidFilterMenu;
import com.misterd.mobflowutilities.item.custom.VoidFilterItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenFilterPacket(BlockPos collectorPos, int filterSlotIndex) implements CustomPacketPayload {

    public static final Type<OpenFilterPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "open_filter")
    );

    public static final StreamCodec<ByteBuf, OpenFilterPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            OpenFilterPacket::collectorPos,
            ByteBufCodecs.INT,
            OpenFilterPacket::filterSlotIndex,
            OpenFilterPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenFilterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.collectorPos);

                if (blockEntity instanceof CollectorBlockEntity collector) {
                    // Get the filter from the specified module slot
                    ItemStack filterStack = collector.moduleSlots.getStackInSlot(packet.filterSlotIndex);

                    // Verify it's actually a void filter
                    if (filterStack.getItem() instanceof VoidFilterItem) {
                        // Open the filter GUI with the in-place filter using the factory method
                        serverPlayer.openMenu(
                                VoidFilterMenu.createForCollectorFilter(
                                        Component.translatable("gui.mobflowutilities.void_filter"),
                                        packet.collectorPos,
                                        packet.filterSlotIndex
                                )
                        );
                    }
                }
            }
        });
    }
}