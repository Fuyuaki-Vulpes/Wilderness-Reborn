package com.fuyuaki.r_wilderness.data.worldgen;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;
import static com.fuyuaki.r_wilderness.api.WildernessConstants.*;

public class RSurfaceRuleData {

    private static final SurfaceRules.RuleSource AIR = makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.white());
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.orange());
    private static final SurfaceRules.RuleSource TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
    private static final SurfaceRules.RuleSource DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
    private static final SurfaceRules.RuleSource COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
    private static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource CALCITE = makeStateRule(Blocks.CALCITE);
    private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
    private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WATER = makeStateRule(Blocks.WATER);
    private static final SurfaceRules.RuleSource CINNABAR = makeStateRule(Blocks.CINNABAR);
    private static final SurfaceRules.RuleSource SULFUR = makeStateRule(Blocks.SULFUR);

    //OUR ADDITIONAL SURFACE RULE SOURCES
    private static final SurfaceRules.RuleSource SCHINITE = makeStateRule(RBlocks.SCHINITE.get());
    private static final SurfaceRules.RuleSource MAGNEISS = makeStateRule(RBlocks.MAGNEISS.get());
    private static final SurfaceRules.RuleSource MALATITE = makeStateRule(RBlocks.MALATITE.get());

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
    public static SurfaceRules.RuleSource overworld(HolderGetter<Biome> biomes) {
        return overworldLike(biomes, true, false, true);
    }
    public static SurfaceRules.RuleSource overworldLike(HolderGetter<Biome> biomes, boolean doPreliminarySurfaceCheck, boolean bedrockRoof, boolean bedrockFloor
    ) {

        SurfaceRules.ConditionSource woodedBadlandsTop = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
        SurfaceRules.ConditionSource badlandsTop = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
        SurfaceRules.ConditionSource badlandsHeightCondition = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
        SurfaceRules.ConditionSource badlandsMid = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
        SurfaceRules.ConditionSource mangroveSwampPuddleLevel = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
        SurfaceRules.ConditionSource swampPuddleLevel = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
        SurfaceRules.ConditionSource aboveOverworldSeaLevel = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
        SurfaceRules.ConditionSource notUnderwater = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource aboveWater = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.ConditionSource notUnderDeepWater = SurfaceRules.waterStartCheck(-6, -1);
        SurfaceRules.ConditionSource hole = SurfaceRules.hole();
        SurfaceRules.ConditionSource frozenOcean = SurfaceRules.isBiome(biomes, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        SurfaceRules.ConditionSource steep = SurfaceRules.steep();
        SurfaceRules.RuleSource grassOrDirtIfUnderwater = SurfaceRules.sequence(SurfaceRules.ifTrue(aboveWater, GRASS_BLOCK), DIRT);
        SurfaceRules.RuleSource sandOrSandstoneIfCeiling = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
        SurfaceRules.RuleSource gravelOrStoneIfCeiling = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
        SurfaceRules.ConditionSource biomesWithSandAndSandstone = SurfaceRules.isBiome(biomes, Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
        SurfaceRules.ConditionSource biomesWithSandAndVeryDeepSandstone = SurfaceRules.isBiome(biomes, Biomes.DESERT);
        SurfaceRules.RuleSource sulfurCaveBands = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition3d(Noises.SULFUR_CAVE_GRADIENT, -0.4F, -0.1F), CINNABAR),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition3d(Noises.SULFUR_CAVE_GRADIENT, 0.0, 0.4F), SULFUR),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition3d(Noises.SULFUR_CAVE_GRADIENT, 0.4F), CINNABAR)
        );
        SurfaceRules.RuleSource commonSurfaceAndUnderRules = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.STONY_PEAKS),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition2d(Noises.CALCITE, -0.0125, 0.0125), CALCITE), STONE)
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.STONY_SHORE),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition2d(Noises.GRAVEL, -0.05, 0.05), gravelOrStoneIfCeiling), STONE)
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE)),
                SurfaceRules.ifTrue(biomesWithSandAndSandstone, sandOrSandstoneIfCeiling),
                SurfaceRules.ifTrue(biomesWithSandAndVeryDeepSandstone, sandOrSandstoneIfCeiling),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.DRIPSTONE_CAVES), STONE),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.SULFUR_CAVES), SurfaceRules.sequence(sulfurCaveBands, STONE))
        );
        SurfaceRules.RuleSource powderSnowUnderRule = SurfaceRules.ifTrue(
                SurfaceRules.noiseCondition2d(Noises.POWDER_SNOW, 0.45, 0.58), SurfaceRules.ifTrue(aboveWater, POWDER_SNOW)
        );
        SurfaceRules.RuleSource powderSnowSurfaceRule = SurfaceRules.ifTrue(
                SurfaceRules.noiseCondition2d(Noises.POWDER_SNOW, 0.35, 0.6), SurfaceRules.ifTrue(aboveWater, POWDER_SNOW)
        );
        SurfaceRules.RuleSource biomeUnderSurfaceRule = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.FROZEN_PEAKS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(steep, PACKED_ICE),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition2d(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition2d(Noises.ICE, -0.0625, 0.025), ICE),
                                SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK)
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.SNOWY_SLOPES),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), powderSnowUnderRule, SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.JAGGED_PEAKS), STONE),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.GROVE), SurfaceRules.sequence(powderSnowUnderRule, DIRT)),
                commonSurfaceAndUnderRules,
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), STONE)),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.WINDSWEPT_GRAVELLY_HILLS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), gravelOrStoneIfCeiling),
                                SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE),
                                SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), DIRT),
                                gravelOrStoneIfCeiling
                        )
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.MANGROVE_SWAMP), MUD),
                DIRT
        );
        SurfaceRules.RuleSource biomeSurfaceRule = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.FROZEN_PEAKS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(steep, PACKED_ICE),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition2d(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition2d(Noises.ICE, 0.0, 0.025), ICE),
                                SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK)
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.SNOWY_SLOPES),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), powderSnowSurfaceRule, SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.JAGGED_PEAKS),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.GROVE), SurfaceRules.sequence(powderSnowSurfaceRule, SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))
                ),
                commonSurfaceAndUnderRules,
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.WINDSWEPT_SAVANNA),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.5), COARSE_DIRT))
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.WINDSWEPT_GRAVELLY_HILLS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), gravelOrStoneIfCeiling),
                                SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE),
                                SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), grassOrDirtIfUnderwater),
                                gravelOrStoneIfCeiling
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), COARSE_DIRT), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.95), PODZOL))
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.ICE_SPIKES), SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK)),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.MANGROVE_SWAMP), MUD),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.MUSHROOM_FIELDS), MYCELIUM),
                grassOrDirtIfUnderwater
        );
        SurfaceRules.ConditionSource clayBand1 = SurfaceRules.noiseCondition2d(Noises.SURFACE, -0.909, -0.5454);
        SurfaceRules.ConditionSource clayBand2 = SurfaceRules.noiseCondition2d(Noises.SURFACE, -0.1818, 0.1818);
        SurfaceRules.ConditionSource clayBand3 = SurfaceRules.noiseCondition2d(Noises.SURFACE, 0.5454, 0.909);
        SurfaceRules.RuleSource mainRuleCloseToSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(
                                        SurfaceRules.isBiome(biomes, Biomes.WOODED_BADLANDS),
                                        SurfaceRules.ifTrue(
                                                woodedBadlandsTop,
                                                SurfaceRules.sequence(
                                                        SurfaceRules.ifTrue(clayBand1, COARSE_DIRT),
                                                        SurfaceRules.ifTrue(clayBand2, COARSE_DIRT),
                                                        SurfaceRules.ifTrue(clayBand3, COARSE_DIRT),
                                                        grassOrDirtIfUnderwater
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(
                                        SurfaceRules.isBiome(biomes, Biomes.SWAMP),
                                        SurfaceRules.ifTrue(
                                                swampPuddleLevel,
                                                SurfaceRules.ifTrue(
                                                        SurfaceRules.not(aboveOverworldSeaLevel), SurfaceRules.ifTrue(SurfaceRules.noiseCondition2d(Noises.SWAMP, 0.0), WATER)
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(
                                        SurfaceRules.isBiome(biomes, Biomes.MANGROVE_SWAMP),
                                        SurfaceRules.ifTrue(
                                                mangroveSwampPuddleLevel,
                                                SurfaceRules.ifTrue(
                                                        SurfaceRules.not(aboveOverworldSeaLevel), SurfaceRules.ifTrue(SurfaceRules.noiseCondition2d(Noises.SWAMP, 0.0), WATER)
                                                )
                                        )
                                )
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(biomes, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(
                                        SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(badlandsTop, ORANGE_TERRACOTTA),
                                                SurfaceRules.ifTrue(
                                                        badlandsMid,
                                                        SurfaceRules.sequence(
                                                                SurfaceRules.ifTrue(clayBand1, TERRACOTTA),
                                                                SurfaceRules.ifTrue(clayBand2, TERRACOTTA),
                                                                SurfaceRules.ifTrue(clayBand3, TERRACOTTA),
                                                                SurfaceRules.bandlands()
                                                        )
                                                ),
                                                SurfaceRules.ifTrue(notUnderwater, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), RED_SAND)),
                                                SurfaceRules.ifTrue(SurfaceRules.not(hole), ORANGE_TERRACOTTA),
                                                SurfaceRules.ifTrue(notUnderDeepWater, WHITE_TERRACOTTA),
                                                gravelOrStoneIfCeiling
                                        )
                                ),
                                SurfaceRules.ifTrue(
                                        badlandsHeightCondition,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(aboveOverworldSeaLevel, SurfaceRules.ifTrue(SurfaceRules.not(badlandsMid), ORANGE_TERRACOTTA)),
                                                SurfaceRules.bandlands()
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(notUnderDeepWater, WHITE_TERRACOTTA))
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.ifTrue(
                                notUnderwater,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(
                                                frozenOcean,
                                                SurfaceRules.ifTrue(
                                                        hole, SurfaceRules.sequence(SurfaceRules.ifTrue(aboveWater, AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), ICE), WATER)
                                                )
                                        ),
                                        biomeSurfaceRule
                                )
                        )
                ),
                SurfaceRules.ifTrue(
                        notUnderDeepWater,
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(frozenOcean, SurfaceRules.ifTrue(hole, WATER))),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, biomeUnderSurfaceRule),
                                SurfaceRules.ifTrue(biomesWithSandAndSandstone, SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SANDSTONE)),
                                SurfaceRules.ifTrue(biomesWithSandAndVeryDeepSandstone, SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, SANDSTONE))
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE),
                                SurfaceRules.ifTrue(
                                        SurfaceRules.isBiome(biomes, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), sandOrSandstoneIfCeiling
                                ),
                                gravelOrStoneIfCeiling
                        )
                )
        );
        ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
        if (bedrockRoof) {
            builder.add(
                    SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK)
            );
        }

        if (bedrockFloor) {
            builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
        }

        SurfaceRules.RuleSource ruleAbovePreliminarySurface = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), mainRuleCloseToSurface);
        builder.add(doPreliminarySurfaceCheck ? ruleAbovePreliminarySurface : mainRuleCloseToSurface);
        builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(biomes, Biomes.SULFUR_CAVES), sulfurCaveBands));
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("malatite",
                VerticalAnchor.absolute(MALATITE_Y), VerticalAnchor.absolute(MALATITE_Y+ LAYER_GRADIENT_BLOCKS)), MALATITE));
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("magneiss",
                VerticalAnchor.absolute(MAGNEISS_Y), VerticalAnchor.absolute(MAGNEISS_Y+ LAYER_GRADIENT_BLOCKS)), MAGNEISS));
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("schinite",
                VerticalAnchor.absolute(SCHINITE_Y), VerticalAnchor.absolute(SCHINITE_Y+ LAYER_GRADIENT_BLOCKS)), SCHINITE));
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate",
                VerticalAnchor.absolute(DEEPSLATE_Y), VerticalAnchor.absolute(DEEPSLATE_Y + LAYER_GRADIENT_BLOCKS)), DEEPSLATE));
        return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
    }
    public static SurfaceRules.RuleSource air() {
        return AIR;
    }

    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double threshold) {
        return SurfaceRules.noiseCondition2d(Noises.SURFACE, threshold / 8.25, Double.MAX_VALUE);
    }

}
