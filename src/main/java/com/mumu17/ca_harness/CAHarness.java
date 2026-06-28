package com.mumu17.ca_harness;

import com.mojang.logging.LogUtils;
import com.mumu17.ca_harness.register.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CAHarness.MODID)
public class CAHarness {
    public static final String MODID = "ca_harness";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CAHarness(IEventBus bus) {
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModRegistrate.register(bus);
        ModCreativeTabs.register(bus);
        HarnessPacketManager.init();
    }

}
