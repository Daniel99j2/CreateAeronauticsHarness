package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.SubLevelHarnessData;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.mass.MergedMassTracker;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerSubLevel.class)
public abstract class ServerSubLevelMixin implements SubLevelHarnessData {
    @Shadow
    private @Nullable Object2ObjectMap<ForceGroup, QueuedForceGroup> queuedForceGroups;
    @Shadow
    private MergedMassTracker massTracker;

    @Shadow
    public abstract ServerLevel getLevel();

    @Unique
    private Vec3 createAeronauticsHarness$oldForces = Vec3.ZERO;

    @Unique
    private UUID createAeronauticsHarness$harnessedPlayer = null;

    @Inject(method = "applyQueuedForces", at = @At(value = "HEAD"))
    private void storeForces(CallbackInfo ci) {
        if(this.queuedForceGroups != null) {
            final Vector3d velocity1 = new Vector3d();

            this.queuedForceGroups.forEach((g, q) -> {
                velocity1.add(q.getForceTotal().getLocalForce());
            });

            Quaterniond quat = ((SubLevel) (Object) this).logicalPose().orientation();
            Vector3d euler = quat.getEulerAnglesYXZ(new Vector3d());

            Quaterniond quat2 = new Quaterniond().rotationY(euler.y);

            Vector3d vector2 = quat2.transform(new Vector3d(velocity1));
            //vector2.add(DimensionPhysicsData.getGravity(this.getLevel()));
            vector2.mul(DimensionPhysicsData.getUniversalDrag(this.getLevel()));
            //this.massTracker.getMass();
            createAeronauticsHarness$oldForces = new Vec3(vector2.x, vector2.y, vector2.z);
        }
    }

    @Override
    public Vec3 getCreateAeronauticsHarness$oldForces() {
        return createAeronauticsHarness$oldForces;
    }

    @Override
    public UUID getCreateAeronauticsHarness$harnessedPlayer() {
        return createAeronauticsHarness$harnessedPlayer;
    }

    @Override
    public void setCreateAeronauticsHarness$harnessedPlayer(UUID player) {
        this.createAeronauticsHarness$harnessedPlayer = player;
    }
}

