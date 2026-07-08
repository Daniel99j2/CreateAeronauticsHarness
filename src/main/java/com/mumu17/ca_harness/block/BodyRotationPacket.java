package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.CAHarness;
import foundry.veil.api.network.handler.ClientPacketContext;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record BodyRotationPacket(float rotation) implements CustomPacketPayload {
    public static final Type<BodyRotationPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CAHarness.MODID, "body_rotation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyRotationPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, BodyRotationPacket::rotation,
            BodyRotationPacket::new
    );

    public void handle(final ServerPacketContext context) {
        handle(this, context.player());
    }

    private static void handle(final BodyRotationPacket packet, ServerPlayer player) {
        player.yBodyRot = packet.rotation();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}