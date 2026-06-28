package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record UpdateLocalPlayerUsingHarnessPacket(BlockPos harnessPos) implements CustomPacketPayload {
    public static final Type<UpdateLocalPlayerUsingHarnessPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CAHarness.MODID, "set_localplayer_harness"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateLocalPlayerUsingHarnessPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, UpdateLocalPlayerUsingHarnessPacket::harnessPos,
            UpdateLocalPlayerUsingHarnessPacket::new
    );

    public void handle(final ClientPacketContext context) {
        handle(this, context.player());
    }

    private static void handle(final UpdateLocalPlayerUsingHarnessPacket packet, LocalPlayer player) {
        PlayerHarnessExtension playerHarnessExtension = (PlayerHarnessExtension) player;
        playerHarnessExtension.ca_harness$setHarnessPos(packet.harnessPos != BlockPos.ZERO ? packet.harnessPos : null);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}