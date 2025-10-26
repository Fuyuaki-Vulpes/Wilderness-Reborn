package com.fuyuaki.r_wilderness.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModSurfaceRuleData {

    private static final SurfaceRules.RuleSource AIR = makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA);
    private static final SurfaceRules.RuleSource TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.RuleSource GLASS = makeStateRule(Blocks.GLASS);
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
    private static final SurfaceRules.RuleSource LAVA = makeStateRule(Blocks.LAVA);
    private static final SurfaceRules.RuleSource NETHERRACK = makeStateRule(Blocks.NETHERRACK);
    private static final SurfaceRules.RuleSource SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
    private static final SurfaceRules.RuleSource SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
    private static final SurfaceRules.RuleSource BASALT = makeStateRule(Blocks.BASALT);
    private static final SurfaceRules.RuleSource BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
    private static final SurfaceRules.RuleSource WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
    private static final SurfaceRules.RuleSource WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
    private static final SurfaceRules.RuleSource NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
    private static final SurfaceRules.RuleSource CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
    private static final SurfaceRules.RuleSource ENDSTONE = makeStateRule(Blocks.END_STONE);

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static SurfaceRules.RuleSource overworld() {
        return overworldLike(true, true);
    }

    public static SurfaceRules.RuleSource overworldLike(boolean aboveGround, boolean bedrockFloor) {

        SurfaceRules.ConditionSource y97 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
        SurfaceRules.ConditionSource y256 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
        SurfaceRules.ConditionSource y63Depth1 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
        SurfaceRules.ConditionSource y74 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
        SurfaceRules.ConditionSource y61 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
        SurfaceRules.ConditionSource y62 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
        SurfaceRules.ConditionSource y63 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
        SurfaceRules.ConditionSource surfacerules$conditionsource7 = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource surfacerules$conditionsource8 = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.ConditionSource surfacerules$conditionsource9 = SurfaceRules.waterStartCheck(-6, -1);
        SurfaceRules.ConditionSource surfacerules$conditionsource10 = SurfaceRules.hole();
        SurfaceRules.ConditionSource isFrozenOcean = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        SurfaceRules.ConditionSource isSteep = SurfaceRules.steep();
        SurfaceRules.RuleSource dirtNoSteep = SurfaceRules.ifTrue(SurfaceRules.not(isSteep),DIRT);
        SurfaceRules.RuleSource coarseDirtNoSteep = SurfaceRules.ifTrue(SurfaceRules.not(isSteep),COARSE_DIRT);
        SurfaceRules.RuleSource podzolNoSteep = SurfaceRules.ifTrue(SurfaceRules.not(isSteep),PODZOL);
        SurfaceRules.RuleSource makeGrassSurfance = SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules$conditionsource8, GRASS_BLOCK), DIRT);
        SurfaceRules.RuleSource grassSurfaceNoSteep = SurfaceRules.ifTrue(SurfaceRules.not(isSteep),makeGrassSurfance);
        SurfaceRules.RuleSource makeSandSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
        SurfaceRules.RuleSource makeGravelSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
        SurfaceRules.ConditionSource isWarmOceanSnowyOceanOrBeach = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
        SurfaceRules.ConditionSource isDesert = SurfaceRules.isBiome(Biomes.DESERT);
        SurfaceRules.RuleSource surfacerules$rulesource3 = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.STONY_PEAKS),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125, 0.0125), CALCITE), STONE)
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.STONY_SHORE),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05, 0.05), makeGravelSurface), STONE)
                ),
                SurfaceRules.ifTrue(isSteep, STONE),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE)),
                SurfaceRules.ifTrue(isWarmOceanSnowyOceanOrBeach, makeSandSurface),
                SurfaceRules.ifTrue(isDesert, makeSandSurface),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), STONE)
        );
        SurfaceRules.RuleSource makePowderSnow = SurfaceRules.ifTrue(
                SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45, 0.58), SurfaceRules.ifTrue(surfacerules$conditionsource8, POWDER_SNOW)
        );
        SurfaceRules.RuleSource makePowderSnowLarge = SurfaceRules.ifTrue(
                SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6), SurfaceRules.ifTrue(surfacerules$conditionsource8, POWDER_SNOW)
        );



        SurfaceRules.RuleSource underSurfaceRule = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.FROZEN_PEAKS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(isSteep, PACKED_ICE),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625, 0.025), ICE),
                                SurfaceRules.ifTrue(surfacerules$conditionsource8, SNOW_BLOCK)
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.SNOWY_SLOPES),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(isSteep, STONE),
                                makePowderSnow,
                                SurfaceRules.ifTrue(surfacerules$conditionsource8, SNOW_BLOCK)
                        )
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), STONE),
                surfacerules$rulesource3,
                SurfaceRules.ifTrue(SurfaceRules.not(isSteep),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(makePowderSnow, dirtNoSteep)),
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), STONE)),
                                SurfaceRules.ifTrue(
                                        SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), makeGravelSurface),
                                                SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE),
                                                SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), dirtNoSteep),
                                                makeGravelSurface
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD)
                        )
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.RIVER,Biomes.FROZEN_RIVER),
                        SurfaceRules.ifTrue(ModSurfaceRuleData.continental_threshold(NoiseRouterData.CONTINENTS,-0.2F,-0.111F), makeSandSurface)),
                dirtNoSteep
        );
        SurfaceRules.RuleSource surfaceRule = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.FROZEN_PEAKS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(isSteep, PACKED_ICE),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0, 0.025), ICE),
                                SurfaceRules.ifTrue(surfacerules$conditionsource8, SNOW_BLOCK)
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.SNOWY_SLOPES),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(isSteep, STONE),
                                makePowderSnowLarge,
                                SurfaceRules.ifTrue(surfacerules$conditionsource8, SNOW_BLOCK)
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.JAGGED_PEAKS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(isSteep, STONE), SurfaceRules.ifTrue(surfacerules$conditionsource8, SNOW_BLOCK)
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.GROVE),
                        SurfaceRules.sequence(makePowderSnowLarge, SurfaceRules.ifTrue(surfacerules$conditionsource8, SNOW_BLOCK))
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES), SurfaceRules.ifTrue(surfacerules$conditionsource8, SNOW_BLOCK)),
                surfacerules$rulesource3,
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.5), coarseDirtNoSteep))
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), makeGravelSurface),
                                SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE),
                                SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), grassSurfaceNoSteep),
                                makeGravelSurface
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), coarseDirtNoSteep), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.95), podzolNoSteep))
                ),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), MYCELIUM),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.RIVER,Biomes.FROZEN_RIVER),
                        SurfaceRules.ifTrue(ModSurfaceRuleData.continental_threshold(NoiseRouterData.CONTINENTS,-0.2F,-0.1F), makeSandSurface)),
                grassSurfaceNoSteep
        );
        SurfaceRules.ConditionSource surfaceNoiseVeryNegative = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909, -0.5454);
        SurfaceRules.ConditionSource surfaceNoiseMedian = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
        SurfaceRules.ConditionSource surfaceNoiseVeryPositive = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454, 0.909);
        SurfaceRules.RuleSource badlandsAndSurfaces = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(
                                        SurfaceRules.isBiome(Biomes.WOODED_BADLANDS),
                                        SurfaceRules.ifTrue(
                                                y97,
                                                SurfaceRules.ifTrue(SurfaceRules.not(isSteep),
                                                SurfaceRules.sequence(
                                                        SurfaceRules.ifTrue(surfaceNoiseVeryNegative, coarseDirtNoSteep),
                                                        SurfaceRules.ifTrue(surfaceNoiseMedian, coarseDirtNoSteep),
                                                        SurfaceRules.ifTrue(surfaceNoiseVeryPositive, coarseDirtNoSteep),
                                                        grassSurfaceNoSteep
                                                )
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(
                                        SurfaceRules.isBiome(Biomes.SWAMP),
                                        SurfaceRules.ifTrue(
                                                y62,
                                                SurfaceRules.ifTrue(
                                                        SurfaceRules.not(y63), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER)
                                                )
                                        )
                                ),
                                SurfaceRules.ifTrue(
                                        SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP),
                                        SurfaceRules.ifTrue(
                                                y61,
                                                SurfaceRules.ifTrue(
                                                        SurfaceRules.not(y63), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER)
                                                )
                                        )
                                )
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(
                                        SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(y256, ORANGE_TERRACOTTA),
                                                SurfaceRules.ifTrue(
                                                        y74,
                                                        SurfaceRules.sequence(
                                                                SurfaceRules.ifTrue(surfaceNoiseVeryNegative, TERRACOTTA),
                                                                SurfaceRules.ifTrue(surfaceNoiseMedian, TERRACOTTA),
                                                                SurfaceRules.ifTrue(surfaceNoiseVeryPositive, TERRACOTTA),
                                                                SurfaceRules.bandlands()
                                                        )
                                                ),
                                                SurfaceRules.ifTrue(
                                                        surfacerules$conditionsource7, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), RED_SAND)
                                                ),
                                                SurfaceRules.ifTrue(SurfaceRules.not(surfacerules$conditionsource10), ORANGE_TERRACOTTA),
                                                SurfaceRules.ifTrue(surfacerules$conditionsource9, WHITE_TERRACOTTA),
                                                makeGravelSurface
                                        )
                                ),
                                SurfaceRules.ifTrue(
                                        y63Depth1,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(
                                                        y63, SurfaceRules.ifTrue(SurfaceRules.not(y74), ORANGE_TERRACOTTA)
                                                ),
                                                SurfaceRules.bandlands()
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(surfacerules$conditionsource9, WHITE_TERRACOTTA))
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.ifTrue(
                                surfacerules$conditionsource7,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(
                                                isFrozenOcean,
                                                SurfaceRules.ifTrue(
                                                        surfacerules$conditionsource10,
                                                        SurfaceRules.sequence(
                                                                SurfaceRules.ifTrue(surfacerules$conditionsource8, AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), ICE), WATER
                                                        )
                                                )
                                        ),
                                        surfaceRule
                                )
                        )
                ),
                SurfaceRules.ifTrue(
                        surfacerules$conditionsource9,
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(
                                        SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(isFrozenOcean, SurfaceRules.ifTrue(surfacerules$conditionsource10, WATER))
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, underSurfaceRule),
                                SurfaceRules.ifTrue(isWarmOceanSnowyOceanOrBeach, SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SANDSTONE)),
                                SurfaceRules.ifTrue(isDesert, SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, SANDSTONE))
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE),
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), makeSandSurface),
                                makeGravelSurface
                        )
                )
        );
        ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();

        if (bedrockFloor) {
            builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
        }

        SurfaceRules.RuleSource surfacerules$rulesource9 = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), badlandsAndSurfaces);
        builder.add(aboveGround ? surfacerules$rulesource9 : badlandsAndSurfaces);
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));


        return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
    }

    private static SurfaceRules.ConditionSource continental_threshold(ResourceKey<DensityFunction> densityFunction, double minThreshold, double maxThreshold) {
        return new ContinentalnessSource(densityFunction, minThreshold, maxThreshold);
    }


    public static SurfaceRules.RuleSource air() {
        return AIR;
    }

    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double value) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, value / 8.25, Double.MAX_VALUE);
    }

    record ContinentalnessSource(ResourceKey<DensityFunction> densityMap, double minThreshold, double maxThreshold) implements SurfaceRules.ConditionSource {
        static final KeyDispatchDataCodec<ContinentalnessSource> CODEC = KeyDispatchDataCodec.of(
                RecordCodecBuilder.mapCodec(
                        p_258995_ -> p_258995_.group(
                                        ResourceKey.codec(Registries.DENSITY_FUNCTION).fieldOf("densityMap").forGetter(ContinentalnessSource::densityMap),
                                        Codec.DOUBLE.fieldOf("min_threshold").forGetter(ContinentalnessSource::minThreshold),
                                        Codec.DOUBLE.fieldOf("max_threshold").forGetter(ContinentalnessSource::maxThreshold)
                                )
                                .apply(p_258995_, ContinentalnessSource::new)
                )
        );

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRules.Condition apply(SurfaceRules.Context ruleContext) {
            final DensityFunction densityFunction = ruleContext.randomState.router().continents();
            final DensityFunction.FunctionContext functionContext = new DensityFunction.FunctionContext() {
                @Override
                public int blockX() {
                    return ruleContext.blockX;
                }

                @Override
                public int blockY() {
                    return ruleContext.blockY;
                }

                @Override
                public int blockZ() {
                    return ruleContext.blockZ;
                }
            };

            class ContinentalnessThresholdCondition extends SurfaceRules.LazyXZCondition {
                ContinentalnessThresholdCondition() {
                    super(ruleContext);
                }

                @Override
                protected boolean compute() {
                    double d0 = densityFunction.compute(functionContext);
                    return d0 >= ContinentalnessSource.this.minThreshold && d0 <= ContinentalnessSource.this.maxThreshold;
                }
            }

            return new ContinentalnessThresholdCondition();
        }
    }
    public interface ConditionSource extends Function<SurfaceRules.Context, SurfaceRules.Condition> {
        static final DeferredRegister<MapCodec<? extends SurfaceRules.ConditionSource>> CONDITIONS = DeferredRegister.create(BuiltInRegistries.MATERIAL_CONDITION,MODID);

        public static final DeferredHolder<MapCodec<? extends SurfaceRules.ConditionSource>, MapCodec<? extends SurfaceRules.ConditionSource>> CONTINENTALNESS_CONDITION =
                CONDITIONS.register("continentalness_threshold", ContinentalnessSource.CODEC::codec);

        public static void init(IEventBus bus){
            CONDITIONS.register(bus);
        }
    }
}
