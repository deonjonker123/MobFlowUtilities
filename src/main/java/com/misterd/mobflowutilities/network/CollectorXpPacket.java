package com.misterd.mobflowutilities.network;

import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CollectorXpPacket(BlockPos pos, XpAction action, int amount) implements CustomPacketPayload {

    public static final Type<CollectorXpPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("mobflowutilities", "collector_xp")
    );

    public static final StreamCodec<FriendlyByteBuf, CollectorXpPacket> STREAM_CODEC;

    static {
        STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, CollectorXpPacket::pos,
                XpAction.STREAM_CODEC, CollectorXpPacket::action,
                ByteBufCodecs.INT, CollectorXpPacket::amount,
                CollectorXpPacket::new
        );
    }

    @Override
    public Type<CollectorXpPacket> type() {
        return TYPE;
    }

    public static void handle(CollectorXpPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.pos());
            if (!(blockEntity instanceof CollectorBlockEntity collector)) return;

            int depositAmount;
            int storedXp;

            switch (packet.action()) {
                case WITHDRAW -> {
                    storedXp = collector.getStoredXP();
                    depositAmount = Math.min(packet.amount(), storedXp);
                    if (depositAmount > 0) {
                        collector.setStoredXP(storedXp - depositAmount);
                        serverPlayer.giveExperiencePoints(depositAmount);
                    }
                }
                case DEPOSIT -> {
                    storedXp = serverPlayer.totalExperience;
                    depositAmount = Math.min(packet.amount(), storedXp);
                    if (depositAmount > 0) {
                        serverPlayer.giveExperiencePoints(-depositAmount);
                        collector.setStoredXP(collector.getStoredXP() + depositAmount);
                    }
                }
            }
        });
    }

    public enum XpAction {
        WITHDRAW,
        DEPOSIT;

        public static final StreamCodec<FriendlyByteBuf, XpAction> STREAM_CODEC = StreamCodec.of(
                (buf, action) -> buf.writeEnum(action),
                buf -> buf.readEnum(XpAction.class)
        );
    }
}