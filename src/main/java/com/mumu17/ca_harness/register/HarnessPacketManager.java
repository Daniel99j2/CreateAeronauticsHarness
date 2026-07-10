package com.mumu17.ca_harness.register;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.block.*;
import foundry.veil.api.network.VeilPacketManager;

public class HarnessPacketManager {
    public static final VeilPacketManager INSTANCE = VeilPacketManager.create(CAHarness.MODID, "0.2");

    public static void init() {
        INSTANCE.registerServerbound(UpdatePlayerUsingHarnessPacket.TYPE, UpdatePlayerUsingHarnessPacket.CODEC, UpdatePlayerUsingHarnessPacket::handle);
        INSTANCE.registerServerbound(BodyRotationPacket.TYPE, BodyRotationPacket.CODEC, BodyRotationPacket::handle);
        INSTANCE.registerServerbound(LocalVelocityPacket.TYPE, LocalVelocityPacket.CODEC, LocalVelocityPacket::handle);
        INSTANCE.registerClientbound(PlayerVelocityPacket.TYPE, PlayerVelocityPacket.CODEC, PlayerVelocityPacket::handle);
        INSTANCE.registerClientbound(StopHarnessPacket.TYPE, StopHarnessPacket.CODEC, StopHarnessPacket::handle);
        INSTANCE.registerClientbound(PlayerTempGravityPacket.TYPE, PlayerTempGravityPacket.CODEC, PlayerTempGravityPacket::handle);
        INSTANCE.registerClientbound(UpdateLocalPlayerUsingHarnessPacket.TYPE, UpdateLocalPlayerUsingHarnessPacket.CODEC, UpdateLocalPlayerUsingHarnessPacket::handle);
    }
}
