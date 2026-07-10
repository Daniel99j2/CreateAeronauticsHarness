package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.SubLevelHarnessData;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.physics.mass.MergedMassTracker;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
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

    @Shadow
    public abstract MassData getMassTracker();

    @Unique
    private Vec3 createAeronauticsHarness$oldForces = Vec3.ZERO;

    @Unique
    private UUID createAeronauticsHarness$harnessedPlayer = null;

    @Inject(method = "applyQueuedForces", at = @At(value = "HEAD"))
    private void storeForces(CallbackInfo ci) {
        if(this.queuedForceGroups != null && this.createAeronauticsHarness$harnessedPlayer != null) {
            final Vector3d velocity1 = new Vector3d();

            //https://github.com/Creators-of-Aeronautics/Simulated-Project/blob/ee15c150ae79c4950f5379c3f629e43a0250ecdd/simulated/common/src/main/java/dev/simulated_team/simulated/content/entities/diagram/DiagramEntity.java#L186
            double timeStep = 1.0 / 20.0 / SubLevelPhysicsSystem.get(this.getLevel()).getConfig().substepsPerTick;

            this.queuedForceGroups.forEach((g, q) -> {
                velocity1.add(q.getForceTotal().getLocalForce());
            });

            velocity1.div(timeStep);

            Quaterniond quat = ((SubLevel) (Object) this).logicalPose().orientation();
            Vector3d euler = quat.getEulerAnglesYXZ(new Vector3d());

            Quaterniond quat2 = new Quaterniond().rotationY(euler.y);

            Vector3d vector2 = quat2.transform(new Vector3d(velocity1));
            vector2.add(DimensionPhysicsData.getGravity(this.getLevel()).mul(this.getMassTracker().getMass()));
            vector2.mul(DimensionPhysicsData.getUniversalDrag(this.getLevel()));
            //this.massTracker.getMass();
            createAeronauticsHarness$oldForces = new Vec3(vector2.x, vector2.y, vector2.z).multiply((float) timeStep, (float) timeStep, (float) timeStep);
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

