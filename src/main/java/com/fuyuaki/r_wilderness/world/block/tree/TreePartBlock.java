package com.fuyuaki.r_wilderness.world.block.tree;

import com.fuyuaki.r_wilderness.world.block.state.properties.RBlockStateProperties;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public abstract class TreePartBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty THICKNESS = RBlockStateProperties.THICKNESS;
    public static final BooleanProperty CARVED = RBlockStateProperties.CARVED;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(
            Maps.newEnumMap(
                    Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST, Direction.UP, UP, Direction.DOWN, DOWN)
            )
    );

    public TreePartBlock(Properties properties) {
        super(properties);
        /*
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(THICKNESS,4)
                        .setValue(CARVED, false)
                        .setValue(WATERLOGGED,false)
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
        )
        ;*/
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
                WATERLOGGED,THICKNESS,CARVED,
                NORTH,EAST,SOUTH,WEST,UP,DOWN);
    }

    protected static AABB makeCube(double radius) {
        return new AABB(0.5 - radius, 0.5 - radius, 0.5 - radius, 0.5 + radius, 0.5 + radius, 0.5 + radius);
    }

    public static boolean isConnected(BlockState state, Direction direction){
        return state.getValue(PROPERTY_BY_DIRECTION.get(direction));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState originalState = this.defaultBlockState();
        for (Direction direction : Direction.values()){
            BlockState state = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
            originalState.trySetValue(PROPERTY_BY_DIRECTION.get(direction),mayConnect(state));
        }
        return originalState;
    }

    protected Boolean mayConnect(BlockState state) {
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int radius = getRadius(state);
        if (radius == 8) return Shapes.block();
        int[] radii = new int[7];

        radii[6] = radius;

        for (Direction dir : Direction.values()) {
            radii[dir.ordinal()] = Math.min(getSideConnectionRadius(level, pos, dir,radius), radius);
        }
        VoxelShape newShape = generateNewShape(radii);

        return newShape;
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

    protected int getRadius(BlockState state){
     return state.getValueOrElse(THICKNESS,0);
    }

    private static VoxelShape generateNewShape(int[] radii) {
        double radius = radii[6] / 16.0;
        VoxelShape shape = Shapes.create(makeCube(radius));
        for (Direction dir : Direction.values()) {
            double sideRadius = radii[dir.ordinal()] / 16.0f;
            if (sideRadius > 0.0f) {
                double gap = 0.5f - sideRadius;
                AABB aabb = makeCube(sideRadius);
                aabb = aabb.expandTowards(dir.getStepX() * gap, dir.getStepY() * gap, dir.getStepZ() * gap);
                shape = Shapes.or(shape, Shapes.create(aabb));
            }
        }
        return shape;
    }

    protected int getSideConnectionRadius(BlockGetter level, BlockPos pos, Direction side, int radius) {
        final BlockPos relativePos = pos.relative(side);
        final BlockState blockState = level.getBlockState(relativePos);
        if (blockState.is(BlockTags.LEAVES)){
            return radius;
        }

        return Math.min(blockState.getValueOrElse(THICKNESS,0),8);
    }

    private static boolean isTreePart(BlockState blockState) {
        return blockState.getBlock() instanceof TreePartBlock;
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
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity p_393688_, BlockGetter p_56301_, BlockPos p_56302_, BlockState p_56303_, Fluid p_56304_) {
        int maxRadiusForWaterLogging = 7;
        if (getRadius(p_56303_) > maxRadiusForWaterLogging) {
            return false;
        }
        return SimpleWaterloggedBlock.super.canPlaceLiquid(p_393688_, p_56301_, p_56302_, p_56303_, p_56304_);
    }

}
