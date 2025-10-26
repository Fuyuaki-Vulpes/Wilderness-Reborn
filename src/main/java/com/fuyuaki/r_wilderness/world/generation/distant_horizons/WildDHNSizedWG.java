package com.fuyuaki.r_wilderness.world.generation.distant_horizons;

import com.mojang.logging.LogUtils;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.enums.EDhApiDetailLevel;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGeneratorReturnType;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.IDhApiWorldGenerator;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import com.seibel.distanthorizons.api.objects.data.IDhApiFullDataSource;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class WildDHNSizedWG implements IDhApiWorldGenerator {
    private static final Logger LOGGER = LogUtils.getLogger();




    private final IDhApiLevelWrapper levelWrapper;

    public WildDHNSizedWG(IDhApiLevelWrapper levelWrapper) {
        this.levelWrapper = levelWrapper;
    }

    public static void registerForLevel(IDhApiLevelWrapper levelWrapper)
    {

        IDhApiWorldGenerator worldGenerator = new WildDHChunkWorldGen(levelWrapper);
        DhApi.worldGenOverrides.registerWorldGeneratorOverride(levelWrapper, worldGenerator);

    }

    @Override
    public byte getSmallestDataDetailLevel() { return (byte) (EDhApiDetailLevel.BLOCK.detailLevel); }
    @Override
    public byte getLargestDataDetailLevel()
    {
        // This value determines the largest LOD area that can be generated
        // in this example 12 is equivalent to 2^12 (4096) blocks wide per LOD datapoint.
        // Try changing this number and watch how the LODs generated will generate at a higher detail level.
        return (byte) (EDhApiDetailLevel.BLOCK.detailLevel + 12);
    }


    @Override
    public EDhApiWorldGeneratorReturnType getReturnType() { return  EDhApiWorldGeneratorReturnType.API_DATA_SOURCES; }

    @Override
    public boolean runApiValidation() { return true; }
    @Override
    public CompletableFuture<Void> generateLod(
            int chunkPosMinX, int chunkPosMinZ,
            int posX, int posZ, byte detailLevel,
            IDhApiFullDataSource pooledFullDataSource,
            EDhApiDistantGeneratorMode generatorMode, ExecutorService worldGeneratorThreadPool,
            Consumer<IDhApiFullDataSource> resultConsumer)
    {
        return CompletableFuture.runAsync(() ->
                        this.generateInternal(
                                chunkPosMinX, chunkPosMinZ,
                                posX, posZ, detailLevel,
                                pooledFullDataSource, generatorMode, resultConsumer),
                worldGeneratorThreadPool);
    }

    public void generateInternal(
            int chunkPosMinX, int chunkPosMinZ,
            int posX, int posZ, byte detailLevel,
            IDhApiFullDataSource pooledFullDataSource,
            EDhApiDistantGeneratorMode generatorMode,
            Consumer<IDhApiFullDataSource> resultConsumer) {
        // get the blocks used for this position
        IDhApiBiomeWrapper biome;
        IDhApiBlockStateWrapper colorBlock;
        IDhApiBlockStateWrapper borderBlock;
        IDhApiBlockStateWrapper airBlock;
        int maxHeight;

        try
        {
            biome = DhApi.Delayed.wrapperFactory.getBiomeWrapper("minecraft:plains", this.levelWrapper);
            airBlock = DhApi.Delayed.wrapperFactory.getAirBlockStateWrapper();
            borderBlock = DhApi.Delayed.wrapperFactory.getDefaultBlockStateWrapper("minecraft:stone", this.levelWrapper);


            ArrayList<DhApiTerrainDataPoint> dataPoints = new ArrayList<>();
            int width = pooledFullDataSource.getWidthInDataColumns();

            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    IDhApiBlockStateWrapper block;


                    dataPoints.clear();

//                    dataPoints.add(DhApiTerrainDataPoint.create((byte)0, 0, 0, 0, maxHeight, block, biome));
//                    dataPoints.add(DhApiTerrainDataPoint.create((byte)0, 0, 0, maxHeight, ModWorldGenConstants.BUILD_HEIGHT, airBlock, biome));



                }
            }


        }
        catch (IOException e)
        {
            LOGGER.error("Failed to get biome/block: ["+ e.getMessage()+"].", e);
            return;
        }

    }
    @Override
    public void preGeneratorTaskStart() {

    }

    @Override
    public void close() {

    }

}
