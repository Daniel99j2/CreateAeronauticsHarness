package com.mumu17.ca_harness.register;

import com.mumu17.ca_harness.CAHarness;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.neoforged.bus.api.IEventBus;

public class ModRegistrate {

    public static final CreateRegistrate REGISTRATE =
            CreateRegistrate.create(CAHarness.MODID).defaultCreativeTab(ModCreativeTabs.MAIN_TAB.getKey());

    public static void register(IEventBus bus) {
        REGISTRATE.registerEventListeners(bus);
    }

}
