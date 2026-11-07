 package com.fuyuaki.r_wilderness.world.level.levelgen;

 import com.fuyuaki.r_wilderness.api.RRegistries;
 import com.fuyuaki.r_wilderness.api.RWildernessMod;
 import com.fuyuaki.r_wilderness.api.common.RTags;
 import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
 import com.fuyuaki.r_wilderness.world.generation.biome.RebornBiomeSource;
 import net.minecraft.core.Holder;
 import net.minecraft.core.HolderGetter;
 import net.minecraft.core.registries.Registries;
 import net.minecraft.data.worldgen.BootstrapContext;
 import net.minecraft.resources.ResourceKey;
 import net.minecraft.world.level.biome.*;
 import net.minecraft.world.level.chunk.ChunkGenerator;
 import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
 import net.minecraft.world.level.dimension.DimensionType;
 import net.minecraft.world.level.dimension.LevelStem;
 import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
 import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
 import net.minecraft.world.level.levelgen.placement.PlacedFeature;
 import net.minecraft.world.level.levelgen.presets.WorldPreset;
 import net.minecraft.world.level.levelgen.structure.StructureSet;

 import java.util.Map;

 public class RebornWorldPresets {
     public static final ResourceKey<WorldPreset> REBORN_WORLD = register("reborn");



     public static void bootstrap(BootstrapContext<WorldPreset> context) {
         HolderGetter<DimensionType> holdergetter = context.lookup(Registries.DIMENSION_TYPE);

         HolderGetter<RebornWorldSettings> rebornSettings  = context.lookup(RRegistries.WRGEN_SETTINGS);
         HolderGetter<NoiseGeneratorSettings> noiseSettings  = context.lookup(Registries.NOISE_SETTINGS);
         HolderGetter<Biome> biomes  = context.lookup(Registries.BIOME);
         HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterLists
                 = context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
         Holder<DimensionType> overworldDimensionType
                 = holdergetter.getOrThrow(RebornWorldConfiguration.DimensionTypes.OVERWORLD);

         Holder<DimensionType> holder = holdergetter.getOrThrow(BuiltinDimensionTypes.NETHER);
         Holder<NoiseGeneratorSettings> holder1 = noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER);
         Holder.Reference<MultiNoiseBiomeSourceParameterList> reference =
                 multiNoiseBiomeSourceParameterLists
                 .getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER);

         LevelStem netherStem = new LevelStem(holder, new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(reference), holder1));

         Holder<DimensionType> holder2 = holdergetter.getOrThrow(BuiltinDimensionTypes.END);
         Holder<NoiseGeneratorSettings> holder3 = noiseSettings.getOrThrow(NoiseGeneratorSettings.END);


         LevelStem endStem  = new LevelStem(holder2, new NoiseBasedChunkGenerator(TheEndBiomeSource.create(biomes), holder3));
         HolderGetter<BiomePlacementData> bPlaceData = context.lookup(RRegistries.REBORN_BIOME_PLACEMENT_KEY);


         RebornBiomeSource biomeSource = new RebornBiomeSource(bPlaceData.getOrThrow(RTags.BiomePlacementDatas.ENABLED));

         registerCustomOverworldPreset(
                 context,
                 REBORN_WORLD,
                 makeOverworld(
                         overworldDimensionType,
                         new RebornChunkGenerator(
                                 biomeSource,
                                 rebornSettings.getOrThrow(RebornWorldSettings.OVERWORLD)
                         )
                 ),
                 netherStem,
                 endStem

         );
     }


     private static ResourceKey<WorldPreset> register(String name) {
         return ResourceKey.create(Registries.WORLD_PRESET, RWildernessMod.identifier(name));
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
