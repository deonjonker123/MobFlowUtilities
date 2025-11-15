package com.misterd.mobflowutilities.util;

import com.mojang.authlib.GameProfile;
import java.lang.ref.WeakReference;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class FakePlayerHandler {
    private static final GameProfile DEFAULT_PROFILE = new GameProfile(UUID.fromString("12345678-1234-1234-1234-123456789012"), "[MobFlowUtilities]");

    private static FakePlayer getDefault(ServerLevel level) {
        return FakePlayerFactory.get(level, DEFAULT_PROFILE);
    }

    private static FakePlayer get(ServerLevel level, @Nullable UUID placer) {
        FakePlayer fakePlayer;
        if (placer == null) {
            fakePlayer = getDefault(level);
        } else {
            fakePlayer = FakePlayerFactory.get(level, new GameProfile(placer, Component.translatable("fakeplayer.mobflowutilities.damage_pad").getString()));
        }

        fakePlayer.getPersistentData().putBoolean("mobflowutilities", true);
        fakePlayer.setInvisible(true);
        fakePlayer.setInvulnerable(true);
        return fakePlayer;
    }

    public static WeakReference<FakePlayer> get(WeakReference<FakePlayer> previous, ServerLevel level, @Nullable UUID placer, BlockPos pos) {
        FakePlayer fakePlayer = previous.get();
        if (fakePlayer == null) {
            fakePlayer = get(level, placer);
            fakePlayer.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            return new WeakReference(fakePlayer);
        } else {
            fakePlayer.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            return previous;
        }
    }

    public static boolean isMobflowutilitiesFakePlayer(FakePlayer fakePlayer) {
        return fakePlayer.getPersistentData().contains("mobflowutilities");
    }
}
