package com.mumu17.ca_harness.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SubLevelEntityCollision.class)
public abstract class NoCollisionMixin {
    @WrapOperation(method = "collide", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/ActiveSableCompanion;getAllIntersecting(Lnet/minecraft/world/level/Level;Ldev/ryanhcode/sable/companion/math/BoundingBox3dc;)Ljava/lang/Iterable;"))
    private static Iterable<SubLevel> e(ActiveSableCompanion instance, Level level, BoundingBox3dc bounds, Operation<Iterable<SubLevel>> original, @Local(argsOnly = true) Entity entity) {
        Iterable<SubLevel> real = original.call(instance, level, bounds);
        if(entity instanceof Player player && player.level().isClientSide && player instanceof PlayerHarnessExtension harness && harness.ca_harness$getHarnessPos() != null) {
            List<SubLevel> subLevels = new ArrayList<>();
            real.forEach(subLevels::add);
            subLevels.remove(Sable.HELPER.getContaining(player.level(), harness.ca_harness$getHarnessPos()));
            return subLevels;
        }
        return real;
    }
}

