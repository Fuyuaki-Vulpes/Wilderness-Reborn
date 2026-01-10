package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.client.RSoundEvents;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jspecify.annotations.Nullable;


public class ROverworldBiomes {
    protected static final int NORMAL_WATER_COLOR = 4159204;
    protected static final int NORMAL_WATER_FOG_COLOR = 329011;
    private static final int OVERWORLD_FOG_COLOR = 12638463;
    private static final int DARK_DRY_FOLIAGE_COLOR = 8082228;
    @Nullable
    private static final Music NORMAL_MUSIC = null;
    public static final int SWAMP_SKELETON_WEIGHT = 70;

    protected static Biome dense_savanna(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        spawnBuilder.addSpawn(MobCategory.CREATURE,1, new MobSpawnSettings.SpawnerData(EntityType.HORSE,  2, 6))
                .addSpawn(MobCategory.CREATURE,1, new MobSpawnSettings.SpawnerData(EntityType.DONKEY,  1, 1))
                .addSpawn(MobCategory.CREATURE, 10,new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO,  2, 3));
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);


        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);

        globalOverworldGeneration(biomeBuilder);


        BiomeDefaultFeatures.addSavannaGrass(biomeBuilder);


        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.DENSE_SAVANNA_TREES);
        BiomeDefaultFeatures.addWarmFlowers(biomeBuilder);
        BiomeDefaultFeatures.addSavannaExtraGrass(biomeBuilder);


        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder,true);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SHALLOW_GRASS_SPARSE);



        return biome(true,
                2.0F,
                0.2F,
                4159204,
                0xbfb755,
                0xAEA42A,
                spawnBuilder,
                biomeBuilder
        )
                .build();
    }

    protected static Biome caatinga(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.desertSpawns(spawnBuilder);
        spawnBuilder.addSpawn(MobCategory.CREATURE,6,  new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 1, 2));
        spawnBuilder.creatureGenerationProbability(0.2F);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        BiomeDefaultFeatures.addFossilDecoration(biomeBuilder);


        globalOverworldGeneration(biomeBuilder);

        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addExtraGold(biomeBuilder);

        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SPARSE_OAK_TREE);

        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);

        BiomeDefaultFeatures.addDefaultGrass(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.STONY_ROCK);

        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.PRICKLY_PEAR);

        BiomeDefaultFeatures.addDesertVegetation(biomeBuilder);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SAND_GRASS_SPARSE);


        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDesertExtraVegetation(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SHALLOW_GRASS_SPARSE);


        return biome(true,
                2.0F,
                0.0F,
                4159204,
                0xabbd82,
                0xc6cc97,
                spawnBuilder,
                biomeBuilder
        )
                .build();
    }

    protected static Biome cerrado(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        spawnBuilder.addSpawn(MobCategory.CREATURE, 12, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 8))
                .addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.FROG, 2, 5))
                .addSpawn(MobCategory.CREATURE,20,  new MobSpawnSettings.SpawnerData(EntityType.PARROT, 1, 2))
                .addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.OCELOT,  1, 3))
//                .addSpawn(MobCategory.CREATURE,8, new MobSpawnSettings.SpawnerData(MtaEntityTypes.DEER.get(),  1, 3))
        ;
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);
        BiomeDefaultFeatures.farmAnimals(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);


        globalOverworldGeneration(biomeBuilder);

        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_TALL_GRASS);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);

        BiomeDefaultFeatures.addSavannaTrees(biomeBuilder);

        BiomeDefaultFeatures.addWarmFlowers(biomeBuilder);
        BiomeDefaultFeatures.addSavannaExtraGrass(biomeBuilder);

        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.PRICKLY_PEAR);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SAND_GRASS_SPARSE);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.ACACIA_BUSH);

        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder,true);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SHALLOW_GRASS_SPARSE);


        return biome(true,1.8F,0.1F,
                4159204,
                0xd4a531,
                0xe3c622,
                spawnBuilder,
                biomeBuilder
        )
                .build();
    }

    protected static Biome alpineTundra(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE,4, new MobSpawnSettings.SpawnerData(EntityType.WOLF,  2, 4))
//                .addSpawn(MobCategory.CREATURE,15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.DEER.get(),  2, 4))
                .addSpawn(MobCategory.CREATURE,15,  new MobSpawnSettings.SpawnerData(EntityType.GOAT, 2, 4))
                .addSpawn(MobCategory.CREATURE,15, new MobSpawnSettings.SpawnerData(EntityType.FOX,  2, 4))
                .addSpawn(MobCategory.CREATURE,15,  new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 4))
                .addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 4))

//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.YUKI_ONNA.get(), 2, 4))
//                .addSpawn(MobCategory.MONSTER, 15,new MobSpawnSettings.SpawnerData(MtaEntityTypes.FROSTED_SLIME.get(),  2, 4))
//                .addSpawn(MobCategory.MONSTER, 15,new MobSpawnSettings.SpawnerData(MtaEntityTypes.ICICLE_CREEPER.get(),  2, 4))
                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 2, 4));

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        globalOverworldGeneration(biomeBuilder);

        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);


//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.PATCH_TUNDRA_GRASS);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.STONY_ROCK);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.ALPINE_TREE);


        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);


        return biome(true, -1.0F, 0.8F,
                4159204,
                0xeac466,
                0xa5bc6c,
                spawnBuilder, biomeBuilder)
                .build();
    }

    protected static Biome polarDesert(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.snowySpawns(mobspawnsettings$builder,true);
        mobspawnsettings$builder.addSpawn(MobCategory.CREATURE,8, new MobSpawnSettings.SpawnerData(EntityType.WOLF,  4, 4))
                .addSpawn(MobCategory.CREATURE, 12,new MobSpawnSettings.SpawnerData(EntityType.FOX,  2, 4))
//                .addSpawn(MobCategory.MONSTER,40, new MobSpawnSettings.SpawnerData(MtaEntityTypes.YUKI_ONNA.get(),1, 3))
        ;
        mobspawnsettings$builder.creatureGenerationProbability(0.05F);
        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);
        BiomeDefaultFeatures.addFossilDecoration(biomeBuilder);
        globalOverworldGeneration(biomeBuilder);

        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.PERMAFROST_ROCK);
        BiomeDefaultFeatures.addExtraEmeralds(biomeBuilder);
        BiomeDefaultFeatures.addInfestedStone(biomeBuilder);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(-1.0F)
                .downfall(0.0F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(4159204)
                        .build())
                .mobSpawnSettings(mobspawnsettings$builder.build())
                .generationSettings(biomeBuilder.build())
                .build();

    }


    protected static Biome bog(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE,4,  new MobSpawnSettings.SpawnerData(EntityType.FROG, 1, 3))


                .addSpawn(MobCategory.MONSTER, 5, new MobSpawnSettings.SpawnerData(EntityType.WITCH,  1, 1))
                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(EntityType.BOGGED,  2, 4))
//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.ARMORED_SKELETON.get(), 2, 4))
//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.FALLEN_SAMURAI.get(), 2, 4))
//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.BLACK_WIDOW.get(), 2, 4))
//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.HOST.get(), 1, 1))
//                .addSpawn(MobCategory.MONSTER, 15,new MobSpawnSettings.SpawnerData(MtaEntityTypes.SKELETON_FIGHTER.get(),  2, 4))
//                .addSpawn(MobCategory.MONSTER, 15,new MobSpawnSettings.SpawnerData(MtaEntityTypes.CORROSIVE_CUBE.get(),  2, 4))
//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.TOXIC_ZOMBIE.get(), 2, 4))
;
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);


        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        globalOverworldGeneration(biomeBuilder);

        BiomeDefaultFeatures.addBadlandGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_GRAVEL);


//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.BOGGED_OAK_TREE);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.STONY_ROCK);


        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addBadlandExtraVegetation(biomeBuilder);

        BiomeSpecialEffects.Builder effects = new BiomeSpecialEffects.Builder()
                .waterColor(0x9eacb5)
                .grassColorOverride(0x899aa3)
                .foliageColorOverride(0x899aa3);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.7F)
                .downfall(0.2F)
                .specialEffects(effects.build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }



    protected static Biome lushRiver(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();


        spawnBuilder.addSpawn(MobCategory.WATER_CREATURE,2,  new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 4))
                .addSpawn(MobCategory.WATER_AMBIENT, 6,new MobSpawnSettings.SpawnerData(EntityType.SALMON,  1, 5))
                .addSpawn(MobCategory.WATER_AMBIENT, 5, new MobSpawnSettings.SpawnerData(EntityType.COD, 1, 5))
                .addSpawn(MobCategory.WATER_AMBIENT, 2,new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH,  3, 8));
        spawnBuilder.addSpawn(MobCategory.MONSTER, 40, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 1, 1));

        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        globalOverworldGeneration(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, MtaPlacedFeatures.MOSSY_ROCKS);
        BiomeDefaultFeatures.addForestFlowers(biomeBuilder);


        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SPARSE_MANGROVE);


        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_SAND);
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
//        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MtaPlacedFeatures.DISK_MOSS);
//        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MtaPlacedFeatures.FREQUENT_CLAY);
        BiomeDefaultFeatures.addWaterTrees(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addDefaultGrass(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder,true);


        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP)
                .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE)
                .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.KELP_WARM)
                .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_COMMON);






        return biome(true,0.6F,0.3F, 4566514, null, null,spawnBuilder,biomeBuilder)
                .build();

    }

    protected static Biome sparseCherryGrove(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers){
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 2))
                .addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 6))
                .addSpawn(MobCategory.CREATURE, 2,new MobSpawnSettings.SpawnerData(EntityType.SHEEP,  2, 4));
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        globalOverworldGeneration(biomeBuilder);

        BiomeDefaultFeatures.addPlainGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MtaPlacedFeatures.FREQUENT_CLAY);


        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_PLAIN);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_CHERRY);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SPARSE_CHERRY_TREE);

        BiomeDefaultFeatures.addExtraEmeralds(biomeBuilder);
        BiomeDefaultFeatures.addInfestedStone(biomeBuilder);

        return biome(true,
                0.5F,
                0.8F,
                6141935,
                11983713,
                11983713,
                spawnBuilder,
                biomeBuilder)
                .build();
    }

    protected static Biome sparseTaiga(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();


        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        spawnBuilder.addSpawn(MobCategory.CREATURE,4,  new MobSpawnSettings.SpawnerData(EntityType.WOLF, 2, 4))
                .addSpawn(MobCategory.CREATURE,6,  new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 5))
                .addSpawn(MobCategory.CREATURE,5,  new MobSpawnSettings.SpawnerData(EntityType.FOX, 1, 4));
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);


        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        globalOverworldGeneration(biomeBuilder);

        BiomeDefaultFeatures.addPlainGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SPARSE_SPRUCE_TREE);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addTaigaGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder,true);
        BiomeDefaultFeatures.addCommonBerryBushes(biomeBuilder);
        return biome(
                true,
                0.2F,
                0.6F,
                4159204,
                null,
                null,
                spawnBuilder,
                biomeBuilder)
                .build();
    }

    protected static Biome oasis(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();


        BiomeDefaultFeatures.desertSpawns(spawnBuilder);
        spawnBuilder.addSpawn(MobCategory.CREATURE,4,  new MobSpawnSettings.SpawnerData(EntityType.CAMEL, 1, 3));


        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);


        BiomeDefaultFeatures.addFossilDecoration(biomeBuilder);
        globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.PALM_TREE);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addDefaultGrass(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.PRICKLY_PEAR);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SAND_GRASS);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SANDSTONE_ROCK);

        BiomeDefaultFeatures.addDesertVegetation(biomeBuilder);

        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);

        BiomeDefaultFeatures.addDesertExtraVegetation(biomeBuilder);
        BiomeDefaultFeatures.addDesertExtraDecoration(biomeBuilder);


        return biome(
                false,
                2.0F,
                0.4F,
                0x32A598,
                0xBFB755,
                0xBFB755,
                spawnBuilder,
                biomeBuilder)
                .build();
    }

    protected static Biome gravelRiver(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();


        spawnBuilder.addSpawn(MobCategory.WATER_CREATURE,2,  new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 4))
                .addSpawn(MobCategory.WATER_AMBIENT, 6, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 1, 5));
        spawnBuilder.addSpawn(MobCategory.MONSTER, 40, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 1, 1));

        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addForestFlowers(biomeBuilder);


        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);



        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);

        BiomeDefaultFeatures.addWaterTrees(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addDefaultGrass(biomeBuilder);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.STONY_ROCK);



        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder,true);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.VINES);


        return biome(true,0.3F,0.1F, 0x7C8CC4, null, null,spawnBuilder,biomeBuilder)
                .build();

    }

    protected static Biome lushMeadow(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers){
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, 1,new MobSpawnSettings.SpawnerData(EntityType.DONKEY,  1, 2))
                .addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 6))
//                .addSpawn(MobCategory.CREATURE, 3,new MobSpawnSettings.SpawnerData(MtaEntityTypes.BUTTERFLY.get(),  2, 5))
                .addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 4));
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        globalOverworldGeneration(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);


        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FLOWER_FOREST);


//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.DISK_MOSS_RARE);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.LUSH_VEGETATION);

        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TreePlacements.FANCY_OAK_BEES_002);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TreePlacements.OAK_BEES_002);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TreePlacements.BIRCH_BEES_002);
//        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, MtaPlacedFeatures.MOSSY_ROCKS);

        BiomeDefaultFeatures.addMeadowVegetation(biomeBuilder);
        BiomeDefaultFeatures.addWarmFlowers(biomeBuilder);
        BiomeDefaultFeatures.addJungleGrass(biomeBuilder);


        BiomeDefaultFeatures.addExtraEmeralds(biomeBuilder);
        BiomeDefaultFeatures.addInfestedStone(biomeBuilder);

        return biome(true,
                0.3F,
                0.8F,
                6141935,
                11983713,
                11983713,
                spawnBuilder,
                biomeBuilder)
                .build();
    }


    protected static Biome crystallineGrotto(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers){
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();



        BiomeDefaultFeatures.caveSpawns(spawnBuilder);
        BiomeDefaultFeatures.monsters(spawnBuilder, 40, 6, 40, 50,false);

        spawnBuilder.addSpawn(MobCategory.MONSTER,20,
                new MobSpawnSettings.SpawnerData(EntityType.SILVERFISH, 2, 8));


        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

//        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MtaPlacedFeatures.CALCITE_BLOBS);

        BiomeDefaultFeatures.addDefaultCarversAndLakes(biomeBuilder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biomeBuilder);

        BiomeDefaultFeatures.addDefaultSprings(biomeBuilder);
        BiomeDefaultFeatures.addSurfaceFreezing(biomeBuilder);

        BiomeDefaultFeatures.addDefaultOres(biomeBuilder, true);
//        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MtaPlacedFeatures.LARGE_CLEAR_QUARTZ_VEIN);
//        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MtaPlacedFeatures.CLEAR_QUARTZ_SHARD);





        return biome(true, 0.0F, 0.4F, spawnBuilder, biomeBuilder)
                .build();
    }


    protected static Biome tundra(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        spawnBuilder.addSpawn(MobCategory.CREATURE, 4,new MobSpawnSettings.SpawnerData(EntityType.WOLF,  2, 4));


        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);
        BiomeDefaultFeatures.snowySpawns(spawnBuilder,true);

        globalOverworldGeneration(biomeBuilder);

        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);


//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.PATCH_TUNDRA_GRASS);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.STONY_ROCK);


        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);


        return biome(false, -1.0F, 0.5F,
                4159204,
                0xeac466,
                0xa5bc6c,
                spawnBuilder, biomeBuilder)
                .build();
    }





    protected static Biome underwaterForest(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder()
                .addSpawn(MobCategory.WATER_AMBIENT,15, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH,  1, 3))
                .addSpawn(MobCategory.WATER_AMBIENT, 4, new MobSpawnSettings.SpawnerData(EntityType.AXOLOTL, 1, 2))
                .addSpawn(MobCategory.WATER_AMBIENT, 10, new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID, 2, 5));
        BiomeDefaultFeatures.warmOceanSpawns(mobspawnsettings$builder, 20, 2);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);



        biomeBuilder
                .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION)
                .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM)
                .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE);

//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.SEAWOOD_TREE);

//        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MtaPlacedFeatures.FREQUENT_CLAY);


        return baseOcean(mobspawnsettings$builder, 4445678, 270131, biomeBuilder)
                .build();
    }



    protected static Biome mapleForest(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder()
//                .addSpawn(MobCategory.CREATURE, 15,new MobSpawnSettings.SpawnerData(MtaEntityTypes.BUTTERFLY.get(),  1, 3))
//                .addSpawn(MobCategory.CREATURE, 15,new MobSpawnSettings.SpawnerData(MtaEntityTypes.DEER.get(),  1, 3))
//                .addSpawn(MobCategory.CREATURE, 15,new MobSpawnSettings.SpawnerData(MtaEntityTypes.OWL.get(),  1, 3))
//                .addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.RACCOON.get(), 1, 3))
//                .addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.TURKEY.get(), 1, 3))
//                .addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.WISPFLY.get(), 1, 3))
                .addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(EntityType.FOX, 1, 3))

//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.FALLEN_SAMURAI.get(), 1, 3))
//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.GLOW_SPIDER.get(), 1, 3))
//                .addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(MtaEntityTypes.SKELETON_FIGHTER.get(), 1, 3))
;

        BiomeDefaultFeatures.commonSpawns(spawnBuilder, 20);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatures,worldCarvers);

        globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addOtherBirchTrees(biomeBuilder);

        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);
        BiomeDefaultFeatures.addForestGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);



//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MtaPlacedFeatures.MAPLE_TREE);


        return biome(true, 0.2F, 0.8F,
                0x79a6ff,
                0x79c05a,
                0xdbb435,
                spawnBuilder, biomeBuilder)
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_FOREST))
                .build();
    }

    protected static Biome barrenCaves(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.caveSpawns(spawns);
        BiomeDefaultFeatures.monsters(spawns, 95, 5, 0, 100, false);


        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);
        globalOverworldGeneration(generation);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultOres(generation, true);
        BiomeDefaultFeatures.addDefaultSoftDisks(generation);
        BiomeDefaultFeatures.addPlainVegetation(generation);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        BiomeDefaultFeatures.addDefaultExtraVegetation(generation, false);
        return biome(true, 0.45F, 0.4F, spawns, generation)
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(RSoundEvents.MUSIC_BIOME_BARREN_CAVES))
                .build();
    }



    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }


    protected static int calculateSkyColor(float pTemperature) {
        float $$1 = pTemperature / 3.0F;
        $$1 = Mth.clamp($$1, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }

    private static Biome.BiomeBuilder biome(
            boolean pHasPercipitation,
            float pTemperature,
            float pDownfall,
            MobSpawnSettings.Builder pMobSpawnSettings,
            BiomeGenerationSettings.Builder pGenerationSettings
    ) {
        return biome(pHasPercipitation, pTemperature, pDownfall, 4159204, null, null, pMobSpawnSettings, pGenerationSettings);
    }

    private static Biome.BiomeBuilder biome(
            boolean pHasPrecipitation,
            float pTemperature,
            float pDownfall,
            int pWaterColor,
            Integer pGrassColorOverride,
            Integer pFoliageColorOverride,
            MobSpawnSettings.Builder pMobSpawnSettings,
            BiomeGenerationSettings.Builder pGenerationSettings
    ) {
        BiomeSpecialEffects.Builder biomespecialeffects$builder = new BiomeSpecialEffects.Builder()
                .waterColor(pWaterColor);
        if (pGrassColorOverride != null) {
            biomespecialeffects$builder.grassColorOverride(pGrassColorOverride);
        }

        if (pFoliageColorOverride != null) {
            biomespecialeffects$builder.foliageColorOverride(pFoliageColorOverride);
        }

        return new Biome.BiomeBuilder()
                .hasPrecipitation(pHasPrecipitation)
                .temperature(pTemperature)
                .downfall(pDownfall)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, calculateSkyColor(pTemperature))
                .specialEffects(biomespecialeffects$builder.build())
                .mobSpawnSettings(pMobSpawnSettings.build())
                .generationSettings(pGenerationSettings.build());
    }


    private static Biome.BiomeBuilder baseOcean(MobSpawnSettings.Builder mobSpawnSettings, int waterColor, int waterFogColor, BiomeGenerationSettings.Builder generationSettings) {
        return biome(true, 0.5F, 0.5F, waterColor, null, null, mobSpawnSettings, generationSettings);
    }



    private static BiomeGenerationSettings.Builder baseOceanGeneration(BiomeGenerationSettings.Builder builder) {
        globalOverworldGeneration(builder);
        BiomeDefaultFeatures.addDefaultOres(builder);
        BiomeDefaultFeatures.addDefaultSoftDisks(builder);
        BiomeDefaultFeatures.addWaterTrees(builder);
        BiomeDefaultFeatures.addDefaultFlowers(builder);
        BiomeDefaultFeatures.addDefaultGrass(builder);
        BiomeDefaultFeatures.addDefaultMushrooms(builder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(builder,true);
        return builder;
    }

}
