package com.mumu17.ca_harness.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import com.mumu17.ca_harness.CAHarness;
import net.minecraft.client.KeyMapping;
import net.neoforged.jarjar.nio.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static final Lazy<KeyMapping> STOP_HARNESS = Lazy.of(() ->
            new KeyMapping(
                    "key."+ CAHarness.MODID +".stop_harness",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_H,
                    "key.categories.ca_harness"
            )
    );
}

