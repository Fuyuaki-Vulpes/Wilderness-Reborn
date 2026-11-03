package com.fuyuaki.r_wilderness.data.worldgen.features;

import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BlockStateProviders;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class ModTreeFeatures {
    public static final ResourceKey<Feature> ALPINE_TREE = ModFeatureUtils.createKey("alpine_tree");

    public static void bootstrap(BootstrapContext<Feature> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
        HolderGetter<BlockStateProvider> blockStateProviders = context.lookup(Registries.BLOCK_STATE_PROVIDER);
        BlockPredicate stemReplaceableBlocks = stemReplaceables();

        BeehiveDecorator beehive0002 = new BeehiveDecorator(0.002F);
        BeehiveDecorator beehive001 = new BeehiveDecorator(0.01F);
        BeehiveDecorator beehive002 = new BeehiveDecorator(0.02F);
        BeehiveDecorator beehive005 = new BeehiveDecorator(0.05F);
        BeehiveDecorator beehive = new BeehiveDecorator(1.0F);
        PlaceOnGroundDecorator sparseLeafLitter = new PlaceOnGroundDecorator(
                96, 4, 2, Holder.direct(new WeightedStateProvider(VegetationFeatures.leafLitterPatchBuilder(1, 3)))
        );
        PlaceOnGroundDecorator thickLeafLitter = new PlaceOnGroundDecorator(
                150, 2, 2, Holder.direct(new WeightedStateProvider(VegetationFeatures.leafLitterPatchBuilder(1, 4)))
        );
        Holder<BlockStateProvider> belowTrunkProvider = blockStateProviders.getOrThrow(BlockStateProviders.SOIL_BENEATH_TREE);

        context.register(
                ALPINE_TREE,
                new TreeFeature.Builder(
                        BlockStateProvider.of(RBlocks.ALPINE_LOG.get()),
                        new StraightTrunkPlacer(6, 4, 0),
                        BlockStateProvider.of(RBlocks.ALPINE_LEAVES.get()),
                        new PineFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(3, 4)),
                        new TwoLayersFeatureSize(2, 0, 2),
                        belowTrunkProvider
                )
                        .ignoreVines()
                        .build()
        );
    }

    private static BlockPredicate stemReplaceables() {
        return BlockPredicate.matchesBlocks(
                Blocks.OAK_SAPLING,
                Blocks.SPRUCE_SAPLING,
                Blocks.BIRCH_SAPLING,
                Blocks.JUNGLE_SAPLING,
                Blocks.ACACIA_SAPLING,
                Blocks.CHERRY_SAPLING,
                Blocks.DARK_OAK_SAPLING,
                Blocks.PALE_OAK_SAPLING,
                Blocks.MANGROVE_PROPAGULE,
                Blocks.DANDELION,
                Blocks.TORCHFLOWER,
                Blocks.POPPY,
                Blocks.BLUE_ORCHID,
                Blocks.ALLIUM,
                Blocks.AZURE_BLUET,
                Blocks.RED_TULIP,
                Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP,
                Blocks.PINK_TULIP,
                Blocks.OXEYE_DAISY,
                Blocks.CORNFLOWER,
                Blocks.WITHER_ROSE,
                Blocks.LILY_OF_THE_VALLEY,
                Blocks.BROWN_MUSHROOM,
                Blocks.RED_MUSHROOM,
                Blocks.WHEAT,
                Blocks.SUGAR_CANE,
                Blocks.ATTACHED_PUMPKIN_STEM,
                Blocks.ATTACHED_MELON_STEM,
                Blocks.PUMPKIN_STEM,
                Blocks.MELON_STEM,
                Blocks.LILY_PAD,
                Blocks.NETHER_WART,
                Blocks.COCOA,
                Blocks.CARROTS,
                Blocks.POTATOES,
                Blocks.CHORUS_PLANT,
                Blocks.CHORUS_FLOWER,
                Blocks.TORCHFLOWER_CROP,
                Blocks.PITCHER_CROP,
                Blocks.BEETROOTS,
                Blocks.SWEET_BERRY_BUSH,
                Blocks.WARPED_FUNGUS,
                Blocks.CRIMSON_FUNGUS,
                Blocks.WEEPING_VINES,
                Blocks.WEEPING_VINES_PLANT,
                Blocks.TWISTING_VINES,
                Blocks.TWISTING_VINES_PLANT,
                Blocks.CAVE_VINES,
                Blocks.CAVE_VINES_PLANT,
                Blocks.SPORE_BLOSSOM,
                Blocks.AZALEA,
                Blocks.FLOWERING_AZALEA,
                Blocks.MOSS_CARPET,
                Blocks.PINK_PETALS,
                Blocks.WILDFLOWERS,
                Blocks.BIG_DRIPLEAF,
                Blocks.BIG_DRIPLEAF_STEM,
                Blocks.SMALL_DRIPLEAF
        );
    }

}
