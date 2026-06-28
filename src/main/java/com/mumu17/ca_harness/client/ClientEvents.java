package com.mumu17.ca_harness.client;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.keybind.ModKeybinds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = CAHarness.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.STOP_HARNESS.get());
    }
}

