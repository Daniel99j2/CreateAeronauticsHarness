package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.TempNoGravity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class)
public class EntityMixin {
    @Inject(method = "getGravity", at = @At(value = "HEAD"), cancellable = true)
    public void customGravity(CallbackInfoReturnable<Double> cir) {
        if(this instanceof TempNoGravity tempNoGravity && tempNoGravity.getCreateAeronauticsHarness$tempGravity() != Double.MAX_VALUE) {
            cir.setReturnValue(tempNoGravity.getCreateAeronauticsHarness$tempGravity());
        }
    }
}
