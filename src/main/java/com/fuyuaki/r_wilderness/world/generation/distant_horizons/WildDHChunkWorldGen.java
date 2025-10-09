package com.fuyuaki.r_wilderness.world.generation.distant_horizons;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.enums.EDhApiDetailLevel;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGeneratorReturnType;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.AbstractDhApiChunkWorldGenerator;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.IDhApiWorldGenerator;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.data.DhApiChunk;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.ArrayList;

public class WildDHChunkWorldGen extends AbstractDhApiChunkWorldGenerator {

    private final IDhApiLevelWrapper levelWrapper;
    private final LevelReader level;

    public WildDHChunkWorldGen(IDhApiLevelWrapper levelWrapper) {
        this.levelWrapper = levelWrapper;
        this.level = (LevelReader) levelWrapper.getWrappedMcObject();
    }
    public static void registerForLevel(IDhApiLevelWrapper levelWrapper)
    {
        IDhApiWorldGenerator worldGenerator = new WildDHChunkWorldGen(levelWrapper);
        DhApi.worldGenOverrides.registerWorldGeneratorOverride(levelWrapper, worldGenerator);

    }

    @Override
    public EDhApiWorldGeneratorReturnType getReturnType() {
        return EDhApiWorldGeneratorReturnType.API_CHUNKS;
    }


    @Override
    public Object[] generateChunk(int chunkX, int chunkZ, EDhApiDistantGeneratorMode eDhApiDistantGeneratorMode){
        ChunkAccess chunk = this.level.getChunk(chunkX, chunkZ);
        return new Object[] { chunk, this.level };
    }

    @Override
    public DhApiChunk generateApiChunk(int chunkPosX, int chunkPosZ, EDhApiDistantGeneratorMode generatorMode) {
        ChunkAccess chunk = this.level.getChunk(chunkPosX, chunkPosZ);


        int minBuildHeight = chunk.getMinY();
        int maxBuildHeight = chunk.getMaxY();

        DhApiChunk apiChunk = DhApiChunk.create(chunkPosX, chunkPosZ, minBuildHeight, maxBuildHeight);
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                ArrayList<DhApiTerrainDataPoint> dataPoints = new ArrayList<>();

                IDhApiBlockStateWrapper block = null;
                IDhApiBiomeWrapper biome = null;

                for (int y = minBuildHeight; y < maxBuildHeight; y++)
                {
                    // Note: air/empty spaces must be defined, otherwise DH will fail to downsample correctly
                    // and LODs will have the incorrect lighting.
                    block = DhApi.Delayed.wrapperFactory.getBlockStateWrapper(new Object[]{ chunk.getBlockState(new BlockPos(x, y, z)) }, this.levelWrapper);
                    biome = DhApi.Delayed.wrapperFactory.getBiomeWrapper(new Object[]{ chunk.getNoiseBiome(x, y, z) }, this.levelWrapper);
                    // Note: merging identical datapoints together will improve processing speed and reduce filesize
                    dataPoints.add(DhApiTerrainDataPoint.create(EDhApiDetailLevel.BLOCK.detailLevel, 0, 15, y, y+1, block, biome));
                }

                //Collections.reverse(dataPoints);
                // the api chunk can accept datapoints in either top-down or bottom-up order
                apiChunk.setDataPoints(x, z, dataPoints);
            }
        }
        return apiChunk;
    }

    @Override
    public void preGeneratorTaskStart() {

    }

    @Override
    public void close() {

    }
}
