package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import dev.simulated_team.simulated.content.blocks.handle.PlayerHoldingHandleRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerHoldingHandleRenderer.class)
public class PlayerHoldingHandleRendererMixin {
    @Inject(method = "afterSetupAnim", at = @At("HEAD"), cancellable = true)
    private static void afterSetupAnim(Player player, HumanoidModel<?> model, CallbackInfo ci) {
        PlayerHarnessExtension playerHarnessExtension = (PlayerHarnessExtension) player;
        if (playerHarnessExtension.ca_harness$getHarnessPos() != null) {
            ci.cancel();
        }
    }

}
