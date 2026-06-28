package com.mumu17.ca_harness.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.ca_harness.block.HarnessBlockEntity;
import dev.simulated_team.simulated.content.blocks.handle.ClientHandleHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandleHandler.class)
public class ClientHandleHandlerMixin {

    @Shadow
    public boolean movingSubLevel;

    @Inject(method = "startHold", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;swing(Lnet/minecraft/world/InteractionHand;)V"), cancellable = true)
    public void ca_harness$startHold(Level level, Player player, BlockPos blockPos, CallbackInfo ci) {
        if (level.getBlockEntity(blockPos) instanceof HarnessBlockEntity && !movingSubLevel) ci.cancel();
    }

    @WrapWithCondition(method = "activeTick", at = @At(value = "INVOKE", target = "Ldev/simulated_team/simulated/content/blocks/handle/ClientHandleHandler;sendUpdate(Z)V"))
    public boolean ca_harness$activeTick(ClientHandleHandler instance, boolean remove, @Local(argsOnly = true) Level level, @Local(name = "interactionPos") BlockPos blockPos) {
        return !(level.getBlockEntity(blockPos) instanceof HarnessBlockEntity && movingSubLevel);
    }
}
