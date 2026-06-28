package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import dev.simulated_team.simulated.content.blocks.handle.ServerHandleHoldingHandler;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record UpdatePlayerUsingHarnessPacket(BlockPos interactionPos) implements CustomPacketPayload {

    public static StreamCodec<RegistryFriendlyByteBuf, UpdatePlayerUsingHarnessPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, UpdatePlayerUsingHarnessPacket::interactionPos,
            UpdatePlayerUsingHarnessPacket::new);

    public static Type<UpdatePlayerUsingHarnessPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CAHarness.MODID, "remove_player_harness"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final ServerPacketContext ctx) {
        final ServerPlayer player = ctx.player();
        final Level level = ctx.level();

        PlayerHarnessExtension playerHarnessExtension = (PlayerHarnessExtension) player;
        BlockPos harnessPos = playerHarnessExtension.ca_harness$getHarnessPos();
        if (harnessPos != null) {
            ServerHandleHoldingHandler.stopHolding(player);
            final BlockEntity be = level.getBlockEntity(harnessPos);
            if (be instanceof final HarnessBlockEntity hbe)
                hbe.ca_harness$stopGrabbingServer(player.getUUID());
        }
        playerHarnessExtension.ca_harness$setHarnessPos(null);
    }
}
