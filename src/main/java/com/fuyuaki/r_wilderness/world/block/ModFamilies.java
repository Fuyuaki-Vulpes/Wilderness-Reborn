package com.fuyuaki.r_wilderness.world.block;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.stream.Stream;

public class ModFamilies {private static final Map<Block, BlockFamily> MAP = Maps.newHashMap();
    private static final String RECIPE_GROUP_PREFIX_WOODEN = "wooden";
    private static final String RECIPE_UNLOCKED_BY_HAS_PLANKS = "has_planks";



    public static final BlockFamily ALPINE_PLANKS = familyBuilder(RBlocks.ALPINE_PLANKS.get())
            .button(RBlocks.ALPINE_BUTTON.get())
            .fence(RBlocks.ALPINE_FENCE.get())
            .fenceGate(RBlocks.ALPINE_FENCE_GATE.get())
            .pressurePlate(RBlocks.ALPINE_PRESSURE_PLATE.get())
            .sign(RBlocks.ALPINE_SIGN.get(), RBlocks.ALPINE_WALL_SIGN.get())
            .slab(RBlocks.ALPINE_SLAB.get())
            .stairs(RBlocks.ALPINE_STAIRS.get())
            .door(RBlocks.ALPINE_DOOR.get())
            .trapdoor(RBlocks.ALPINE_TRAPDOOR.get())
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();


    private static BlockFamily.Builder familyBuilder(Block baseBlock) {
        BlockFamily.Builder blockfamily$builder = new BlockFamily.Builder(baseBlock);
        BlockFamily blockfamily = MAP.put(baseBlock, blockfamily$builder.getFamily());
        if (blockfamily != null) {
            throw new IllegalStateException("Duplicate family value for " + BuiltInRegistries.BLOCK.getKey(baseBlock));
        } else {
            return blockfamily$builder;
        }
    }

    public static Stream<BlockFamily> getAllFamilies() {
        return MAP.values().stream();
    }

}
