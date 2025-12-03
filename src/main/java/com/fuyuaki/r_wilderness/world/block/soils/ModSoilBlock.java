
package com.fuyuaki.r_wilderness.world.block.soils;

import com.fuyuaki.r_wilderness.world.block.RBlocks;
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
            if ((block == RBlocks.CHALKY_SOIL.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  RBlocks.CHALKY_FARMLAND.get().defaultBlockState();
            }
            else if ((block == RBlocks.CLAY_SOIL.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  RBlocks.CLAY_FARMLAND.get().defaultBlockState();
            }
            else if ((block == RBlocks.PEAT.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  RBlocks.PEAT_FARMLAND.get().defaultBlockState();
            }
            else if ((block == RBlocks.SANDY_SOIL.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  RBlocks.SANDY_FARMLAND.get().defaultBlockState();
            }
            else if ((block == RBlocks.SILT.get()) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return  RBlocks.SILT_FARMLAND.get().defaultBlockState();
            }
        }
        return null;
    }
}
