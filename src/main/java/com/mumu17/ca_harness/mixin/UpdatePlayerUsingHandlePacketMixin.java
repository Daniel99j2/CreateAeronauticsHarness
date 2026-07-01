package com.mumu17.ca_harness.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.ca_harness.block.HarnessBlockEntity;
import dev.simulated_team.simulated.network.packets.UpdatePlayerUsingHandlePacket;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UpdatePlayerUsingHandlePacket.class)
public class UpdatePlayerUsingHandlePacketMixin {
    @Shadow
    @Final
    private float desiredRange;

    @Inject(method = "handle", at = @At(value = "INVOKE", target = "Ldev/simulated_team/simulated/content/blocks/handle/ServerHandleHoldingHandler;stopHolding(Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
    public void ca_harness$handle0(ServerPacketContext ctx, CallbackInfo ci, @Local(name = "be") BlockEntity be) {
        if (be instanceof HarnessBlockEntity) ci.cancel();
    }

    @Inject(method = "handle", at = @At(value = "INVOKE", target = "Ldev/simulated_team/simulated/content/blocks/handle/HandleBlockEntity;stopGrabbingServer(Ljava/util/UUID;)V"), cancellable = true)
    public void ca_harness$handle1(ServerPacketContext ctx, CallbackInfo ci, @Local(name = "be") BlockEntity be) {
        if (be instanceof HarnessBlockEntity) ci.cancel();
    }

    @Inject(method = "handle", at = @At(value = "INVOKE", target = "Ldev/simulated_team/simulated/content/blocks/handle/HandleBlockEntity;startGrabbingServer(Ljava/util/UUID;F)V"), cancellable = true)
    public void ca_harness$handle2(ServerPacketContext ctx, CallbackInfo ci, @Local(name = "be") BlockEntity be, @Local(name = "player") ServerPlayer player) {
        if (be instanceof HarnessBlockEntity hbe) {
            hbe.startGrabbingServer(player.getUUID(), this.desiredRange);
            ci.cancel();
        }
    }
}
