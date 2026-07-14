package com.mumu17.ca_harness.mixin;

import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import com.mumu17.ca_harness.mixin_interface.TempNoGravity;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class)
public abstract class EntityHarnessMixin {
    @Shadow
    private Level level;

    @Shadow
    public abstract void setPos(Vec3 p_146885_);

    @Inject(method = "getGravity", at = @At(value = "HEAD"), cancellable = true)
    public void customGravity(CallbackInfoReturnable<Double> cir) {
        if(this instanceof TempNoGravity tempNoGravity && tempNoGravity.getCreateAeronauticsHarness$tempGravity() != Double.MAX_VALUE) {
            cir.setReturnValue(tempNoGravity.getCreateAeronauticsHarness$tempGravity());
        }
    }

    @Inject(method = "getDeltaMovement", at = @At(value = "HEAD"), cancellable = true)
    public void noMovement(CallbackInfoReturnable<Vec3> cir) {
        if(((Entity) (Object) this) instanceof Player player && player instanceof PlayerHarnessExtension he && he.ca_harness$getHarnessPos() != null) {
            cir.setReturnValue(Vec3.ZERO);
        }
    }

    @Inject(method = "position", at = @At(value = "HEAD"))
    public void harnessedPos(CallbackInfoReturnable<Vec3> cir) {
        if(((Entity) (Object) this) instanceof Player player && player instanceof PlayerHarnessExtension he && he.ca_harness$getHarnessPos() != null && Sable.HELPER.getContaining(player.level(), he.ca_harness$getHarnessPos()) != null) {
            Vector3d playerPos = new Vector3d();
            BlockPos origin = Sable.HELPER.getContaining(player.level(), he.ca_harness$getHarnessPos()).getPlot().getCenterBlock();
            playerPos.add(0, -player.getEyeHeight(), 0);
            playerPos.add(he.ca_harness$getHarnessPos().getX()-origin.getX(), he.ca_harness$getHarnessPos().getY()-origin.getY(), he.ca_harness$getHarnessPos().getZ()-origin.getZ());
            Sable.HELPER.getContaining(player.level(), he.ca_harness$getHarnessPos()).logicalPose().orientation().transform(playerPos);
            playerPos.add(Sable.HELPER.getContaining(player.level(), he.ca_harness$getHarnessPos()).logicalPose().position());
            this.setPos(new Vec3(playerPos.x(), playerPos.y(), playerPos.z()));
        }
    }
}
