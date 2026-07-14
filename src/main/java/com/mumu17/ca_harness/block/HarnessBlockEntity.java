package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.mixin_interface.InputGetter;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import com.mumu17.ca_harness.mixin_interface.TempNoGravity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.blocks.handle.HandleBlockEntity;
import dev.simulated_team.simulated.config.server.physics.SimPhysics;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.service.SimConfigService;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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
            Minecraft.getInstance().player.connection.send(new ServerboundPlayerInputPacket(Minecraft.getInstance().player.xxa, Minecraft.getInstance().player.zza, Minecraft.getInstance().player.input.jumping, Minecraft.getInstance().player.input.shiftKeyDown));
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
        ServerSubLevel subLevel = (ServerSubLevel) Sable.HELPER.getContaining(this.getLevel(), this.getBlockPos());
        if(Objects.requireNonNull(level.getPlayerByUUID(player)).getMainHandItem().is(SimItems.PHYSICS_STAFF) && subLevel != null && !PhysicsStaffServerHandler.get((ServerLevel) level).isLocked(subLevel)) PhysicsStaffServerHandler.get((ServerLevel) level).toggleLock(subLevel.getUniqueId());
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

            VeilPacketManager.player((ServerPlayer) player).sendPacket(new PlayerTempGravityPacket(0));
            ((TempNoGravity) player).setCreateAeronauticsHarness$tempGravity(0);

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

            InputGetter.Inputs inputs = ((InputGetter) player).ca_harness$getInputs();
            Vector3d movement = new Vector3d();
            if(inputs.w()) movement.add(1, 0, 0);
            if(inputs.a()) movement.add(0, 0, 1);
            if(inputs.s()) movement.add(-1, 0, 0);
            if(inputs.d()) movement.add(0, 0, -1);

            subLevel.getOrCreateQueuedForceGroup(ForceGroups.PROPULSION.get()).applyAndRecordPointForce(subLevel.getMassTracker().getCenterOfMass(), movement);

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

            //rotation
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

            //movement
//            double linearStiffness = config.physicsStaffLinearStiffness.getF() * stiffnessScale;
//            double linearDamping = config.physicsStaffLinearDamping.getF() * dampingScale;
//
//            this.constraintHandle.setMotor(
//                    ConstraintJointAxis.LINEAR_X,
//                    this.localGoal.x(),
//                    linearStiffness,
//                    linearDamping,
//                    true,
//                    maxForce
//            );
//
//            this.constraintHandle.setMotor(
//                    ConstraintJointAxis.LINEAR_Y,
//                    this.localGoal.y(),
//                    linearStiffness,
//                    linearDamping,
//                    true,
//                    maxForce
//            );
//
//            this.constraintHandle.setMotor(
//                    ConstraintJointAxis.LINEAR_Z,
//                    this.localGoal.z(),
//                    linearStiffness,
//                    linearDamping,
//                    true,
//                    maxForce
//            );

//            if(subLevel.logicalPose().position().distance(player.getX(), player.getY(), player.getZ()) > 10) {
//                //x and z are swapped
//                handle.teleport(new Vector3d(player.getX(), player.getY(), player.getZ()), new Quaterniond());
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
