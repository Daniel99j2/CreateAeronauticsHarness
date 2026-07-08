package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.CAHarness;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.network.client.ClientSableInterpolationState;
import dev.ryanhcode.sable.network.client.SubLevelSnapshotInterpolator;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientSubLevel.class)
public abstract class SubLevelInterpolationMixin2 extends SubLevel {
    protected SubLevelInterpolationMixin2(Level level, int plotX, int plotY, Pose3d pose) {
        super(level, plotX, plotY, pose);
    }

    @Inject(method = "setInitialPosesFrom", at = @At(value = "HEAD"))
    public void setLevel(ClientSableInterpolationState state, CallbackInfo ci) {
        CAHarness.activeIntepolatingLevel = (ClientSubLevel) (Object) this;
    }

    @Inject(method = "setInitialPosesFrom", at = @At(value = "TAIL"))
    public void resetLevel(ClientSableInterpolationState state, CallbackInfo ci) {
        CAHarness.activeIntepolatingLevel = null;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void setLevel2(CallbackInfo ci) {
        CAHarness.activeIntepolatingLevel = (ClientSubLevel) (Object) this;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void resetLevel2(CallbackInfo ci) {
        CAHarness.activeIntepolatingLevel = null;
    }

//    @Inject(method = "scaleSkyLight", at = @At(value = "TAIL"), cancellable = true)
//    public void noDark(int skyLight, CallbackInfoReturnable<Integer> cir) {
//        cir.setReturnValue(skyLight);
//    }
}

