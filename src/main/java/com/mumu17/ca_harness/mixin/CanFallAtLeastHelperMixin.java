package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.mixinhelpers.CanFallAtleastHelper;
import dev.ryanhcode.sable.network.client.SubLevelSnapshotInterpolator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CanFallAtleastHelper.class)
public abstract class CanFallAtLeastHelperMixin {
    @Inject(method = "canFallAtleastWithSubLevels", at = @At(value = "HEAD"), cancellable = true)
    private static void noTrapdoorClimbThingy(Level level, AABB aabb, CallbackInfoReturnable<Vector3d> cir) {
        cir.setReturnValue(null);
    }
}

