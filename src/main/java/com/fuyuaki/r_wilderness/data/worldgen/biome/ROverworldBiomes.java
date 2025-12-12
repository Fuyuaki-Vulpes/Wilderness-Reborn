package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.client.RSoundEvents;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import javax.annotation.Nullable;

public class ROverworldBiomes {
    protected static final int NORMAL_WATER_COLOR = 4159204;
    protected static final int NORMAL_WATER_FOG_COLOR = 329011;
    private static final int OVERWORLD_FOG_COLOR = 12638463;
    private static final int DARK_DRY_FOLIAGE_COLOR = 8082228;
    @Nullable
    private static final Music NORMAL_MUSIC = null;
    public static final int SWAMP_SKELETON_WEIGHT = 70;


    public static Biome barrenCaves(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
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
            @Nullable Integer pGrassColorOverride,
            @Nullable Integer pFoliageColorOverride,
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
