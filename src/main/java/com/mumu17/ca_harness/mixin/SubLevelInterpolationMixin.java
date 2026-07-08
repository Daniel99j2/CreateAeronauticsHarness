package com.mumu17.ca_harness.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.network.client.SubLevelSnapshotInterpolator;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SubLevelSnapshotInterpolator.class)
public abstract class SubLevelInterpolationMixin {

    @Shadow
    public abstract void getSampleAt(double gameTick, Pose3d dest);

    @Unique
    private static volatile boolean alreadyCalled = false;

    @Inject(method = "getSampleAt", at = @At(value = "HEAD"), cancellable = true)
    private void sampler(double gameTick, Pose3d dest, CallbackInfo ci) {
        if(CAHarness.activeIntepolatingLevel == null) throw new RuntimeException("Something is calling getSampleAt!");
        if(Minecraft.getInstance().player instanceof PlayerHarnessExtension harness
                && harness.ca_harness$getHarnessPos() != null
                && Sable.HELPER.getContaining(Minecraft.getInstance().player.level(), harness.ca_harness$getHarnessPos()) == CAHarness.activeIntepolatingLevel) {
            if (alreadyCalled) return;
            alreadyCalled = true;
            ci.cancel();
            this.getSampleAt(gameTick + 1000, dest);
            alreadyCalled = false;
        }
    }
}

