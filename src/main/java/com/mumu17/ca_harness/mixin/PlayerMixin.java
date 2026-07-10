package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import com.mumu17.ca_harness.mixin_interface.TempNoGravity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class)
public class PlayerMixin implements PlayerHarnessExtension, TempNoGravity {

    @Unique
    BlockPos ca_harness$harnessPos = null;

    @Unique
    boolean ca_harness$isControlling = false;

    @Unique
    Vec3 ca_harness$lastDelta = Vec3.ZERO;

    @Unique
    double createAeronauticsHarness$tempNoGravity = Double.MAX_VALUE;

    @Override
    public void ca_harness$setHarnessPos(@Nullable BlockPos pos) {
        ca_harness$harnessPos = pos;
    }

    @Override
    public void ca_harness$setControlling(boolean controlling) {
        ca_harness$isControlling = controlling;
    }

    @Override
    public Vec3 ca_harness$getLastDelta() {
        return ca_harness$lastDelta;
    }

    @Override
    public void ca_harness$setLastDelta(Vec3 delta) {
        ca_harness$lastDelta = delta;
    }

    @Override
    public boolean ca_harness$isControlling() {
        return ca_harness$isControlling;
    }

    @Override
    public BlockPos ca_harness$getHarnessPos() {
        return ca_harness$harnessPos;
    }


    @Override
    public double getCreateAeronauticsHarness$tempGravity() {
        return createAeronauticsHarness$tempNoGravity;
    }

    @Override
    public void setCreateAeronauticsHarness$tempGravity(double createAeronauticsHarness$tempNoGravity) {
        this.createAeronauticsHarness$tempNoGravity = createAeronauticsHarness$tempNoGravity;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void fixGravity(CallbackInfo ci) {
        createAeronauticsHarness$tempNoGravity = Double.MAX_VALUE;
    }
}
