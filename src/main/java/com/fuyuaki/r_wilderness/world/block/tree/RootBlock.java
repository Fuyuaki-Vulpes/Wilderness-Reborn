package com.fuyuaki.r_wilderness.world.block.tree;

import com.fuyuaki.r_wilderness.world.block.state.properties.RBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.nio.channels.Pipe;

public class RootBlock extends PipeBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> BASE = RBlockStateProperties.BASE;

    public static final MapCodec<BranchBlock> CODEC = simpleCodec(BranchBlock::new);

    public RootBlock(float apothem, Properties properties) {
        super(apothem, properties);
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return null;
    }
}
