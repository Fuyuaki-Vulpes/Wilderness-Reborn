package com.fuyuaki.r_wilderness.world.generation.region;

import com.fuyuaki.r_wilderness.world.generation.GenerationUtils;
import com.fuyuaki.r_wilderness.world.generation.features.RegionFeatureGenerator;
import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RegionRivers;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RegionWaterBodies;
import com.fuyuaki.r_wilderness.world.generation.features.plates.RegionPlates;
import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.fuyuaki.r_wilderness.world.generation.noise.OpenSimplex2D;
import com.fuyuaki.r_wilderness.world.generation.region.data.*;
import com.fuyuaki.r_wilderness.world.generation.region.storage.RegionStorage;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegionManager {
    private final ExecutorService ioExecutor;
    public static final Logger LOGGER = LogUtils.getLogger();
    private final RegionStorage storage;
    private FileChannel activeChannel;
    private final long seed;
    private final RandomSource random;
    private final ServerLevel level;
    private final Path regionDir;

    private final List<Double> initializedTimes = new ArrayList<>();
    private final List<Double> rasterTimes = new ArrayList<>();
    private final List<Double> platTimes = new ArrayList<>();
    private final List<Double> heightTimes = new ArrayList<>();
    private final List<Double> riverTimes = new ArrayList<>();
    private final List<Double> postTimes = new ArrayList<>();
    private final List<Double> finalTimes = new ArrayList<>();

    private final ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> initializations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> rasterizations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> plateauTerrains = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> roughHeights = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> riverGeneration = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> riversPostGen = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> finalizations = new ConcurrentHashMap<>();
    private final RegionNoisesRecord regionNoises;

    public RegionManager(ServerLevel level) {
        int cpuThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 3);

        this.ioExecutor = Executors.newFixedThreadPool(cpuThreads,  Log4jThreadFactory.createDaemonThreadFactory("region-io-worker"));

        this.level = level;
        this.random = level.getRandom();
        this.seed = level.getSeed();
        Path rootPath = level.getServer().getWorldPath(LevelResource.ROOT);
        this.regionDir = rootPath.resolve("wilderness_regions");
        try {
            Files.createDirectories(this.regionDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create region directory at {}", this.regionDir, e);
        }
        this.storage = new RegionStorage(regionDir);
        this.regionNoises = RegionNoisesRecord.createFor(seed);
    }

    public void joinCompletedRegions(){

        AtomicBoolean updatedAny = new AtomicBoolean(false);

        if (!initializations.isEmpty()) {
            initializations.entrySet().removeIf(entry -> {
                CompletableFuture<WorldRegion> future = entry.getValue();
                if (!future.isDone()) {
                    return false;
                }
                try {
                    WorldRegion completedRegion = future.join();
                    if (completedRegion.getPhase().isAtLeast(RegionPhase.INITIALIZED.getNext())) {
                        return true;
                    }
                    long packedPos = entry.getKey();
                    RegionMemory.getInstance(level).putRegion(packedPos, completedRegion);
                    LOGGER.debug("Successfully Initialized and Merged region at {}", completedRegion.getPos());
                    this.save(completedRegion);
                    updatedAny.set(true);
                } catch (Exception e) {
                    LOGGER.error("Region Initialization failed for region at {}", RegionPos.unpack(entry.getKey()), e);
                }
                return true;
            });
        }

        if (!rasterizations.isEmpty()) {
            rasterizations.entrySet().removeIf(entry -> {
                CompletableFuture<WorldRegion> future = entry.getValue();
                if (!future.isDone()) {
                    return false;
                }
                try {
                    WorldRegion completedRegion = future.join();
                    if (completedRegion.getPhase().isAtLeast(RegionPhase.RASTERIZATION.getNext())) {
                        return true;
                    }
                    long packedPos = entry.getKey();
                    RegionMemory.getInstance(level).putRegion(packedPos, completedRegion);
                    LOGGER.debug("Successfully Rasterized and Merged Region at {}", completedRegion.getPos());
                    this.save(completedRegion);
                    updatedAny.set(true);
                } catch (Exception e) {
                    LOGGER.error("Region Rasterization failed for region at {}", RegionPos.unpack(entry.getKey()), e);
                }
                return true;
            });
        }
        if (!plateauTerrains.isEmpty()) {
            plateauTerrains.entrySet().removeIf(entry -> {
                CompletableFuture<WorldRegion> future = entry.getValue();
                if (!future.isDone()) {
                    return false;
                }
                try {
                    WorldRegion completedRegion = future.join();
                    if (completedRegion.getPhase().isAtLeast(RegionPhase.PLATEAU_AND_TERRAIN.getNext())) {
                        return true;
                    }
                    long packedPos = entry.getKey();
                    RegionMemory.getInstance(level).putRegion(packedPos, completedRegion);
                    LOGGER.debug("Created Plateaus and Terrain Variation and Merged Region at {}", completedRegion.getPos());
                    this.save(completedRegion);
                    updatedAny.set(true);
                } catch (Exception e) {
                    LOGGER.error("Plateau and Terrain Layer Generation failed for region at {}", RegionPos.unpack(entry.getKey()), e);
                }
                return true;
            });
        }
        if (!roughHeights.isEmpty()) {
            roughHeights.entrySet().removeIf(entry -> {
                CompletableFuture<WorldRegion> future = entry.getValue();
                if (!future.isDone()) {
                    return false;
                }
                try {
                    WorldRegion completedRegion = future.join();
                    if (completedRegion.getPhase().isAtLeast(RegionPhase.ROUGH_HEIGHTS.getNext())) {
                        return true;
                    }
                    long packedPos = entry.getKey();
                    RegionMemory.getInstance(level).putRegion(packedPos, completedRegion);
                    LOGGER.debug("Created Rough Heightmaps and Merged Region at {}", completedRegion.getPos());
                    this.save(completedRegion);
                    updatedAny.set(true);
                } catch (Exception e) {
                    LOGGER.error("Rough Heightmap Generation failed for region at {}", RegionPos.unpack(entry.getKey()), e);
                }
                return true;
            });
        }

        if (!riverGeneration.isEmpty()) {
            riverGeneration.entrySet().removeIf(entry -> {
                CompletableFuture<WorldRegion> future = entry.getValue();
                if (!future.isDone()) {
                    return false;
                }
                try {
                    WorldRegion completedRegion = future.join();
                    if (completedRegion.getPhase().isAtLeast(RegionPhase.RIVERS.getNext())) {
                        return true;
                    }
                    long packedPos = entry.getKey();
                    RegionMemory.getInstance(level).putRegion(packedPos, completedRegion);
                    LOGGER.debug("Successfully created Rivers and Merged region at {}", completedRegion.getPos());
                    this.save(completedRegion);
                    updatedAny.set(true);
                } catch (Exception e) {
                    LOGGER.error("Region River generation failed for region at {}", RegionPos.unpack(entry.getKey()), e);
                }
                return true;
            });
        }

        if (!riversPostGen.isEmpty()) {
            riversPostGen.entrySet().removeIf(entry -> {
                CompletableFuture<WorldRegion> future = entry.getValue();
                if (!future.isDone()) {
                    return false;
                }
                try {
                    WorldRegion completedRegion = future.join();
                    if (completedRegion.getPhase().isAtLeast(RegionPhase.RIVERS_POST.getNext())) {
                        return true;
                    }
                    long packedPos = entry.getKey();
                    RegionMemory.getInstance(level).putRegion(packedPos, completedRegion);
                    LOGGER.debug("Successfully Post Processed Rivers and Merged region at {}", completedRegion.getPos());
                    this.save(completedRegion);
                    updatedAny.set(true);
                } catch (Exception e) {
                    LOGGER.error("Region River Post Processing failed for region at {}", RegionPos.unpack(entry.getKey()), e);
                }
                return true;
            });
        }

        if (!finalizations.isEmpty()) {
            finalizations.entrySet().removeIf(entry -> {
                CompletableFuture<WorldRegion> future = entry.getValue();
                if (!future.isDone()) {
                    return false;
                }
                try {
                    WorldRegion completedRegion = future.join();
                    if (completedRegion.queryCount() <= 0) return true;
                    long packedPos = entry.getKey();
                    RegionMemory.getInstance(level).putRegion(packedPos, completedRegion);
                    LOGGER.info("Successfully Generated and Merged region at {}", completedRegion.getPos());
                    this.save(completedRegion);
                    updatedAny.set(true);
                } catch (Exception e) {
                    LOGGER.error("Region Finalization failed for region at {}", RegionPos.unpack(entry.getKey()), e);
                }
                return true;
            });
        }

        if (this.finalTimes.size() >= 4 && updatedAny.get()) {
            this.computeAverageTimeOfPhases();
        }
    }

    public RegionNoisesRecord getRegionNoises() {
        return regionNoises;
    }


    protected RegionMemory getRegionMemory() {
        return RegionMemory.getInstance(level);
    }

    @SuppressWarnings("DataFlowIssue")
    protected CompletableFuture<WorldRegion> generateRegionPhaseAsync(RegionPos pos, RegionPhase phase) {
        if (phase == RegionPhase.EMPTY) {
            LOGGER.info("Attempting to load existing Region at {}", pos);
            return CompletableFuture.supplyAsync(() -> {
                WorldRegion r = this.getRegionMemory().getRegionOfIndexWithoutCreation(pos.pack());
                if (r == null) {
                    r = loadOrCreateBase(pos);
                }
                return r;
            },Util.backgroundExecutor());
        }
        ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> phaseMap = this.getMapForPhase(phase);

        return phaseMap.computeIfAbsent(pos.pack(), p -> generateRegionPhaseAsync(pos, phase.getPrevious()).thenComposeAsync(prevRegion ->{
            if (prevRegion != null && prevRegion.getPhase().isAtLeast(phase)){

                return CompletableFuture.completedFuture(prevRegion);
            }

            prevRegion.query();
            CompletableFuture<WorldRegion> phaseWork = switch (phase){
                case INITIALIZED -> CompletableFuture.supplyAsync(() -> initialize(prevRegion),Util.backgroundExecutor());

                case RASTERIZATION -> fetchNeighborsAsync(pos,1,RegionPhase.INITIALIZED)
                        .thenApplyAsync( neighbors ->{
                            WorldRegion center = neighbors.get(pos);
                            if (center.getPhase().isAtLeast(RegionPhase.RASTERIZATION)) return center;
                            return rasterize(center,neighbors);
                        },Util.backgroundExecutor());

                case PLATEAU_AND_TERRAIN -> CompletableFuture.supplyAsync(() -> makePlateauAndTerrain(prevRegion),Util.backgroundExecutor());

                case ROUGH_HEIGHTS -> fetchNeighborsAsync(pos,1,RegionPhase.PLATEAU_AND_TERRAIN).thenApplyAsync(
                        neighbors -> {
                            WorldRegion center = neighbors.get(pos);
                            if (center.getPhase().isAtLeast(RegionPhase.ROUGH_HEIGHTS)) return center;
                            return generateHeightmap(prevRegion, neighbors);
                        },Util.backgroundExecutor());

                case RIVERS -> fetchNeighborsAsync(pos, 1, RegionPhase.ROUGH_HEIGHTS)
                        .thenApplyAsync(neighbors -> {
                            WorldRegion center = neighbors.get(pos);
                            if (center.getPhase().isAtLeast(RegionPhase.RIVERS)) return center;
                            return generateRivers(prevRegion, neighbors);
                        },Util.backgroundExecutor());
                case RIVERS_POST -> fetchNeighborsAsync(pos, 1, RegionPhase.RIVERS)
                        .thenApplyAsync(neighbors -> {
                            WorldRegion center = neighbors.get(pos);
                            if (center.getPhase().isAtLeast(RegionPhase.RIVERS_POST)) return center;
                            return postProcessRivers(prevRegion, neighbors);
                        },Util.backgroundExecutor());

                case LOADED_AND_FINISHED -> fetchNeighborsAsync(pos, 1, RegionPhase.RIVERS_POST)
                        .thenApplyAsync(neighbors -> {
                            WorldRegion center = neighbors.get(pos);
                            if (center.getPhase().isAtLeast(RegionPhase.LOADED_AND_FINISHED)) return center;
                            return finalizeRegion(prevRegion,neighbors);
                        },Util.backgroundExecutor());
                default -> CompletableFuture.completedFuture(prevRegion);
            };
            return phaseWork.thenApply(region -> {
                region.updateStatus(phase);
                region.release();
                return region;
            });
        }));
    }



    public CompletableFuture<Map<RegionPos, WorldRegion>> fetchNeighborsAsync(RegionPos center, int radius, RegionPhase requiredPhase) {
        List<CompletableFuture<WorldRegion>> futures = new ArrayList<>();
        List<RegionPos> positions = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                RegionPos nPos = new RegionPos(center.x() + x, center.z() + z);
                positions.add(nPos);
                futures.add(generateRegionPhaseAsync(nPos, requiredPhase));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    Map<RegionPos, WorldRegion> map = new HashMap<>();
                    for (int i = 0; i < positions.size(); i++) {
                        map.put(positions.get(i), futures.get(i).join());
                    }
                    return map;
                });
    }

    private WorldRegion loadOrCreateBase(RegionPos pos) {
        Path regionFile = getRegionFilePath(pos);
        if (Files.exists(regionFile)) {
            try {
                if (Files.size(regionFile) > 0) {
                    WorldRegion region = storage.read(this.regionNoises, pos,this);
                    region.setSaved();
                    return region;
                } else {
                    LOGGER.warn("Region file {} is 0 bytes, deleting and re-initializing", pos);
                    Files.deleteIfExists(regionFile);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load region {}, re-initializing", pos, e);
                try {
                    Files.deleteIfExists(regionFile);
                } catch (IOException ignored) {}
            }
        }

        WorldRegion region = new WorldRegion(this.getRegionNoises(),pos,this.seed);
        region.setSaved();
        return region;
    }

    private WorldRegion initialize(WorldRegion worldRegion) {

        long startTime = System.nanoTime();
        RegionPos pos =  worldRegion.getPos();
        SerializableRegionData data = new SerializableRegionData(worldRegion.getPos(), new RegionPlates(pos,this.seed,this.regionNoises), new RegionRivers(regionNoises), new RegionWaterBodies(regionNoises));

        worldRegion.layers().setAtmospherePressure(this.regionNoises.atmospherePressure().generate(worldRegion.layers().getAtmospherePressure(),worldRegion.getPos()));
        worldRegion.layers().setTemperature(this.regionNoises.temperature().generate(worldRegion.layers().getTemperature(),worldRegion.getPos()));
        worldRegion.layers().setVegetation(this.regionNoises.vegetation().generate(worldRegion.layers().getVegetation(),worldRegion.getPos()));
        worldRegion.layers().setHumidity(this.regionNoises.humidity().generate(worldRegion.layers().getHumidity(),worldRegion.getPos()));
        worldRegion.layers().setRockiness(this.regionNoises.rockiness().generate(worldRegion.layers().getRockiness(),worldRegion.getPos()));
        worldRegion.layers().setMagicalness(this.regionNoises.magicalness().generate(worldRegion.layers().getMagicalness(),worldRegion.getPos()));

        worldRegion.setNbtData(data);
        worldRegion.updateStatus(RegionPhase.INITIALIZED);
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        LOGGER.debug("Initialization for region at {} complete with time: {} ms", pos, durationMs);
        this.addToInit(durationMs);
        return worldRegion;
    }

    private WorldRegion rasterize(WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors) {

        long startTime = System.nanoTime();
        RegionPos pos = worldRegion.getPos();
        worldRegion.layers().setErodability(this.regionNoises.erodability().generate(worldRegion.layers().getErodability(),worldRegion.getPos()));
        RegionFloatMap[] maps = RegionFeatureGenerator.createRasterizedPlateLayers(pos, neighbors,worldRegion);
        worldRegion.layers().setPlateWeights(maps[GenerationUtils.TectonicArrayIndex.WEIGHT]);
        worldRegion.layers().setPlateEdges(maps[GenerationUtils.TectonicArrayIndex.EDGES]);
        worldRegion.layers().setPlateElevationOffsets(maps[GenerationUtils.TectonicArrayIndex.BASE_ELEVATION_OFFSET]);
        worldRegion.layers().setVolcanicness(maps[GenerationUtils.TectonicArrayIndex.VOLCANICNESS]);
        worldRegion.layers().setTectonicStress(maps[GenerationUtils.TectonicArrayIndex.TECTONIC_STRESS]);
        worldRegion.layers().setBuoyancy(maps[GenerationUtils.TectonicArrayIndex.BUOYANCY]);
        worldRegion.layers().setDistToParent(maps[GenerationUtils.TectonicArrayIndex.DISTANCE_PARENT]);
        worldRegion.layers().setAge(maps[GenerationUtils.TectonicArrayIndex.AGE]);
        worldRegion.layers().setSwelling(maps[GenerationUtils.TectonicArrayIndex.SWELLING]);
        worldRegion.updateStatus(RegionPhase.RASTERIZATION);
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        LOGGER.debug("Rasterized region at {} in {} ms", pos, durationMs);
        this.addToRasters(durationMs);
        return worldRegion;
    }


    private WorldRegion makePlateauAndTerrain(WorldRegion worldRegion) {
        if (worldRegion.getPhase().isAtLeast(RegionPhase.PLATEAU_AND_TERRAIN)) return worldRegion;
        long startTime = System.nanoTime();
        RegionPos pos = worldRegion.getPos();


        RegionFloatMap plateau = RegionFeatureGenerator.plateauMap(pos,level, worldRegion);
        worldRegion.layers().setPlateaus(plateau);
        RegionFloatMap hills = RegionFeatureGenerator.createHills(pos,level, worldRegion);
        worldRegion.layers().setHilliness(hills);

        worldRegion.updateStatus(RegionPhase.PLATEAU_AND_TERRAIN);
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        LOGGER.debug("Generated Plateau and Terrain Data for region at {}. Completed in {} ms", pos, durationMs);
        this.addToPlat(durationMs);
        return worldRegion;
    }

    private WorldRegion generateHeightmap(WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors) {
        long startTime = System.nanoTime();
        RegionPos pos = worldRegion.getPos();

        RegionFeatureGenerator.createHeightmapsAndPrepareForRiver(pos,level,worldRegion,neighbors);
        worldRegion.getNbtData().rivers().generateRiverNodes(worldRegion.getPos(),seed,worldRegion);

        worldRegion.updateStatus(RegionPhase.ROUGH_HEIGHTS);
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        LOGGER.debug("Heightmaps for region at {} took {} ms to complete", pos, durationMs);
        this.addToHeight(durationMs);
        return worldRegion;
    }


    private WorldRegion generateRivers(WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors) {
        long startTime = System.nanoTime();
        RegionPos pos = worldRegion.getPos();

        worldRegion.getNbtData().rivers().computeEntireRivers(pos, neighbors, worldRegion);

        worldRegion.getNbtData().rivers().postProcessRiverCliffsAndFlow(pos, neighbors,worldRegion,this.seed);
        worldRegion.updateStatus(RegionPhase.RIVERS);
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        LOGGER.debug("Rivers for region at {} took {} ms to complete", pos, durationMs);
        this.addToRiver(durationMs);
        return worldRegion;
    }

    private WorldRegion postProcessRivers(WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors) {
        long startTime = System.nanoTime();
        RegionPos pos = worldRegion.getPos();

        worldRegion.getNbtData().rivers().computeEntireRivers(pos, neighbors, worldRegion);
        worldRegion.getNbtData().rivers().postProcessRiverCliffsAndFlow(pos, neighbors,worldRegion,this.seed);
        worldRegion.getNbtData().rivers().generatePointsForAllRivers(pos,worldRegion, neighbors);

        worldRegion.updateStatus(RegionPhase.RIVERS_POST);

        long endTime0 = System.nanoTime();
        double durationMs0 = (endTime0 - startTime) / 1_000_000.0;
        LOGGER.debug("Ran Processing for region at {}, time taken: {} ms", worldRegion.getPos(), durationMs0);
        this.addToPost(durationMs0);


        return worldRegion;
    }

    private WorldRegion finalizeRegion(WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors) {
        long startTime = System.nanoTime();
        RegionPos pos = worldRegion.getPos();

        RegionFeatureGenerator.createRiverShapeAndHeightmaps(pos,level,worldRegion,neighbors);

        worldRegion.updateStatus(RegionPhase.LOADED_AND_FINISHED);
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        LOGGER.debug("Finalized region at {} within {} ms", worldRegion.getPos(), durationMs);
        this.addToFinal(durationMs);
        return worldRegion;
    }

    protected WorldRegion getOrCreateCompleteWorldRegion(RegionPos pos) {
        WorldRegion region = getOrCreateWorldRegionOfPhase(pos,RegionPhase.LOADED_AND_FINISHED);
        if (region.getNbtData().status().isAtLeast(RegionPhase.RIVERS_POST) && !region.getNbtData().rivers().regionHasGeneratedPoints()){
            Map<RegionPos, WorldRegion> neighbors = fetchNeighborsAsync(pos,1,RegionPhase.RIVERS).join();
            region.getNbtData().rivers().generatePointsForAllRivers(pos,region,neighbors);
        }
        region.setReadyToLoad();
        return region;
    }


    protected WorldRegion getOrCreateWorldRegionOfPhase(RegionPos pos,RegionPhase phase) {
        return generateRegionPhaseAsync(pos,phase).join();

    }

    private ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> getMapForPhase(RegionPhase phase) {
        return switch (phase) {
            case EMPTY -> null;
            case INITIALIZED -> this.initializations;
            case RASTERIZATION -> this.rasterizations;
            case PLATEAU_AND_TERRAIN -> this.plateauTerrains;
            case ROUGH_HEIGHTS -> this.roughHeights;
            case RIVERS -> this.riverGeneration;
            case RIVERS_POST -> this.riversPostGen;
            case LOADED_AND_FINISHED -> this.finalizations;
        };
    }

    protected void save(WorldRegion region) {
        if (region.hasBeenSaved()) return;
        RegionPos pos = region.getPos();
        LOGGER.debug("Saving region at {}", pos);
        region.query();
        CompletableFuture.runAsync(() -> {
            try {
                this.storage.write(region);
                LOGGER.debug("Successfully saved region at {}", pos);

            } catch (Throwable e) {
                LOGGER.error("Failed to save region {}", pos, e);
            }
        }, ioExecutor);

        region.setSaved();
        region.release();
    }

    private Path getRegionFilePath(RegionPos pos) {
        String fileName = "r." + pos.x() + "." + pos.z() + ".rwreg";
        return this.regionDir.resolve(fileName);
    }

    private synchronized void addToInit(double time){
        this.initializedTimes.add(time);
    }
    private synchronized void addToRasters(double time){
        this.rasterTimes.add(time);
    }
    private synchronized void addToPlat(double time){
        this.platTimes.add(time);
    }
    private synchronized void addToHeight(double time){
        this.heightTimes.add(time);
    }
    private synchronized void addToRiver(double time){
        this.riverTimes.add(time);
    }
    private synchronized void addToPost(double time){
        this.postTimes.add(time);
    }
    private synchronized void addToFinal(double time){
        this.finalTimes.add(time);
    }

    public void computeAverageTimeOfPhases(){
        LOGGER.debug("Average duration for INITIALIZATION was {} ms", calculateAverage(this.initializedTimes));
        LOGGER.debug("Average duration for RASTER was {} ms", calculateAverage(this.rasterTimes));
        LOGGER.debug("Average duration for PLATEAU was {} ms", calculateAverage(this.platTimes));
        LOGGER.debug("Average duration for ROUGH HEIGHTS was {} ms", calculateAverage(this.heightTimes));
        LOGGER.debug("Average duration for RIVER was {} ms", calculateAverage(this.riverTimes));
        LOGGER.debug("Average duration for RIVER POST was {} ms", calculateAverage(this.postTimes));
        LOGGER.debug("Average duration for FINALIZATION was {} ms", calculateAverage(this.finalTimes));
    }

    public static double calculateAverage(List<Double> list) {
        if (list == null || list.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            sum += list.get(i);
        }
        return sum / size;
    }

    public void close() {
        try {
            this.joinCompletedRegions();
            if (activeChannel != null && activeChannel.isOpen()) {
                activeChannel.close();
            }
            LOGGER.info("RegionManager closed successfully.");
        } catch (IOException e) {
            LOGGER.error("Error closing RegionManager", e);
        }
    }


    private static Noise2D baseNoise(long seed, float wavelength, float amplitude){
        return new OpenSimplex2D(seed)
                .spread(1/wavelength)
                .scaled(-amplitude,amplitude);
    }

    private Noise2D noise(long hash, float wavelength, float amplitude){
        return baseNoise(this.seed ^ hash, wavelength, amplitude);
    }
    private Noise2D noise(String hash, float wavelength, float amplitude){
        long hashedString = 7L;
        int length = hash.length();
        for (int i = 0; i < length; i++) {
            hashedString = 31L * hashedString + hash.charAt(i);
        }

        return baseNoise(this.seed ^ hashedString, wavelength, amplitude);
    }
}


