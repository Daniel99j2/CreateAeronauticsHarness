package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.CAHarness;
import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record PlayerVelocityPacket(Vector3f velocity, boolean allRelative, boolean limitVelocity) implements CustomPacketPayload {
    public static final Type<PlayerVelocityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CAHarness.MODID, "local_velocity"));
    public static final float RELATIVE = 1234567890.1234567890f;

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVelocityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, PlayerVelocityPacket::velocity,
            ByteBufCodecs.BOOL, PlayerVelocityPacket::allRelative,
            ByteBufCodecs.BOOL, PlayerVelocityPacket::limitVelocity,
            PlayerVelocityPacket::new
    );

    public void handle(final ClientPacketContext context) {
        handle(this, context.player());
    }

    private static void handle(final PlayerVelocityPacket packet, LocalPlayer player) {
        if(packet.allRelative) {
            Vec3 newVelocity = new Vec3(packet.velocity);
            if(packet.limitVelocity) {
                Vec3 current = player.getDeltaMovement();

                player.addDeltaMovement(new Vec3(
                        apply(current.x, packet.velocity.x, packet.velocity.x),
                        apply(current.y, packet.velocity.y, packet.velocity.y),
                        apply(current.z, packet.velocity.z, packet.velocity.z))
                );
            } else player.addDeltaMovement(newVelocity);
        } else {
            Vec3 newVelocity = new Vec3(packet.velocity);
            if(packet.velocity.x == RELATIVE) {
                newVelocity = new Vec3(player.getDeltaMovement().x, newVelocity.y, newVelocity.z);
            }
            if(packet.velocity.y == RELATIVE) {
                newVelocity = new Vec3(newVelocity.x, player.getDeltaMovement().y, newVelocity.z);
            }
            if(packet.velocity.z == RELATIVE) {
                newVelocity = new Vec3(newVelocity.x, newVelocity.y, player.getDeltaMovement().z);
            }
            player.setDeltaMovement(newVelocity);
        }
    }

    private static double apply(double current, double add, double max) {
        if(current + add > max && max > 0) {
            return add-current;
        }
        if(current + add < max && max < 0) {
            return add-current;
        }
        return add;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}