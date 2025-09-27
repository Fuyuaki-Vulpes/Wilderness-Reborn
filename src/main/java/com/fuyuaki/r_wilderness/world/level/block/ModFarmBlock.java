package com.fuyuaki.r_wilderness.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

public class ModFarmBlock extends FarmBlock {

    public ModFarmBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (!state.canSurvive(level, pos)) {
            turnToDirt(null, state, level, pos);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        int i = state.getValue(MOISTURE);
        if (!isNearWater(level, pos) && !level.isRainingAt(pos.above())) {
            if (i > 0) {
                level.setBlock(pos, state.setValue(MOISTURE, i - 1), 2);
            } else if (!shouldMaintainFarmland(level, pos)) {
                turnToDirt(null, state, level, pos);
            }
        } else if (i < 7) {
            level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (level instanceof ServerLevel serverlevel) {
            if (CommonHooks.onFarmlandTrample(serverlevel, pos, Blocks.DIRT.defaultBlockState(), fallDistance, entity)) {
                turnToDirt(entity, state, level, pos);
            }
            entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
        }
    }

    public static void turnToDirt(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        Block block = state.getBlock();
        if (block == ModBlocks.CHALKY_FARMLAND.get()) {
            BlockState blockstate = pushEntitiesUp(state, ModBlocks.CHALKY_SOIL.get().defaultBlockState(), level, pos);
            level.setBlockAndUpdate(pos, blockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        }
        else if (block == ModBlocks.CLAY_FARMLAND.get()) {
            BlockState blockstate = pushEntitiesUp(state, ModBlocks.CLAY_SOIL.get().defaultBlockState(), level, pos);
            level.setBlockAndUpdate(pos, blockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        }
        else if (block == ModBlocks.PEAT_FARMLAND.get()) {
            BlockState blockstate = pushEntitiesUp(state, ModBlocks.PEAT.get().defaultBlockState(), level, pos);
            level.setBlockAndUpdate(pos, blockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        }
        else if (block == ModBlocks.SANDY_FARMLAND.get()) {
            BlockState blockstate = pushEntitiesUp(state, ModBlocks.SANDY_SOIL.get().defaultBlockState(), level, pos);
            level.setBlockAndUpdate(pos, blockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        }
        else {
            BlockState blockstate = pushEntitiesUp(state, ModBlocks.SILT.get().defaultBlockState(), level, pos);
            level.setBlockAndUpdate(pos, blockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        }
    }

    private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
    }

    private static boolean isNearWater(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (state.canBeHydrated(level, pos, level.getFluidState(blockpos), blockpos)) {
                return true;
            }
        }

        return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
            return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos()) ? ModBlocks.CHALKY_SOIL.get().defaultBlockState() : super.getStateForPlacement(context);

    }

}
