package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Player.class)
public class PlayerMixin implements PlayerHarnessExtension {

    @Unique
    BlockPos ca_harness$harnessPos = null;

    @Unique
    boolean ca_harness$isControlling = false;

    @Unique
    Vec3 ca_harness$lastDelta = Vec3.ZERO;

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
}
