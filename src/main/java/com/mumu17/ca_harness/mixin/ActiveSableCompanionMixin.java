package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.SubLevelHarnessData;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.mass.MergedMassTracker;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ActiveSableCompanion.class)
public abstract class ActiveSableCompanionMixin {
    @Inject(method = "getVelocity(Lnet/minecraft/world/level/Level;Ldev/ryanhcode/sable/companion/SubLevelAccess;Lorg/joml/Vector3dc;Lorg/joml/Vector3d;)Lorg/joml/Vector3d;", at = @At(value = "HEAD"), cancellable = true)
    private void usePlayerVelocity(Level level, SubLevelAccess subLevel, Vector3dc pos, Vector3d dest, CallbackInfoReturnable<Vector3d> cir) {
        if(subLevel instanceof SubLevelHarnessData hd && hd.getCreateAeronauticsHarness$harnessedPlayer() != null) {
            Player player = level.getPlayerByUUID(hd.getCreateAeronauticsHarness$harnessedPlayer());
            if(player != null) {
                cir.setReturnValue(new Vector3d(player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z));
            }
        }
    }
}

