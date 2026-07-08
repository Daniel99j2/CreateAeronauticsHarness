package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.mixin_interface.TempNoGravity;
import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlayerTempGravityPacket(double gravity) implements CustomPacketPayload {
    public static final Type<PlayerTempGravityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CAHarness.MODID, "temp_gravity"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerTempGravityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, PlayerTempGravityPacket::gravity,
            PlayerTempGravityPacket::new
    );

    public void handle(final ClientPacketContext context) {
        handle(this, context.player());
    }

    private static void handle(final PlayerTempGravityPacket packet, LocalPlayer player) {
        ((TempNoGravity) player).setCreateAeronauticsHarness$tempGravity(packet.gravity);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}