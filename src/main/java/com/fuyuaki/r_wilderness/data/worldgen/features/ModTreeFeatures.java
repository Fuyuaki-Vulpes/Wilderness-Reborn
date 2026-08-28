package com.fuyuaki.r_wilderness.data.worldgen.features;

import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class ModTreeFeatures {
    public static final ResourceKey<ConfiguredFeature<?,?>> ALPINE_TREE = ModFeatureUtils.createKey("alpine_tree");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        BeehiveDecorator beehive0002 = new BeehiveDecorator(0.002F);
        BeehiveDecorator beehive001 = new BeehiveDecorator(0.01F);
        BeehiveDecorator beehive002 = new BeehiveDecorator(0.02F);
        BeehiveDecorator beehive005 = new BeehiveDecorator(0.05F);
        BeehiveDecorator beehive = new BeehiveDecorator(1.0F);
        PlaceOnGroundDecorator sparseLeafLitter = new PlaceOnGroundDecorator(
                96, 4, 2, new WeightedStateProvider(VegetationFeatures.leafLitterPatchBuilder(1, 3))
        );
        PlaceOnGroundDecorator thickLeafLitter = new PlaceOnGroundDecorator(
                150, 2, 2, new WeightedStateProvider(VegetationFeatures.leafLitterPatchBuilder(1, 4))
        );
        BlockStateProvider belowTrunkProvider = TreeConfiguration.defaultPlaceBelowTreeTrunkProvider(biomes);

        FeatureUtils.register(
                context,
                ALPINE_TREE,
                Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(RBlocks.ALPINE_LOG.get()),
                        new StraightTrunkPlacer(6, 4, 0),
                        BlockStateProvider.simple(RBlocks.ALPINE_LEAVES.get()),
                        new PineFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(3, 4)),
                        new TwoLayersFeatureSize(2, 0, 2),
                        belowTrunkProvider
                )
                        .ignoreVines()
                        .build()
        );
    }

}
