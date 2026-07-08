package com.mumu17.ca_harness.mixin_interface;

import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface SubLevelHarnessData {
    public Vec3 getCreateAeronauticsHarness$oldForces();
    public void setCreateAeronauticsHarness$harnessedPlayer(UUID player);
    public UUID getCreateAeronauticsHarness$harnessedPlayer();
}
