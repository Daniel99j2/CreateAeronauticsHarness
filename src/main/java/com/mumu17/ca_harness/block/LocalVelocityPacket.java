package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record LocalVelocityPacket(Vector3f velocity) implements CustomPacketPayload {
    public static final Type<LocalVelocityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CAHarness.MODID, "set_local_velocity"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LocalVelocityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, LocalVelocityPacket::velocity,
            LocalVelocityPacket::new
    );

    public void handle(final ServerPacketContext context) {
        handle(this, context.player());
    }

    private static void handle(final LocalVelocityPacket packet, ServerPlayer player) {
        ((PlayerHarnessExtension) player).ca_harness$setLastDelta(new Vec3(packet.velocity));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}