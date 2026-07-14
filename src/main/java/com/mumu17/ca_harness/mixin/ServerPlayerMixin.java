package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.InputGetter;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import com.mumu17.ca_harness.mixin_interface.TempNoGravity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class)
public class ServerPlayerMixin implements InputGetter {
    @Unique
    Inputs createAeronauticsHarness$lastInputs = new Inputs(false, false, false, false);

    @Override
    public Inputs ca_harness$getInputs() {
        return createAeronauticsHarness$lastInputs;
    }

    @Inject(method = "setPlayerInput", at = @At(value = "TAIL"))
    private void storeInputs(float forward, float side, boolean jump, boolean sneak, CallbackInfo ci) {
        createAeronauticsHarness$lastInputs = new Inputs(forward > 0, side > 0, forward < 0, side < 0);
    }
}
