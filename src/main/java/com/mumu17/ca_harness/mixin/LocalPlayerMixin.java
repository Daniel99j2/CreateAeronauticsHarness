package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

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
