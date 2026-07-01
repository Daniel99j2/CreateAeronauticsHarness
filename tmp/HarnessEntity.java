package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.keybind.ModKeybinds;
import com.mumu17.ca_harness.register.ModEntityTypes;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class HarnessEntity extends Entity implements IEntityWithComplexSpawn {
    public HarnessEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public HarnessEntity(Level level) {
        this(ModEntityTypes.HARNESS.get(), level);
        // noPhysics = true;
        this.ejectPassengers();
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<HarnessEntity> entityBuilder = (EntityType.Builder<HarnessEntity>) builder;
        return entityBuilder.sized(0.25f, 0.35f);
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        AABB bb = getBoundingBox();
        Vec3 diff = new Vec3(x, y, z).subtract(bb.getCenter());
        setBoundingBox(bb.move(diff));
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return false;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        callback.accept(passenger, passenger.getX(), passenger.getY(), passenger.getZ());
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        entity.setYHeadRot(entity.getYRot());
    }

    @Override
    public void tick() {
        if (level().isClientSide)
            return;
        boolean blockPresent = level().getBlockState(blockPosition())
                .getBlock() instanceof HarnessBlock;
        if (!ModKeybinds.STOP_HARNESS.get().isDown() && blockPresent)
            return;
        CAHarness.LOGGER.debug("{}, {}", !ModKeybinds.STOP_HARNESS.get().isDown(), blockPresent);
        this.discard();
    }

    @Override
    protected boolean canRide(Entity entity) {
        // Fake Players (tested with deployers) have a BUNCH of weird issues, don't let
        // them ride harnesss
        return !(entity instanceof FakePlayer);
    }

    @Override
    protected void removePassenger(Entity entity) {
        if (entity instanceof Player player) {
            if (!ModKeybinds.STOP_HARNESS.get().isDown()) {
                return;
            }
        }
        super.removePassenger(entity);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity pLivingEntity) {
        return super.getDismountLocationForPassenger(pLivingEntity).add(0, 0, 0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    public static class Render extends EntityRenderer<HarnessEntity> {

        public Render(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public boolean shouldRender(HarnessEntity harnessEntity, Frustum frustum, double p_225626_3_, double p_225626_5_,
                                    double p_225626_7_) {
            return false;
        }

        @Override
        public ResourceLocation getTextureLocation(HarnessEntity harnessEntity) {
            return null;
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {}

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {}
}