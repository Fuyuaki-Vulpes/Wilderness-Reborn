package com.fuyuaki.r_wilderness.world.level.biome;

import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class RebornBiomeSource extends BiomeSource implements BiomeManager.NoiseBiomeSource {

    protected TerrainParameters parameters = null;
    protected List<RebornBiomePlacement> knownBiomes;


    public static final MapCodec<RebornBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
            stable -> stable.group(
                            RebornBiomePlacement.CODEC.listOf().fieldOf("biome_lookup").forGetter(RebornBiomeSource::getKnownBiomes)
                    )
                    .apply(stable, stable.stable(RebornBiomeSource::new))
    );


    public RebornBiomeSource() {
        this.knownBiomes = List.of();
    }

    public RebornBiomeSource(List<RebornBiomePlacement> knownBiomes) {
        this.knownBiomes = knownBiomes;
    }


    public void setParameters(TerrainParameters parameters) {
        this.parameters = parameters;
    }

    public TerrainParameters getParameters() {
        return parameters;
    }

    public void setKnownBiomes(List<RebornBiomePlacement> knownBiomes) {
        this.knownBiomes = knownBiomes;
        this.possibleBiomes = Suppliers.memoize(
                () -> this.knownBiomes.stream().map(RebornBiomePlacement::biome).distinct().collect(ImmutableSet.toImmutableSet())
        );

    }

    public List<RebornBiomePlacement> getKnownBiomes() {
        return knownBiomes;
    }

    @Override
    public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler sampler) {
        int xRadMin = x - radius;
        int yRadMin = y - radius;
        int zRadMin = z - radius;
        int xRadMax = x + radius;
        int yRadMax = y + radius;
        int zRadMax = z + radius;
        int xDiam = xRadMax - xRadMin + 1;
        int yDiam = yRadMax - yRadMin + 1;
        int zDiam = zRadMax - zRadMin + 1;
        Set<Holder<Biome>> set = Sets.newHashSet();

        for (int j2 = 0; j2 < zDiam; j2++) {
            for (int k2 = 0; k2 < xDiam; k2++) {
                for (int l2 = 0; l2 < yDiam; l2++) {
                    int i3 = xRadMin + k2;
                    int j3 = yRadMin + l2;
                    int k3 = zRadMin + j2;
                    set.add(this.getNoiseBiome(i3, j3, k3, sampler));
                }
            }
        }

        return set;
    }

    @Nullable
    public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
            int x, int y, int z, int radius, Predicate<Holder<Biome>> biomePredicate, RandomSource random, Climate.Sampler sampler
    ) {
        return this.findBiomeHorizontal(x, y, z, radius, 1, biomePredicate, random, false, sampler);
    }

    @Nullable
    public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(
            BlockPos pos, int radius, int horizontalStep, int verticalStep, Predicate<Holder<Biome>> biomePredicate, Climate.Sampler sampler, LevelReader level
    ) {
        Set<Holder<Biome>> set = this.possibleBiomes().stream().filter(biomePredicate).collect(Collectors.toUnmodifiableSet());
        if (!set.isEmpty()) {
            int step = Math.floorDiv(radius, horizontalStep);
            int[] aint = Mth.outFromOrigin(pos.getY(), level.getMinY() + 1, level.getMaxY() + 1, verticalStep).toArray();

            for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.spiralAround(BlockPos.ZERO, step, Direction.EAST, Direction.SOUTH)) {
                int x = pos.getX() + blockpos$mutableblockpos.getX() * horizontalStep;
                int z = pos.getZ() + blockpos$mutableblockpos.getZ() * horizontalStep;

                TerrainParameters.Sampled sampled = parameters.samplerAt(x,z);

                for (int y : aint) {

                    Holder<Biome> holder = this.cycleThroughBiomePlacements(x, y, z,sampled).biome();
                    if (set.contains(holder)) {
                        return Pair.of(new BlockPos(x, y, z), holder);
                    }
                }
            }

        }
        return null;
    }

    @Nullable
    public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
            int x,
            int y,
            int z,
            int radius,
            int increment,
            Predicate<Holder<Biome>> biomePredicate,
            RandomSource random,
            boolean findClosest,
            Climate.Sampler sampler
    ) {
        Pair<BlockPos, Holder<Biome>> pair = null;
        int i1 = 0;
        int j1 = findClosest ? 0 : radius;
        int k1 = j1;

        while (k1 <= radius) {
            for (int l1 = !SharedConstants.DEBUG_ONLY_GENERATE_HALF_THE_WORLD && !SharedConstants.debugGenerateSquareTerrainWithoutNoise ? -k1 : 0;
                 l1 <= k1;
                 l1 += increment
            ) {
                boolean flag = Math.abs(l1) == k1;

                for (int i2 = -k1; i2 <= k1; i2 += increment) {
                    if (findClosest) {
                        boolean flag1 = Math.abs(i2) == k1;
                        if (!flag1 && !flag) {
                            continue;
                        }
                    }

                    int xPos = x + i2;
                    int zPos = z + l1;

                    TerrainParameters.Sampled sampled = parameters.samplerAt(xPos,zPos);

                    Holder<Biome> holder = this.cycleThroughBiomePlacements(xPos, y, zPos,sampled).biome();
                    if (biomePredicate.test(holder)) {
                        if (pair == null || random.nextInt(i1 + 1) == 0) {
                            BlockPos blockpos = new BlockPos(xPos, y, zPos);
                            if (findClosest) {
                                return Pair.of(blockpos, holder);
                            }

                            pair = Pair.of(blockpos, holder);
                        }

                        i1++;
                    }
                }
            }

            k1 += increment;
        }

        return pair;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.knownBiomes.stream().map(RebornBiomePlacement::biome);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        TerrainParameters.Sampled sampled = parameters.samplerAt(x,z);

        return cycleThroughBiomePlacements(x,y,z,sampled).biome();
    }

    public RebornBiomePlacement cycleThroughBiomePlacements(int x, int y, int z, TerrainParameters.Sampled sampled){
        RebornBiomePlacement biome = null;
        double affinity = -1000;
        double surfaceY = parameters.yLevelAt(x,z,true);


        for (RebornBiomePlacement placement : this.knownBiomes){
            double pD = placement.tryAt(surfaceY,sampled,x,y,z);
            if (pD > affinity){
                affinity = pD;
                biome = placement;
            }
        }

        return biome;
    }

    @Override
    public void addDebugInfo(List<String> info, BlockPos pos, Climate.Sampler sampler) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        TerrainParameters.Sampled sampled = this.parameters.samplerAtCached(x,z);
        DecimalFormat decimalformat = new DecimalFormat("0.000");


        List<RebornBiomePlacement.TerrainStates> states = RebornBiomePlacement.TerrainStates.statesAt(sampled,pos.getY());

        String terrainsAt = "";

        for (RebornBiomePlacement.TerrainStates state : states){
            if (states.getLast() != state){
            terrainsAt = append(terrainsAt, state.getSerializedName() + ", ");
            }else {
                terrainsAt = append(terrainsAt, state.getSerializedName());

            }
        }

        info.add(
                "Biome builder: "
        );
        info.add(
                "Temperature: " + decimalformat.format(sampled.temperature()) + " Humidity: " + decimalformat.format(sampled.humidity())
        );
        info.add(
                "Vegetation Density: " + decimalformat.format(sampled.vegetationDensity()) + " Rockiness: " + decimalformat.format(sampled.rockyness())
        );
        info.add(
                "Continentalness: " + decimalformat.format(sampled.continentalness()) + " Erosion: " + decimalformat.format(sampled.erosion())
        );
        info.add(
                "Weirdness: " + decimalformat.format(sampled.weirdness()) + " Magicalness: " + decimalformat.format(sampled.magicalness())
        );
        info.add("Badlands: " + decimalformat.format(sampled.badlands()) + " Terrain Types: " + terrainsAt);

    }

    private static String append(String first, String second) {
        return first + second;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z) {
        int blockZ = QuartPos.toBlock(z);
        int blockX = QuartPos.toBlock(x);

        TerrainParameters.Sampled sampled = parameters.samplerAt(blockX,blockZ);

        return cycleThroughBiomePlacements(blockX,QuartPos.toBlock(y), blockZ,sampled).biome();
    }

    public class Registerer{
        public static final DeferredRegister<MapCodec<? extends BiomeSource>> SOURCE = DeferredRegister.create(Registries.BIOME_SOURCE, MODID);

        public static final Supplier<MapCodec<? extends BiomeSource>> REBORN_SOURCE = SOURCE.register("reborn_biome_source",() -> CODEC);

        public static void init(IEventBus bus){
            SOURCE.register(bus);
        }
    }
}
