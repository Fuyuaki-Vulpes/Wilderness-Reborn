package com.fuyuaki.r_wilderness.data.generation.other;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;

import java.util.List;

public class RWBlockTransformers {

    public static final ResourceKey<BlockTransformer> SHOVEL = createKey("shovel");
    public static final ResourceKey<BlockTransformer> AXE = createKey("axe");
    public static final ResourceKey<BlockTransformer> HOE = createKey("hoe");

    private static ResourceKey<BlockTransformer> createKey(String name) {
        return ResourceKey.create(Registries.BLOCK_TRANSFORMER, RWildernessMod.identifier(name));
    }

    public static void bootstrap(BootstrapContext<BlockTransformer> context) {
        BlockTransformer.BlockTransformData hoeDefault = BlockTransformer.BlockTransformData.builder(
                        RuleBasedStateProvider.builder()
                                .ifTrueThenProvide(
                                        BlockPredicate.allOf(BlockPredicate.matchesBlocks(RBlocks.CHALKY_SOIL.get()), BlockPredicate.matchesTag(Direction.UP, BlockTags.AIR)),
                                        RBlocks.CHALKY_FARMLAND.get()
                                )
                                .ifTrueThenProvide(
                                        BlockPredicate.allOf(BlockPredicate.matchesBlocks(RBlocks.CLAY_SOIL.get()), BlockPredicate.matchesTag(Direction.UP, BlockTags.AIR)),
                                        RBlocks.CLAY_FARMLAND.get()
                                )
                                .ifTrueThenProvide(
                                        BlockPredicate.allOf(BlockPredicate.matchesBlocks(RBlocks.PEAT.get()), BlockPredicate.matchesTag(Direction.UP, BlockTags.AIR)),
                                        RBlocks.PEAT_FARMLAND.get()
                                )
                                .ifTrueThenProvide(
                                        BlockPredicate.allOf(BlockPredicate.matchesBlocks(RBlocks.SANDY_SOIL.get()), BlockPredicate.matchesTag(Direction.UP, BlockTags.AIR)),
                                        RBlocks.SANDY_FARMLAND.get()
                                )
                                .ifTrueThenProvide(
                                        BlockPredicate.allOf(BlockPredicate.matchesBlocks(RBlocks.SILT.get()), BlockPredicate.matchesTag(Direction.UP, BlockTags.AIR)),
                                        RBlocks.SILT_FARMLAND.get()
                                )
                                .build()
                )
                .sound(SoundEvents.HOE_TILL)
                .disallowedFaces(List.of(Direction.DOWN))
                .build();
        context.register(HOE, new BlockTransformer(ImmutableList.<BlockTransformer.BlockTransformData>builder().add(hoeDefault).build()));

    }
}