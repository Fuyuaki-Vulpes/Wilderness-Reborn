package com.fuyuaki.r_wilderness.world.block.tree;

import com.fuyuaki.r_wilderness.world.block.state.properties.RBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jspecify.annotations.Nullable;

public class BranchBlock extends PipeBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<BranchBlock> CODEC = simpleCodec(BranchBlock::new);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> SUPPORT = RBlockStateProperties.SUPPORT;

    public BranchBlock(Properties properties) {
        super(8,properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
                WATERLOGGED,SUPPORT,
                NORTH,EAST,SOUTH,WEST,UP,DOWN);
    }


    public static boolean isConnected(BlockState state, Direction direction){
        return state.getValue(PROPERTY_BY_DIRECTION.get(direction));
    }


    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState originalState = this.defaultBlockState();
        LevelReader levelreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        for (Direction direction : Direction.values()){
            BlockState state = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
            originalState.setValue(PROPERTY_BY_DIRECTION.get(direction),mayConnect(state));
        }
        Direction[] adirection = context.getNearestLookingDirections();
        for (Direction direction : adirection){
            originalState.setValue(SUPPORT,direction);
            if (originalState.canSurvive(levelreader, blockpos)) {
                return originalState;
            }
        }
        return originalState;
    }

    public static Boolean mayConnect(BlockState state) {
        if (state.is(BlockTags.LEAVES)) return true;
        return isTreePart(state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public static boolean isNextToTreePart(Level level, BlockPos pos, Direction originDir) {
        for (Direction dir : Direction.values()) {
            if (!dir.equals(originDir)) {
                if (isTreePart(level.getBlockState(pos.relative(dir)))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!mayConnect(level.getBlockState(pos.relative(state.getValue(SUPPORT))))){
            level.destroyBlock(pos,true);
        }
        super.tick(state, level, pos, random);
    }



    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    private static boolean isTreePart(BlockState blockState) {
        return blockState.getBlock() instanceof BranchBlock || blockState.is(BlockTags.LOGS);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isTreePart(level.getBlockState(pos.relative(state.getValue(SUPPORT))));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        boolean flag = mayConnect(neighborState);

        return state.setValue(PROPERTY_BY_DIRECTION.get(direction),flag);
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return CODEC;
    }
}
