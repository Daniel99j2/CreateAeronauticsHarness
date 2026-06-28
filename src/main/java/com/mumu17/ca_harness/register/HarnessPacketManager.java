package com.mumu17.ca_harness.register;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.block.UpdateLocalPlayerUsingHarnessPacket;
import com.mumu17.ca_harness.block.UpdatePlayerUsingHarnessPacket;
import foundry.veil.api.network.VeilPacketManager;

public class HarnessPacketManager {
    public static final VeilPacketManager INSTANCE = VeilPacketManager.create(CAHarness.MODID, "0.1");

    public static void init() {
        INSTANCE.registerServerbound(UpdatePlayerUsingHarnessPacket.TYPE, UpdatePlayerUsingHarnessPacket.CODEC, UpdatePlayerUsingHarnessPacket::handle);
        INSTANCE.registerClientbound(UpdateLocalPlayerUsingHarnessPacket.TYPE, UpdateLocalPlayerUsingHarnessPacket.CODEC, UpdateLocalPlayerUsingHarnessPacket::handle);
    }
}
