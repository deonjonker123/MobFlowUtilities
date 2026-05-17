package com.misterd.mobflowutilities.network;

import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
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

            switch (packet.action()) {
                case WITHDRAW -> {
                    int storedXp = collector.getStoredXP();
                    int depositAmount = Math.min(packet.amount(), storedXp);
                    if (depositAmount > 0) {
                        collector.setStoredXP(storedXp - depositAmount);
                        serverPlayer.giveExperiencePoints(depositAmount);
                    }
                }
                case DEPOSIT -> {
                    int storedXp = serverPlayer.totalExperience;
                    int depositAmount = Math.min(packet.amount(), storedXp);
                    if (depositAmount > 0) {
                        serverPlayer.giveExperiencePoints(-depositAmount);
                        collector.setStoredXP(collector.getStoredXP() + depositAmount);
                    }
                }
                case WITHDRAW_ONE_LEVEL -> {
                    int storedXp = collector.getStoredXP();
                    if (storedXp <= 0) return;
                    int partialNeeded = MFUExperienceUtils.getExpNeededForNextLevel(serverPlayer);
                    if (serverPlayer.experienceProgress > 0.0f && partialNeeded > 0) {
                        int toGive = Math.min(partialNeeded, storedXp);
                        serverPlayer.giveExperiencePoints(toGive);
                        collector.setStoredXP(storedXp - toGive);
                        return;
                    }
                    int xpForOneLevel = MFUExperienceUtils.getExperienceForNextLevel(serverPlayer.experienceLevel);
                    int toGive = Math.min(xpForOneLevel, storedXp);
                    if (toGive <= 0) return;
                    serverPlayer.giveExperiencePoints(toGive);
                    collector.setStoredXP(storedXp - toGive);
                }
                case DEPOSIT_ONE_LEVEL -> {
                    int partialXp = (int) Math.round(serverPlayer.experienceProgress * serverPlayer.getXpNeededForNextLevel());
                    if (partialXp > 0) {
                        int removed = MFUExperienceUtils.removePoints(serverPlayer, partialXp);
                        serverPlayer.experienceProgress = 0.0f;
                        collector.setStoredXP(collector.getStoredXP() + removed);
                        return;
                    }
                    if (serverPlayer.experienceLevel <= 0) return;
                    int xpForThisLevel = MFUExperienceUtils.getExperienceForNextLevel(serverPlayer.experienceLevel - 1);
                    serverPlayer.giveExperienceLevels(-1);
                    collector.setStoredXP(collector.getStoredXP() + xpForThisLevel);
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