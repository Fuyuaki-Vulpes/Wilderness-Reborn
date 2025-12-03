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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

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
                int firstY = minBuildHeight;
                int sLight = 0;
                int bLight = 0;

                int surfaceY = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
                int terrainStartY = surfaceY - 32;

                int gap = 16;
                for (int y = minBuildHeight; y < maxBuildHeight; y+= gap) {
                    if (y > terrainStartY){
                        gap = 1;
                    }
                    BlockPos thisBlock = new BlockPos(x, y, z);
                    BlockState b = chunk.getBlockState(thisBlock);
                    IDhApiBlockStateWrapper newBlock = DhApi.Delayed.wrapperFactory.getBlockStateWrapper(new Object[]{b}, this.levelWrapper);
                    IDhApiBiomeWrapper newBiome = DhApi.Delayed.wrapperFactory.getBiomeWrapper(new Object[]{ chunk.getNoiseBiome(x, y, z)}, this.levelWrapper);
                    int blockLight = chunk.getLevel().getBrightness(LightLayer.BLOCK,thisBlock);
                    int skyLight = chunk.getLevel().getBrightness(LightLayer.SKY,thisBlock);
                    if (biome == null || block == null){
                        block = newBlock;
                        biome = newBiome;
                        sLight = skyLight;
                        bLight = blockLight;
                    }
                    if (
                            bLight != blockLight
                            || sLight != skyLight
                            || newBlock != block
                            || newBiome != biome){
                        dataPoints.add(DhApiTerrainDataPoint.create(EDhApiDetailLevel.BLOCK.detailLevel, bLight, sLight, firstY, y, block, biome));
                        firstY = y;
                        sLight = skyLight;
                        bLight = blockLight;
                        block = newBlock;
                        biome = newBiome;
                    }
                    if (maxBuildHeight -1 == y){
                        dataPoints.add(DhApiTerrainDataPoint.create(EDhApiDetailLevel.BLOCK.detailLevel, blockLight, skyLight, y, y+1, newBlock, newBiome));

                    }

                }

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
