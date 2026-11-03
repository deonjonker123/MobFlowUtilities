package com.misterd.mobflowutilities.mixin;

import com.misterd.mobflowutilities.util.FakePlayerHandler;
import net.minecraft.advancements.criterion.PlayerHurtEntityTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerHurtEntityTrigger.class)
public class PlayerHurtEntityTriggerMixin {
    @Inject(method = "trigger", at = @At("HEAD"), cancellable = true)
    private void mobflowutilities$skipPadFakePlayer(ServerPlayer player, Entity entity, DamageSource source, float damageDealt, float damageTaken, boolean blocked, CallbackInfo ci) {
        if (player instanceof FakePlayer fakePlayer && FakePlayerHandler.isMobflowutilitiesFakePlayer(fakePlayer)) {
            ci.cancel();
        }
    }
}