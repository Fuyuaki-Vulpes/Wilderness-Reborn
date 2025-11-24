package com.fuyuaki.r_wilderness.mixin;

import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import com.google.common.collect.ImmutableList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(RuinedPortalStructure.class)
public abstract class RuinedPortalStructureMixin {
    @Shadow @Final private List<RuinedPortalStructure.Setup> setups;


    @Shadow @Final private static String[] STRUCTURE_LOCATION_GIANT_PORTALS;

    @Shadow @Final private static String[] STRUCTURE_LOCATION_PORTALS;



    private static boolean sample(WorldgenRandom random, float threshold) {
        if (threshold == 0.0F) {
            return false;
        } else {
            return threshold == 1.0F ? true : random.nextFloat() < threshold;
        }
    }


    private static boolean isCold(BlockPos pos, Holder<Biome> biome, int seaLevel) {
        return biome.value().coldEnoughToSnow(pos, seaLevel);
    }

    private static int findSuitableY(
            RandomSource random,
            ChunkGenerator chunkGenerator,
            RuinedPortalPiece.VerticalPlacement verticalPlacement,
            boolean airPocket,
            int height,
            int blockCountY,
            BoundingBox box,
            LevelHeightAccessor level,
            RandomState randomState
    ) {
        int j = level.getMinY() + 15;
        int i;
        if (verticalPlacement == RuinedPortalPiece.VerticalPlacement.IN_NETHER) {
            if (airPocket) {
                i = Mth.randomBetweenInclusive(random, 32, 100);
            } else if (random.nextFloat() < 0.5F) {
                i = Mth.randomBetweenInclusive(random, 27, 29);
            } else {
                i = Mth.randomBetweenInclusive(random, 29, 100);
            }
        } else if (verticalPlacement == RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN) {
            int k = height - blockCountY;
            i = getRandomWithinInterval(random, 70, k);
        } else if (verticalPlacement == RuinedPortalPiece.VerticalPlacement.UNDERGROUND) {
            int j1 = height - blockCountY;
            i = getRandomWithinInterval(random, j, j1);
        } else if (verticalPlacement == RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED) {
            i = height - blockCountY + Mth.randomBetweenInclusive(random, 2, 8);
        } else {
            i = height;
        }

        List<BlockPos> list1 = ImmutableList.of(
                new BlockPos(box.minX(), 0, box.minZ()),
                new BlockPos(box.maxX(), 0, box.minZ()),
                new BlockPos(box.minX(), 0, box.maxZ()),
                new BlockPos(box.maxX(), 0, box.maxZ())
        );
        List<NoiseColumn> list = list1.stream()
                .map(p_229280_ -> chunkGenerator.getBaseColumn(p_229280_.getX(), p_229280_.getZ(), level, randomState))
                .collect(Collectors.toList());
        Heightmap.Types heightmap$types = verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR
                ? Heightmap.Types.OCEAN_FLOOR_WG
                : Heightmap.Types.WORLD_SURFACE_WG;

        int l;
        for (l = i; l > j; l--) {
            int i1 = 0;

            for (NoiseColumn noisecolumn : list) {
                BlockState blockstate = noisecolumn.getBlock(l);
                if (heightmap$types.isOpaque().test(blockstate)) {
                    if (++i1 == 3) {
                        return l;
                    }
                }
            }
        }

        return l;
    }

    private static int getRandomWithinInterval(RandomSource random, int min, int max) {
        return min < max ? Mth.randomBetweenInclusive(random, min, max) : max;
    }


    @Inject(method = "findGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;", at = @At(value = "HEAD"), cancellable = true)
    private void findGenerationPoint(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (!(context.chunkGenerator().getBiomeSource() instanceof RebornBiomeSource)) return;
        RuinedPortalPiece.Properties ruinedportalpiece$properties = new RuinedPortalPiece.Properties();
        WorldgenRandom worldgenrandom = context.random();
        RuinedPortalStructure.Setup ruinedportalstructure$setup = null;
        if (this.setups.size() > 1) {
            float f = 0.0F;

            for (RuinedPortalStructure.Setup ruinedportalstructure$setup1 : this.setups) {
                f += ruinedportalstructure$setup1.weight();
            }

            float f1 = worldgenrandom.nextFloat();

            for (RuinedPortalStructure.Setup ruinedportalstructure$setup2 : this.setups) {
                f1 -= ruinedportalstructure$setup2.weight() / f;
                if (f1 < 0.0F) {
                    ruinedportalstructure$setup = ruinedportalstructure$setup2;
                    break;
                }
            }
        } else {
            ruinedportalstructure$setup = this.setups.get(0);
        }

        if (ruinedportalstructure$setup == null) {
            throw new IllegalStateException();
        } else {
            RuinedPortalStructure.Setup ruinedportalstructure$setup3 = ruinedportalstructure$setup;
            ruinedportalpiece$properties.airPocket = sample(worldgenrandom, ruinedportalstructure$setup3.airPocketProbability());
            ruinedportalpiece$properties.mossiness = ruinedportalstructure$setup3.mossiness();
            ruinedportalpiece$properties.overgrown = ruinedportalstructure$setup3.overgrown();
            ruinedportalpiece$properties.vines = ruinedportalstructure$setup3.vines();
            ruinedportalpiece$properties.replaceWithBlackstone = ruinedportalstructure$setup3.replaceWithBlackstone();
            ResourceLocation resourcelocation;
            if (worldgenrandom.nextFloat() < 0.05F) {
                resourcelocation = ResourceLocation.withDefaultNamespace(
                        STRUCTURE_LOCATION_GIANT_PORTALS[worldgenrandom.nextInt(STRUCTURE_LOCATION_GIANT_PORTALS.length)]
                );
            } else {
                resourcelocation = ResourceLocation.withDefaultNamespace(STRUCTURE_LOCATION_PORTALS[worldgenrandom.nextInt(STRUCTURE_LOCATION_PORTALS.length)]);
            }

            StructureTemplate structuretemplate = context.structureTemplateManager().getOrCreate(resourcelocation);
            Rotation rotation = Util.getRandom(Rotation.values(), worldgenrandom);
            Mirror mirror = worldgenrandom.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
            BlockPos blockpos = new BlockPos(structuretemplate.getSize().getX() / 2, 0, structuretemplate.getSize().getZ() / 2);
            ChunkGenerator chunkgenerator = context.chunkGenerator();
            LevelHeightAccessor levelheightaccessor = context.heightAccessor();
            RandomState randomstate = context.randomState();
            BlockPos blockpos1 = context.chunkPos().getWorldPosition();
            BoundingBox boundingbox = structuretemplate.getBoundingBox(blockpos1, rotation, blockpos, mirror);
            BlockPos blockpos2 = boundingbox.getCenter();
            int i = chunkgenerator.getBaseHeight(
                    blockpos2.getX(),
                    blockpos2.getZ(),
                    RuinedPortalPiece.getHeightMapType(ruinedportalstructure$setup3.placement()),
                    levelheightaccessor,
                    randomstate
            )
                    - 1;
            int j = findSuitableY(
                    worldgenrandom,
                    chunkgenerator,
                    ruinedportalstructure$setup3.placement(),
                    ruinedportalpiece$properties.airPocket,
                    i,
                    boundingbox.getYSpan(),
                    boundingbox,
                    levelheightaccessor,
                    randomstate
            );
            BlockPos blockpos3 = new BlockPos(blockpos1.getX(), j, blockpos1.getZ());
            cir.setReturnValue( Optional.of(
                    new Structure.GenerationStub(
                            blockpos3,
                            p_229297_ -> {
                                if (ruinedportalstructure$setup3.canBeCold()) {
                                    ruinedportalpiece$properties.cold = isCold(
                                            blockpos3,
                                            context.chunkGenerator()
                                                    .getBiomeSource()
                                                    .getNoiseBiome(
                                                            blockpos3.getX(),
                                                            blockpos3.getY(),
                                                            blockpos3.getZ(),
                                                            randomstate.sampler()
                                                    ),
                                            chunkgenerator.getSeaLevel()
                                    );
                                }

                                p_229297_.addPiece(
                                        new RuinedPortalPiece(
                                                context.structureTemplateManager(),
                                                blockpos3,
                                                ruinedportalstructure$setup3.placement(),
                                                ruinedportalpiece$properties,
                                                resourcelocation,
                                                structuretemplate,
                                                rotation,
                                                mirror,
                                                blockpos
                                        )
                                );
                            }
                    )
            ));
        }
    }


}
