package com.fuyuaki.wilderness_reborn.world.level.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class ModSoilBlock extends Block {
    public ModSoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (ItemAbilities.HOE_TILL == itemAbility) {
            Block block = state.getBlock();
            if ((block == ModBlocks.CHALKY_SOIL.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  ModBlocks.CHALKY_FARMLAND.get().defaultBlockState();
            }
            else if ((block == ModBlocks.CLAY_SOIL.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  ModBlocks.CLAY_FARMLAND.get().defaultBlockState();
            }
            else if ((block == ModBlocks.PEAT.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  ModBlocks.PEAT_FARMLAND.get().defaultBlockState();
            }
            else if ((block == ModBlocks.SANDY_SOIL.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  ModBlocks.SANDY_FARMLAND.get().defaultBlockState();
            }
            else if ((block == ModBlocks.SILT.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  ModBlocks.SILT_FARMLAND.get().defaultBlockState();
            }
        }
        return null;
    }
}
