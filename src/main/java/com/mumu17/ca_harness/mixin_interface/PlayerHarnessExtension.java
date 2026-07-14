package com.mumu17.ca_harness.mixin_interface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public interface PlayerHarnessExtension {
    void ca_harness$setHarnessPos(BlockPos pos);
    BlockPos ca_harness$getHarnessPos();
}
