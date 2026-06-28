package com.mumu17.ca_harness.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.ca_harness.block.HarnessBlockEntity;
import dev.simulated_team.simulated.network.packets.UpdatePlayerUsingHandlePacket;
import foundry.veil.api.network.handler.ServerPacketContext;
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
    private boolean remove;

    @Definition(id = "getBlockEntity", method = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;")
    @Expression("? = ?.getBlockEntity(?)")
    @Inject(method = "handle", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER), cancellable = true)
    public void ca_harness$handle(ServerPacketContext ctx, CallbackInfo ci, @Local(type = BlockEntity.class, name = "be") BlockEntity be) {
        if (be instanceof HarnessBlockEntity && remove) ci.cancel();
    }
}
