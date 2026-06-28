package com.mumu17.ca_harness.client;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.block.UpdatePlayerUsingHarnessPacket;
import com.mumu17.ca_harness.keybind.ModKeybinds;
import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CAHarness.MODID, value = Dist.CLIENT)
public class ClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (ModKeybinds.STOP_HARNESS.get().consumeClick()) {
            reset();
        }
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        reset();
    }

    public static void reset() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        Player player = mc.player;
        assert player != null;

        PlayerHarnessExtension playerHarnessExtension = (PlayerHarnessExtension) player;
        playerHarnessExtension.ca_harness$setHarnessPos(null);
        VeilPacketManager.server().sendPacket(new UpdatePlayerUsingHarnessPacket(BlockPos.ZERO));
    }
}

