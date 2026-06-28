package com.mumu17.ca_harness.mixin_interface;

import net.minecraft.core.BlockPos;

public interface PlayerHarnessExtension {
    void ca_harness$setHarnessPos(BlockPos pos);
    BlockPos ca_harness$getHarnessPos();
}
