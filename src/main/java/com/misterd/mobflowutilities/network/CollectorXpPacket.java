package com.misterd.mobflowutilities.network;

import com.misterd.mobflowutilities.blockentity.custom.CollectorBlockEntity;
import com.misterd.mobflowutilities.util.MFUExperienceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CollectorXpPacket(BlockPos pos, XpAction action, int amount) implements CustomPacketPayload {

    public static final Type<CollectorXpPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("mobflowutilities", "collector_xp")
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

            int playerXp = MFUExperienceUtils.getPlayerTotalExperience(serverPlayer);
            int storedXp = collector.getStoredXP();

            switch (packet.action()) {
                case WITHDRAW -> {
                    int toGive = Math.min(Math.min(packet.amount(), storedXp), Integer.MAX_VALUE - playerXp);
                    if (toGive <= 0) return;
                    collector.setStoredXP(storedXp - toGive);
                    MFUExperienceUtils.setPlayerTotalExperience(serverPlayer, playerXp + toGive);
                }
                case DEPOSIT -> {
                    int toTake = Math.min(Math.min(packet.amount(), playerXp), Integer.MAX_VALUE - storedXp);
                    if (toTake <= 0) return;
                    MFUExperienceUtils.setPlayerTotalExperience(serverPlayer, playerXp - toTake);
                    collector.setStoredXP(storedXp + toTake);
                }
                case WITHDRAW_ONE_LEVEL -> {
                    if (storedXp <= 0) return;
                    int level = serverPlayer.experienceLevel;
                    int partialXp = playerXp - MFUExperienceUtils.getTotalExperienceForLevel(level);
                    int wanted = MFUExperienceUtils.getExperienceForNextLevel(level) - Math.max(0, partialXp);
                    int toGive = Math.min(Math.min(wanted, storedXp), Integer.MAX_VALUE - playerXp);
                    if (toGive <= 0) return;
                    collector.setStoredXP(storedXp - toGive);
                    MFUExperienceUtils.setPlayerTotalExperience(serverPlayer, playerXp + toGive);
                }
                case DEPOSIT_ONE_LEVEL -> {
                    int level = serverPlayer.experienceLevel;
                    int partialXp = playerXp - MFUExperienceUtils.getTotalExperienceForLevel(level);
                    int wanted = partialXp > 0
                            ? partialXp
                            : (level > 0 ? MFUExperienceUtils.getExperienceForNextLevel(level - 1) : 0);
                    int toTake = Math.min(Math.min(wanted, playerXp), Integer.MAX_VALUE - storedXp);
                    if (toTake <= 0) return;
                    MFUExperienceUtils.setPlayerTotalExperience(serverPlayer, playerXp - toTake);
                    collector.setStoredXP(storedXp + toTake);
                }
            }
        });
    }

    private static int getTotalForLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) Math.min((long) (4.5 * level * level - 162.5 * level + 2220), Integer.MAX_VALUE);
    }

    public enum XpAction {
        WITHDRAW,
        DEPOSIT,
        WITHDRAW_ONE_LEVEL,
        DEPOSIT_ONE_LEVEL;

        public static final StreamCodec<FriendlyByteBuf, XpAction> STREAM_CODEC = StreamCodec.of(
                (buf, action) -> buf.writeEnum(action),
                buf -> buf.readEnum(XpAction.class)
        );
    }
}