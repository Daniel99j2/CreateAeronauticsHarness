package com.mumu17.ca_harness.register;

import com.mumu17.ca_harness.block.HarnessBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.mumu17.ca_harness.register.ModRegistrate.REGISTRATE;

public class ModBlockEntityTypes {
    public static final BlockEntityEntry<HarnessBlockEntity> HARNESS = REGISTRATE.blockEntity("harness", HarnessBlockEntity::new).validBlock(ModBlocks.HARNESS).register();

    public static void register() {}
}
