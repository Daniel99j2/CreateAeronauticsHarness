package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import com.mumu17.ca_harness.mixin_interface.SubLevelHarnessData;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.blocks.handle.HandleBlockEntity;
import dev.simulated_team.simulated.config.server.physics.SimPhysics;
import dev.simulated_team.simulated.service.SimConfigService;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


public class HarnessBlockEntity extends HandleBlockEntity {

    private final Map<UUID, HarnessConstraint> players = new Object2ObjectOpenHashMap<>();

    public HarnessBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        assert this.level != null;
        assert Minecraft.getInstance().player != null;
        if(this.level.isClientSide && this.getBlockPos().equals(((PlayerHarnessExtension) Minecraft.getInstance().player).ca_harness$getHarnessPos())) {
            VeilPacketManager.server().sendPacket(new BodyRotationPacket(Minecraft.getInstance().player.yBodyRot));
        }
    }

    private void checkPlayers() {
        assert this.level != null;

        Iterator<Map.Entry<UUID, HarnessConstraint>> it = this.players.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<UUID, HarnessConstraint> entry = it.next();
            Player player = this.level.getPlayerByUUID((UUID)entry.getKey());
            HarnessConstraint constraint = (HarnessConstraint)entry.getValue();
            if (player != null && !player.isDeadOrDying()) {
                if (constraint == null || !constraint.hasJoint()) {
                    player.resetFallDistance();
                }
            } else {
                if (constraint != null) {
                    constraint.removeJoint();
                }

                it.remove();
                this.setChanged();
            }
        }

    }

    @Override
    public void sable$physicsTick(final ServerSubLevel subLevel, final RigidBodyHandle handle, final double timeStep) {
        this.checkPlayers();

        for(HarnessConstraint constraint : this.players.values()) {
            constraint.physicsTick(subLevel, handle);
        }

    }

    @Override
    public void startGrabbingServer(UUID player, float desiredRange) {

            HarnessConstraint handle = new HarnessConstraint(player, (PhysicsConstraintHandle)null);
            this.players.put(player, handle);
            this.setChanged();

        Level level = this.level;
        assert level != null;
        ServerPlayer serverPlayer = (ServerPlayer) level.getPlayerByUUID(player);
        PlayerHarnessExtension playerHarnessExtension = (PlayerHarnessExtension) serverPlayer;
        if (playerHarnessExtension != null) {
            if (!level.isClientSide && playerHarnessExtension.ca_harness$getHarnessPos() == null)
                HarnessBlock.carryOn(level, this.getBlockPos(), serverPlayer, serverPlayer.isShiftKeyDown());
        }
    }

    @Override
    public void stopGrabbingServer(UUID player) {
        ca_harness$stopGrabbingServer(player);
    }

    public void ca_harness$stopGrabbingServer(UUID player) {
        Level level = this.level;
        assert level != null;
        HarnessConstraint constraint = this.players.remove(player);
        this.setChanged();
        if (constraint != null) {
            constraint.removeJoint();
        }
    }

    @Override
    public void remove() {
        super.remove();
        this.players.values().forEach(HarnessConstraint::removeJoint);
        this.players.clear();
        this.setChanged();
    }

    private class HarnessConstraint {
        private static final double CONSTRAINT_DAMPING = (double)30.0F;
        private static final double CONSTRAINT_STIFFNESS = (double)240.0F;
        private final UUID playerId;
        private final Vector3d localGoal = new Vector3d();
        private final Quaterniond orientation = new Quaterniond();
        private @Nullable PhysicsConstraintHandle constraintHandle;

        public HarnessConstraint(final UUID playerId, final PhysicsConstraintHandle constraintHandle) {
            super();
            this.playerId = playerId;
            this.constraintHandle = constraintHandle;
        }

        public void physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle) {
            this.removeJoint();

            Player player = HarnessBlockEntity.this.level != null
                    ? HarnessBlockEntity.this.level.getPlayerByUUID(this.playerId)
                    : null;

            if (player == null) {
                return;
            }

            if(player.isSpectator() || player.getVehicle() != null || player.isFallFlying()) {
                HarnessBlockEntity.this.stopGrabbingServer(this.playerId);
                VeilPacketManager.player((ServerPlayer) player).sendPacket(new StopHarnessPacket(true));
                return;
            }

            Vec3 velocity = ((SubLevelHarnessData) subLevel).getCreateAeronauticsHarness$oldForces();

            //player.sendSystemMessage(Component.literal(String.valueOf(subLevel.getMassTracker().getMass())));

//            if (velocity.y > 0) {
//                ((PlayerFlyAccessor) ((ServerPlayer) player).connection).setAboveGroundTickCount(0);
//                VeilPacketManager.player((ServerPlayer) player).sendPacket(new PlayerTempGravityPacket((float) DimensionPhysicsData.getGravity(player.level()).y));
//                //VeilPacketManager.player((ServerPlayer) player).sendPacket(new PlayerVelocityPacket(new Vec3(RELATIVE, velocity.y, RELATIVE).toVector3f(), false, false));
//            }
            //VeilPacketManager.player((ServerPlayer) player).sendPacket(new PlayerTempGravityPacket(DimensionPhysicsData.getGravity(player.level()).y*DimensionPhysicsData.getUniversalDrag((ServerLevel) player.level())));
            VeilPacketManager.player((ServerPlayer) player).sendPacket(new PlayerTempGravityPacket(0));
            //Sable.HELPER.getVelocity();
            //+DimensionPhysicsData.getGravity(player.level()).y*DimensionPhysicsData.getUniversalDrag((ServerLevel) player.level())
            VeilPacketManager.player((ServerPlayer) player).sendPacket(new PlayerVelocityPacket(new Vec3(velocity.x, velocity.y, velocity.z).toVector3f(), true, true));

            ServerSubLevelContainer container = SubLevelContainer.getContainer(subLevel.getLevel());
            SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
            ServerSubLevel containingSubLevel =
                    (ServerSubLevel) Sable.HELPER.getContaining(HarnessBlockEntity.this);

            if(HarnessBlockEntity.this.getBlockState().getValue(HarnessBlock.AXIS_ALONG_FIRST_COORDINATE)) {
                player.yBodyRot = player.getYRot();
                player.yBodyRotO = player.getYRot();
            }
            double yaw = Math.toRadians(player.yBodyRot+HarnessBlockEntity.this.getBlockState().getValue(HarnessBlock.FACING).toYRot()+(HarnessBlockEntity.this.getBlockState().getValue(HarnessBlock.FACING).toYRot() % 180 == 0 ? 0 : 180));

            this.orientation.identity()
                    .rotateY(yaw)
                    .invert();

            Vector3d goal = JOMLConversion.toJOML(player.getEyePosition().add(0.0, -0.6, 0.0));
            this.orientation.transformInverse(goal);
            this.localGoal.set(goal);

            FreeConstraintConfiguration configuration = new FreeConstraintConfiguration(
                    JOMLConversion.ZERO,
                    HarnessBlockEntity.this.getGrabCenter(),
                    this.orientation
            );

            this.constraintHandle = physicsSystem.getPipeline().addConstraint(
                    null,
                    containingSubLevel,
                    configuration
            );

            SimPhysics config = SimConfigService.INSTANCE.server().physics;

            double stiffnessScale = 30.0;
            double dampingScale = 60.0;
            double maxForce = config.handleMaxForce.getF()*20;

// Angular motors
            double angularStiffness = config.physicsStaffAngularStiffness.getF() * stiffnessScale;
            double angularDamping = config.physicsStaffAngularDamping.getF() * dampingScale;

            for (ConstraintJointAxis axis : ConstraintJointAxis.ANGULAR) {
                this.constraintHandle.setMotor(
                        axis,
                        0.0,
                        angularStiffness,
                        angularDamping,
                        true,
                        maxForce
                );
            }

// Linear motors
            double linearStiffness = config.physicsStaffLinearStiffness.getF() * stiffnessScale;
            double linearDamping = config.physicsStaffLinearDamping.getF() * dampingScale;

            this.constraintHandle.setMotor(
                    ConstraintJointAxis.LINEAR_X,
                    this.localGoal.x(),
                    linearStiffness,
                    linearDamping,
                    true,
                    maxForce
            );

            this.constraintHandle.setMotor(
                    ConstraintJointAxis.LINEAR_Y,
                    this.localGoal.y(),
                    linearStiffness,
                    linearDamping,
                    true,
                    maxForce
            );

            this.constraintHandle.setMotor(
                    ConstraintJointAxis.LINEAR_Z,
                    this.localGoal.z(),
                    linearStiffness,
                    linearDamping,
                    true,
                    maxForce
            );

            if(subLevel.logicalPose().position().distance(player.getX(), player.getY(), player.getZ()) > 10) {
                //x and z are swapped
                handle.teleport(new Vector3d(player.getX(), player.getY(), player.getZ()), new Quaterniond());
            }


            //constraintHandle = (RotaryConstraintHandle)pipeline.addConstraint(containingSubLevel, plateSubLevel, constraint);
//            this.removeJoint();
//            Player player = HarnessBlockEntity.this.level != null ? HarnessBlockEntity.this.level.getPlayerByUUID(this.playerId) : null;
//            if (player != null) {
//                boolean isJumping = (player.getDeltaMovement().y > 0 || player.getDeltaMovement().y < 0) && !player.onGround();
//                if (player.onGround() || isJumping || player.isInWater() || player.getAbilities().flying || player.onClimbable()) {
//                    SubLevel standingSubLevel = Sable.HELPER.getTrackingSubLevel(player);
//                    if (standingSubLevel != subLevel) {
//                        Vector3d constraintGoal = JOMLConversion.toJOML(player.getEyePosition().add(player.getLookAngle().scale(Math.max((double)2.0F, (double)this.scrollDistance))));
//                        Vector3d constraintPosition = HarnessBlockEntity.this.getGrabCenter();
//                        Vec3 look = player.getLookAngle();
//                        double yawRad = Math.atan2(look.x, look.z);
//
//                        BlockState state = HarnessBlockEntity.this.getBlockState();
//                        Direction facing = state.getValue(DirectionalBlock.FACING);
//                        boolean axisAlongFirst = state.getValue(AbstractDirectionalAxisBlock.AXIS_ALONG_FIRST_COORDINATE);
//                        Vector3d forward = new Vector3d(
//                                facing.getStepX(),
//                                facing.getStepY(),
//                                facing.getStepZ()
//                        );
//                        Vector3d up = Math.abs(forward.y) > 0.999D
//                                ? new Vector3d(0, 0, 1)
//                                : new Vector3d(0, 1, 0);
//                        Quaterniond initialRot = new Quaterniond()
//                                .lookAlong(forward, up)
//                                .rotateY(Math.PI);
//                        if (!axisAlongFirst) {
//                            // facing方向に応じた回転軸の選択
//                            switch (facing) {
//                                case NORTH, SOUTH ->
//                                        initialRot.rotateZ((Math.PI / 2.0D) * (facing.equals(Direction.NORTH) ? 1 : -1));
//                                case UP, DOWN ->
//                                        initialRot.rotateY((Math.PI / 2.0D) * (facing.equals(Direction.UP) ? 1 : -1));
//                            }
//                        } else {
//                            switch (facing) {
//                                case EAST, WEST ->
//                                        initialRot.rotateX((Math.PI / 2.0D) * (facing.equals(Direction.EAST) ? 1 : -1));
//                            }
//                        }
//
//                        // Y座標の健全性チェックと制限
//                        final double MAX_Y_COORDINATE = 1000.0D;
//                        boolean validConstraintGoal = !Double.isNaN(constraintGoal.y) && !Double.isInfinite(constraintGoal.y) && Math.abs(constraintGoal.y) <= MAX_Y_COORDINATE;
//                        boolean validConstraintPosition = !Double.isNaN(constraintPosition.y) && !Double.isInfinite(constraintPosition.y) && Math.abs(constraintPosition.y) <= MAX_Y_COORDINATE;
//
//                        if (validConstraintGoal && validConstraintPosition) {
//                            double validRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue() + (double)2.0F;
//                            double currentDistance = Sable.HELPER.distanceSquaredWithSubLevels(HarnessBlockEntity.this.level, constraintGoal, constraintPosition);
//                            if (!Mth.equal(-1.0F, this.scrollDistance) && !(currentDistance > validRange * validRange)) {
//                                ServerSubLevelContainer container = SubLevelContainer.getContainer(subLevel.getLevel());
//
//                                assert container != null;
//
//                                SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
//                                Quaterniond quaterniond = new Quaterniond().rotateY(-yawRad).invert().mul(initialRot);//.rotateY(Math.PI);
//                                this.orientation.set(quaterniond);
//                                this.orientation.transformInverse(JOMLConversion.toJOML(player.getEyePosition()));
//                                FreeConstraintConfiguration configuration = new FreeConstraintConfiguration(JOMLConversion.ZERO, constraintPosition, this.orientation);// quaterniond);
//                                this.constraintHandle = physicsSystem.getPipeline().addConstraint(null, subLevel, configuration);
//                                if (this.constraintHandle != null) {
//                                    final SimPhysics config = SimConfigService.INSTANCE.server().physics;
//
//                                    final double stiffnessConstant = 3.0;
//                                    final double dampingConstant = 6.0;
//                                    double maxForce = (double) config.handleMaxForce.getF();
//
//                                    // Angular: lock orientation by setting motors with high stiffness/damping
//                                    final double angularStiffness = (double) config.physicsStaffAngularStiffness.getF()*stiffnessConstant;
//                                    final double angularDamping = (double) config.physicsStaffAngularDamping.getF()*dampingConstant;
//                                    for (ConstraintJointAxis axis : ConstraintJointAxis.ANGULAR) {
//                                        this.constraintHandle.setMotor(axis, 0.0, angularStiffness, angularDamping, true, maxForce);
//                                    }
//
//                                    // Compute the desired local goal in constraint space
//                                    Vector3dc vector3dc = JOMLConversion.toJOML(player.getLookAngle().scale(Math.max((double)2.0F, (double)this.scrollDistance)));
//
//                                    final double partialTick = physicsSystem.getPartialPhysicsTick();
//
//                                    final double eyePosX = Mth.lerp(partialTick, player.xOld, player.getX());
//                                    final double eyePosY = Mth.lerp(partialTick, player.yOld, player.getY()) + player.getEyeHeight();
//                                    final double eyePosZ = Mth.lerp(partialTick, player.zOld, player.getZ());
//
//                                    this.localGoal.set(player.getEyePosition().add(0, -0.6, 0).toVector3f());// set(vector3dc).add(eyePosX, eyePosY, eyePosZ);
//                                    this.orientation.transformInverse(this.localGoal);
//
//                                    final double linearStiffness = (double) config.physicsStaffLinearStiffness.getF()*stiffnessConstant;
//                                    final double linearDamping = (double) config.physicsStaffLinearDamping.getF()*dampingConstant;
//
//                                    // Linear motors: use goal offsets and moderate stiffness/damping
//                                    this.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_X, this.localGoal.x(), linearStiffness, linearDamping, true, maxForce);
//                                    this.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_Y, this.localGoal.y(), linearStiffness, linearDamping, true, maxForce);
//                                    this.constraintHandle.setMotor(ConstraintJointAxis.LINEAR_Z, this.localGoal.z(), linearStiffness, linearDamping, true, maxForce);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }

        public boolean hasJoint() {
            return this.constraintHandle != null;
        }

        public void removeJoint() {
            if (this.constraintHandle != null) {
                this.constraintHandle.remove();
                this.constraintHandle = null;
            }

        }
    }
}
