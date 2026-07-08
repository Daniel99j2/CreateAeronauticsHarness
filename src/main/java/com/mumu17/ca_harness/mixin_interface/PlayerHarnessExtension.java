package com.mumu17.ca_harness.mixin_interface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public interface PlayerHarnessExtension {
    void ca_harness$setHarnessPos(BlockPos pos);
    void ca_harness$setControlling(boolean controlling);
    Vec3 ca_harness$getLastDelta();
    void ca_harness$setLastDelta(Vec3 delta);
    boolean ca_harness$isControlling();
    BlockPos ca_harness$getHarnessPos();
}
