package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.mixin_interface.InputGetter;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import com.mumu17.ca_harness.mixin_interface.TempNoGravity;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.apache.http.MethodNotSupportedException;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayer.class)
public class LocalPlayerMixin implements PlayerHarnessExtension {
    @Unique
    BlockPos ca_harness$harnessPos = null;

    @Override
    public void ca_harness$setHarnessPos(@Nullable BlockPos pos) {
        ca_harness$harnessPos = pos;
    }

    @Override
    public BlockPos ca_harness$getHarnessPos() {
        return ca_harness$harnessPos;
    }
}
