package com.fuyuaki.r_wilderness.world.block;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.stream.Stream;

public class ModFamilies {
    private static final Map<Block, BlockFamily> MAP = Maps.newHashMap();
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

    public static final BlockFamily MALATITE = familyBuilder(
            RBlocks.MALATITE.get())
                .slab(RBlocks.MALATITE_SLAB.get())
                .stairs(RBlocks.MALATITE_STAIRS.get())
                .wall(RBlocks.MALATITE_WALL.get())
                .getFamily();

    public static final BlockFamily COBBLED_MALATITE = familyBuilder(RBlocks.COBBLED_MALATITE.get())
                .slab(RBlocks.COBBLED_MALATITE_SLAB.get())
                .stairs(RBlocks.COBBLED_MALATITE_STAIRS.get())
                .wall(RBlocks.COBBLED_MALATITE_WALL.get())
                .getFamily();

    public static final BlockFamily SCHINITE = familyBuilder(RBlocks.SCHINITE.get())
                .slab(RBlocks.SCHINITE_SLAB.get())
                .stairs(RBlocks.SCHINITE_STAIRS.get())
                .wall(RBlocks.SCHINITE_WALL.get())
                .getFamily();

    public static final BlockFamily COBBLED_SCHINITE = familyBuilder(RBlocks.COBBLED_SCHINITE.get())
                .slab(RBlocks.COBBLED_SCHINITE_SLAB.get())
                .stairs(RBlocks.COBBLED_SCHINITE_STAIRS.get())
                .wall(RBlocks.COBBLED_SCHINITE_WALL.get())
                .getFamily();

    public static final BlockFamily MAGNEISS = familyBuilder(RBlocks.MAGNEISS.get())
                .slab(RBlocks.MAGNEISS_SLAB.get())
                .stairs(RBlocks.MAGNEISS_STAIRS.get())
                .wall(RBlocks.MAGNEISS_WALL.get())
                .getFamily();

    public static final BlockFamily COBBLED_MAGNEISS = familyBuilder(RBlocks.COBBLED_MAGNEISS.get())
                .slab(RBlocks.COBBLED_MAGNEISS_SLAB.get())
                .stairs(RBlocks.COBBLED_MAGNEISS_STAIRS.get())
                .wall(RBlocks.COBBLED_MAGNEISS_WALL.get())
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
