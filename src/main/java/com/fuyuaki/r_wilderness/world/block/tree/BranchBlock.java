package com.fuyuaki.r_wilderness.world.block.tree;

import com.fuyuaki.r_wilderness.world.block.state.properties.RBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BranchBlock extends PipeBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> BASE = RBlockStateProperties.BASE;

    public static final MapCodec<BranchBlock> CODEC = simpleCodec(BranchBlock::new);

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return CODEC;
    }

    public BranchBlock(Properties properties) {
        super(10, properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(BASE,Direction.DOWN)
                        .setValue(WATERLOGGED,false)
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
        );
    }

    public BranchBlock(float thickness,Properties properties) {
        super(thickness, properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(BASE,Direction.DOWN)
                        .setValue(WATERLOGGED,false)
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateWithConnections(context.getLevel(), context.getClickedPos(), this.defaultBlockState());
    }

    public BlockState getStateWithConnections(BlockGetter level, BlockPos pos, BlockState state) {
        BlockState downState = level.getBlockState(pos.below());
        BlockState upState = level.getBlockState(pos.above());
        BlockState northState = level.getBlockState(pos.north());
        BlockState eastState = level.getBlockState(pos.east());
        BlockState southState = level.getBlockState(pos.south());
        BlockState westState = level.getBlockState(pos.west());
        Block block = state.getBlock();
        return state.trySetValue(DOWN, mayConnect(block,downState))
                .trySetValue(UP, mayConnect(block,upState))
                .trySetValue(NORTH, mayConnect(block,northState))
                .trySetValue(EAST, mayConnect(block,eastState))
                .trySetValue(SOUTH, mayConnect(block,southState))
                .trySetValue(WEST, mayConnect(block,westState));
    }

    private Boolean mayConnect(Block block, BlockState state) {
        return state.is(block) || state.getBlock() instanceof TrunkBlock || state.getBlock() instanceof BendingTrunkBlock;

    }

}
