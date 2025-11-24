package com.fuyuaki.r_wilderness.world.generation.carvers;

import com.fuyuaki.r_wilderness.world.generation.WildChunkGenerator;
import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.generation.terrain.SurfaceRulesContextExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.carver.CarvingContext;

import java.util.Optional;
import java.util.function.Function;

public class WildCarverContext extends CarvingContext {
    private final RegistryAccess registryAccess;
    private final WRNoiseChunk noiseChunk;
    private final RandomState randomState;
    private final SurfaceRules.RuleSource surfaceRule;
    public WildCarverContext(WildChunkGenerator generator, RegistryAccess registryAccess, LevelHeightAccessor level, WRNoiseChunk noiseChunk, RandomState randomState, SurfaceRules.RuleSource surfaceRule) {
        super(null, registryAccess, level, null, randomState, surfaceRule);
        this.minY = Math.max(level.getMinY(), generator.getMinY());
        this.height = Math.min(level.getHeight(), generator.getGenDepth());
        this.registryAccess = registryAccess;
        this.noiseChunk = noiseChunk;
        this.randomState = randomState;
        this.surfaceRule = surfaceRule;
    }

    @Override
    public Optional<BlockState> topMaterial(Function<BlockPos, Holder<Biome>> biomeMapper, ChunkAccess access, BlockPos pos, boolean hasFluid) {
        SurfaceRules.Context surfacerules$context = new SurfaceRules.Context(this.randomState.surfaceSystem(), randomState, access, null, biomeMapper, this.registryAccess().lookupOrThrow(Registries.BIOME), this);

        ((SurfaceRulesContextExtension) (Object) surfacerules$context).setNoiseChunk(noiseChunk);

        SurfaceRules.SurfaceRule surfacerules$surfacerule = this.surfaceRule.apply(surfacerules$context);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        surfacerules$context.updateXZ(i, k);
        surfacerules$context.updateY(1, 1, hasFluid ? j + 1 : Integer.MIN_VALUE, i, j, k);
        BlockState blockstate = surfacerules$surfacerule.tryApply(i, j, k);
        return Optional.ofNullable(blockstate);
    }
    public RegistryAccess registryAccess() {
        return this.registryAccess;
    }

    public RandomState randomState() {
        return this.randomState;
    }
}
