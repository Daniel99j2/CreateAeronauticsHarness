package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setPosition(double p_90585_, double p_90586_, double p_90587_);

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER))
    private void applySableZoom(BlockGetter level, Entity entity, boolean detached, boolean thirdPerson, float partialTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || entity != mc.player) return;
        if (((PlayerHarnessExtension) mc.player).ca_harness$getHarnessPos() == null) return;


        if (mc.options.getCameraType() != CameraType.THIRD_PERSON_BACK) return;

        double maxDistance = 16.0;
        double minDistance = 2.0;

        double zoom = getCollisionZoom((Camera)(Object)this, entity, maxDistance);

        zoom = Math.max(minDistance, zoom);

        Vec3 basePos = entity.getEyePosition(partialTick);
        Vec3 forward = entity.getLookAngle();

        Vec3 targetPos = basePos.add(forward.scale(-zoom)).add(0, 1.5, 0);

        this.setPosition(targetPos.x, targetPos.y, targetPos.z);
    }

    @Unique
    private static double getCollisionZoom(Camera camera, Entity entity, double maxDistance) {

        Vec3 camPos = camera.getPosition();
        Vec3 forward = new Vec3(camera.getLookVector());

        Level level = entity.level();

        double margin = 3.0;
        double zoom = maxDistance;

        for (int i = 0; i < 8; i++) {

            double offsetX = ((i & 1) * 2 - 1) * 0.1;
            double offsetY = (((i >> 1) & 1) * 2 - 1) * 0.1;
            double offsetZ = (((i >> 2) & 1) * 2 - 1) * 0.1;

            Vec3 start = camPos.add(offsetX, offsetY, offsetZ);

            Vec3 end = start.add(forward.scale(-(zoom + margin)));

            HitResult hit = level.clip(new ClipContext(
                    start,
                    end,
                    ClipContext.Block.VISUAL,
                    ClipContext.Fluid.NONE,
                    entity
            ));

            if (hit.getType() != HitResult.Type.MISS) {

                double dist = hit.getLocation().distanceTo(camPos);

                double safeDist = dist - margin;

                if (safeDist < zoom) {
                    zoom = safeDist;
                }
            }
        }

        return zoom;
    }
}

