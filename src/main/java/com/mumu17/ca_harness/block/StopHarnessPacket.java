package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.client.ClientTickHandler;
import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record StopHarnessPacket(boolean sendResponse) implements CustomPacketPayload {
    public static final Type<StopHarnessPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CAHarness.MODID, "stop_harness"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StopHarnessPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, StopHarnessPacket::sendResponse,
            StopHarnessPacket::new
    );

    public void handle(final ClientPacketContext context) {
        handle(this, context.player());
    }

    private static void handle(final StopHarnessPacket packet, LocalPlayer player) {
        ClientTickHandler.reset(packet.sendResponse);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}