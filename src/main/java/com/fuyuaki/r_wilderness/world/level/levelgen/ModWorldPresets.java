 package com.fuyuaki.r_wilderness.world.level.levelgen;

 import com.fuyuaki.r_wilderness.api.RWildernessMod;
 import com.fuyuaki.r_wilderness.data.worldgen.RSurfaceRuleData;
 import com.fuyuaki.r_wilderness.world.generation.WildChunkGenerator;
 import com.fuyuaki.r_wilderness.world.generation.WildGeneratorSettings;
 import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
 import net.minecraft.core.Holder;
 import net.minecraft.core.HolderGetter;
 import net.minecraft.core.registries.Registries;
 import net.minecraft.data.worldgen.BootstrapContext;
 import net.minecraft.resources.ResourceKey;
 import net.minecraft.world.level.biome.*;
 import net.minecraft.world.level.block.Blocks;
 import net.minecraft.world.level.chunk.ChunkGenerator;
 import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
 import net.minecraft.world.level.dimension.DimensionType;
 import net.minecraft.world.level.dimension.LevelStem;
 import net.minecraft.world.level.levelgen.DensityFunction;
 import net.minecraft.world.level.levelgen.DensityFunctions;
 import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
 import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
 import net.minecraft.world.level.levelgen.placement.PlacedFeature;
 import net.minecraft.world.level.levelgen.presets.WorldPreset;
 import net.minecraft.world.level.levelgen.structure.StructureSet;

 import java.util.Map;

 public class ModWorldPresets {
     public static final ResourceKey<WorldPreset> REBORN_WORLD = register("reborn");



     public static void bootstrap(BootstrapContext<WorldPreset> context) {
         HolderGetter<DimensionType> holdergetter = context.lookup(Registries.DIMENSION_TYPE);


         HolderGetter<NoiseGeneratorSettings> noiseSettings  = context.lookup(Registries.NOISE_SETTINGS);
         HolderGetter<DensityFunction> densityFunctions  = context.lookup(Registries.DENSITY_FUNCTION);
         HolderGetter<Biome> biomes  = context.lookup(Registries.BIOME);
         HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
         HolderGetter<StructureSet> structureSets = context.lookup(Registries.STRUCTURE_SET);
         HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterLists
                 = context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
         Holder<DimensionType> overworldDimensionType
                 = holdergetter.getOrThrow(WildWorldSettings.DimensionTypes.OVERWORLD);

         Holder<DimensionType> holder = holdergetter.getOrThrow(BuiltinDimensionTypes.NETHER);
         Holder<NoiseGeneratorSettings> holder1 = noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER);
         Holder.Reference<MultiNoiseBiomeSourceParameterList> reference =
                 multiNoiseBiomeSourceParameterLists
                 .getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER);

         LevelStem netherStem = new LevelStem(holder, new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(reference), holder1));

         Holder<DimensionType> holder2 = holdergetter.getOrThrow(BuiltinDimensionTypes.END);
         Holder<NoiseGeneratorSettings> holder3 = noiseSettings.getOrThrow(NoiseGeneratorSettings.END);



         LevelStem endStem  = new LevelStem(holder2, new NoiseBasedChunkGenerator(TheEndBiomeSource.create(biomes), holder3));



         RebornBiomeSource biomeSource = new RebornBiomeSource();

         registerCustomOverworldPreset(
                 context,
                 REBORN_WORLD,
                 makeOverworld(
                         overworldDimensionType,
                         new WildChunkGenerator(
                                 biomeSource,
                                 noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD),
                                 new WildGeneratorSettings(
                                         WildWorldSettings.OVERWORLD_NOISE_SETTINGS,
                                         Blocks.STONE.defaultBlockState(),
                                         Blocks.WATER.defaultBlockState(),
                                         RSurfaceRuleData.overworld(),
                                         68
                                 )
                         )
                 ),
                 netherStem,
                 endStem

         );
     }


     private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctionRegistry, ResourceKey<DensityFunction> key) {
         return new DensityFunctions.HolderHolder(densityFunctionRegistry.getOrThrow(key));
     }
     private static ResourceKey<WorldPreset> register(String name) {
         return ResourceKey.create(Registries.WORLD_PRESET, RWildernessMod.modLocation(name));
     }


     private static LevelStem makeOverworld(Holder<DimensionType> overworldDimensionType,ChunkGenerator generator) {
         return new LevelStem(overworldDimensionType, generator);
     }

     private static LevelStem makeNoiseBasedOverworld(BiomeSource biomeSource,Holder<DimensionType> overworldDimensionType, Holder<NoiseGeneratorSettings> settings) {
         return makeOverworld(overworldDimensionType,new NoiseBasedChunkGenerator(biomeSource, settings));
     }

     private static WorldPreset createPresetWithCustomOverworld(LevelStem overworldStem, LevelStem netherStem, LevelStem endStem) {
         return new WorldPreset(Map.of(LevelStem.OVERWORLD, overworldStem, LevelStem.NETHER, netherStem, LevelStem.END, endStem));
     }

     private static void registerCustomOverworldPreset(BootstrapContext<WorldPreset> context, ResourceKey<WorldPreset> dimensionKey, LevelStem overworldStem, LevelStem netherStem, LevelStem endStem) {
         context.register(dimensionKey, createPresetWithCustomOverworld(overworldStem, netherStem,endStem));
     }

}
