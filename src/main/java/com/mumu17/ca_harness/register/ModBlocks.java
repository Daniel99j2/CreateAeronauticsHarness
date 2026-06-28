package com.mumu17.ca_harness.register;

import com.mumu17.ca_harness.CAHarness;
import com.mumu17.ca_harness.block.HarnessBlock;
import com.mumu17.ca_harness.block.HarnessBlockEntity;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;

public class ModBlocks {

    public static final BlockEntry<HarnessBlock> HARNESS;

    static {
        HARNESS = ModRegistrate.REGISTRATE
                .block("harness", (p) -> new HarnessBlock(p, null, HarnessBlock.Variant.IRON))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.noCollission().noOcclusion())
                .blockEntity(HarnessBlockEntity::new).build()
                .defaultBlockstate()
                .loot(RegistrateBlockLootTables::dropSelf)
                .onRegisterAfter(Registries.ITEM, (v) -> ItemDescription.useKey(v, "block."+ CAHarness.MODID +".harness"))
                .simpleItem().register();
    }

    public static void register() {}

}
