package com.mumu17.ca_harness.block;

import com.mumu17.ca_harness.mixin_interface.PlayerHarnessExtension;
import com.mumu17.ca_harness.register.ModBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.simulated_team.simulated.content.blocks.handle.HandleBlock;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HarnessBlock extends HandleBlock implements IWrenchable {

    public HarnessBlock(Properties properties, @Nullable DyeColor dyeColor, Variant variant) {
        super(properties, dyeColor, variant);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        return switch (dir) {
            case NORTH -> Block.box(0, 0, 14, 16, 16, 16);
            case SOUTH -> Block.box(0, 0, 0, 16, 16, 2);
            case WEST  -> Block.box(14, 0, 0, 16, 16, 16);
            case EAST  -> Block.box(0, 0, 0, 2, 16, 16);
            case UP -> Block.box(0, 0, 0, 16, 2, 16);
            case DOWN -> Block.box(0, 14, 0, 16, 16, 16);
        };
    }


    @Override
    public BlockEntityType<? extends HarnessBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.HARNESS.get();
    }

    public static void carryOn(Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player))
            return;
        PlayerHarnessExtension playerHarnessExtension = (PlayerHarnessExtension) player;
        playerHarnessExtension.ca_harness$setHarnessPos(pos != BlockPos.ZERO ? pos : null);
        VeilPacketManager.player(player).sendPacket(new UpdateLocalPlayerUsingHarnessPacket(pos));
    }
}


