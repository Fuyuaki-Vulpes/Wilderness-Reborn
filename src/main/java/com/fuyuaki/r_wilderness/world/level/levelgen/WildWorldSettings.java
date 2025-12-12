package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.*;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.timeline.Timeline;

import java.util.Optional;
import java.util.OptionalLong;

public class WildWorldSettings {
    protected static final NoiseSettings OVERWORLD_NOISE_SETTINGS = NoiseSettings.create(WildernessConstants.WORLD_BOTTOM, WildernessConstants.WORLD_HEIGHT, 1, 2);



    public static class DimensionTypes {
        public static final ResourceKey<DimensionType> OVERWORLD = ResourceKey.create(Registries.DIMENSION_TYPE, RWildernessMod.modLocation("overworld"));


        public static void bootstrap(BootstrapContext<DimensionType> context) {
            HolderGetter<Timeline> holdergetter = context.lookup(Registries.TIMELINE);
            EnvironmentAttributeMap environmentattributemap = EnvironmentAttributeMap.builder()
                    .set(EnvironmentAttributes.FOG_COLOR, -4138753)
                    .set(EnvironmentAttributes.SKY_COLOR, OverworldBiomes.calculateSkyColor(0.8F))
                    .set(EnvironmentAttributes.CLOUD_COLOR, ARGB.white(0.8F))
                    .set(EnvironmentAttributes.CLOUD_HEIGHT, WildernessConstants.CLOUD_HEIGHT)
                    .set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD)
                    .set(EnvironmentAttributes.BED_RULE, BedRule.CAN_SLEEP_WHEN_DARK)
                    .set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
                    .set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, true)
                    .set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                    .build();
            context.register(OVERWORLD,
                    new DimensionType(
                            false,
                            true,
                            false,
                            1.0,
                            WildernessConstants.WORLD_BOTTOM,
                            WildernessConstants.WORLD_HEIGHT,
                            WildernessConstants.WORLD_HEIGHT,
                            BlockTags.INFINIBURN_OVERWORLD,
                            0.0F,
                            new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0),
                            DimensionType.Skybox.OVERWORLD,
                            DimensionType.CardinalLightType.DEFAULT,
                            environmentattributemap,
                            holdergetter.getOrThrow(TimelineTags.IN_OVERWORLD)

                    )
            );

        }

    }

}
