package com.fuyuaki.r_wilderness.world.generation.region;

import com.fuyuaki.r_wilderness.world.environment.EnvironmentSampler;
import com.fuyuaki.r_wilderness.world.generation.biome.RebornBiomeSource;
import com.fuyuaki.r_wilderness.world.generation.features.plates.RegionPlates;
import com.fuyuaki.r_wilderness.world.generation.features.plates.TectonicPlateNode;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPhase;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayerTypes;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2i;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RegionMemory implements AutoCloseable{
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int LOAD_RANGE = 1;
    private static final double MIN_DIST_TO_REMOVE = 4096;

    private RegionManager regionManager = null;
    private RandomSource random;
    private long seed;

    private final ConcurrentHashMap<Long, WorldRegion> activeRegions =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<WorldRegion>> queriedLoads =
            new ConcurrentHashMap<>();

    private CompletableFuture<Void> initializationFutures;

    public static RegionMemory getInstance(ServerLevel level) {
        return ((ServerLevelRegionMemoryFetcher)level).getRegionMemory() ;
    }

    public ConcurrentHashMap<Long, WorldRegion> getActiveRegions() {
        return activeRegions;
    }

    public WorldRegion requestImmediately(BlockPos middleBlockPosition){
        WorldRegion regionAt = getRegionAt(middleBlockPosition);
        if (regionAt.getPhase().isAtLeast(RegionPhase.LOADED_AND_FINISHED)){
            regionAt.setReadyToLoad();
        }
        put(regionAt);
        return regionAt;
    }
    public WorldRegion requestImmediately(int x, int z){
        WorldRegion regionAt = getRegionAt(x,z);
        if (regionAt.getPhase().isAtLeast(RegionPhase.LOADED_AND_FINISHED)){
            regionAt.setReadyToLoad();
        }
        put(regionAt);
        return regionAt;
    }


    protected void put(WorldRegion region){
        this.activeRegions.putIfAbsent(region.getPos().pack(),region);
    }

    protected void putRegion(long packedPos, WorldRegion region) {
        this.activeRegions.put(packedPos, region);
    }

    public List<WorldRegion> activeRegionList(){
        return new ArrayList<>(activeRegions.values());
    }

    public WorldRegion getRegionOfIndex(long packedPos) {
        WorldRegion region = getRegionOfIndex(packedPos,RegionPhase.LOADED_AND_FINISHED);
        if (region.getNbtData().status().isAtLeast(RegionPhase.RIVERS_POST) && !region.getNbtData().rivers().regionHasGeneratedPoints()){
            RegionPos pos = RegionPos.unpack(packedPos);
            if (region.getNbtData().status().isAtLeast(RegionPhase.RIVERS_POST) && !region.getNbtData().rivers().regionHasGeneratedPoints()){
                Map<RegionPos, WorldRegion> neighbors = this.regionManager.fetchNeighborsAsync(pos,1,RegionPhase.RIVERS).join();
                region.getNbtData().rivers().generatePointsForAllRivers(pos,region,neighbors);
            }
        }
        region.setReadyToLoad();

        return region;
    }



    public WorldRegion getRegionOfIndex(long packedPos, RegionPhase phase) {
        WorldRegion region = activeRegions.get(packedPos);
        if (region == null || !region.getPhase().isAtLeast(phase)) {
            region = this.regionManager.getOrCreateWorldRegionOfPhase(RegionPos.unpack(packedPos),phase);
        }
        this.activeRegions.put(packedPos, region);
        return region;
    }
    public WorldRegion getRegionOfIndexWithoutCreation(long packedPos) {
        return activeRegions.getOrDefault(packedPos,null);
    }

    public WorldRegion getRegionOfIndex(int x, int z) {
        return this.getRegionOfIndex(RegionPos.pack(x,z));
    }

    public WorldRegion getRegionAt(Vec3i pos) {
        return this.getRegionOfIndex(
                RegionPos.pack(
                        RegionPos.fromBlockCoord(pos.getX()),
                        RegionPos.fromBlockCoord(pos.getZ())
                )
        );
    }

    public static WorldRegion getRegionAt(int x, int z, Map<RegionPos, WorldRegion> neighbors) {
        RegionPos pos = new RegionPos(
                RegionPos.fromBlockCoord(x),
                RegionPos.fromBlockCoord(z)
        );
        return neighbors.get(pos);

    }


    public WorldRegion getRegionAt(RegionPos pos) {
      return this.getRegionOfIndex(pos.pack());
    }



    public WorldRegion getRegionAt(int x, int z) {
        return this.getRegionOfIndex(
                RegionPos.pack(RegionPos.fromBlockCoord(x),
                        RegionPos.fromBlockCoord(z))
        );
    }
    public WorldRegion getRegionAt(int x, int z, RegionPhase phase) {
        return this.getRegionOfIndex(
                RegionPos.pack(RegionPos.fromBlockCoord(x),
                        RegionPos.fromBlockCoord(z)),
                phase
        );
    }

    public TectonicPlateNode nearestPlate(int xPos, int zPos) {
        TectonicPlateNode nearest = null;
        float nearestDist = Float.MAX_VALUE;
        int scale = 256;
        int scanZone = 1;
        for (int xO = -scanZone;  xO <= scanZone; xO++) {
            for (int zO = -scanZone;  zO <= scanZone; zO++) {
                int x = xPos + (xO * scale);
                int z = zPos + (zO * scale);
                WorldRegion regionAt = this.getRegionAt(x, z);
                RegionPlates plates = regionAt.getNbtData().plates();
                TectonicPlateNode nearbyNode = plates.closestNode(x,z);

                if (nearbyNode != null) {
                    float distance = nearbyNode.getWeightedDistance(xPos, zPos);
                    if (distance < nearestDist) {
                        nearest = nearbyNode;
                        nearestDist = distance;
                    }

                }else{
                    System.out.println("node was null!");
                }
            }
        }
        return nearest;
    }

    public void refresh(ServerLevel level) {
        this.regionManager.joinCompletedRegions();

        Set<RegionPos> prefetchRegions = new HashSet<>();
        Set<RegionPos> forcedRegions = new HashSet<>();

       Set<RegionPos> nearbyRegions = new HashSet<>();
       List<ServerPlayer> players = level.players();
       for (ServerPlayer player : players) {
           Vec3i pos = player.blockPosition();
           int scanRange = 2048;
           for (int xO = -1; xO <= 1; xO++) {
               for (int zO = -1; zO <= 1; zO++) {
                   int xOffset = xO * scanRange;
                   int zOffset = zO * scanRange;
                   Vec3i posOff = pos.offset(xOffset,0,zOffset);
                   RegionPos nearbyRegionPos = new RegionPos(posOff);
                   nearbyRegions.add(nearbyRegionPos);

                   prefetchRegions.add(nearbyRegionPos);

                   if (isWithinDistance(pos, nearbyRegionPos, 1024)) {
                       forcedRegions.add(nearbyRegionPos);
                   }
                   // If player is within 1024 blocks of the region, mark as forced

               }
           }
       }
        for (RegionPos regionPos : prefetchRegions) {
            long packed = regionPos.pack();

            if (!this.activeRegions.containsKey(packed)) {

                this.queriedLoads.computeIfAbsent(packed, key ->
                        CompletableFuture.supplyAsync(() -> loadOrCreate(regionPos), Util.backgroundExecutor()))
                        .thenAcceptAsync(region -> {
                                    this.activeRegions.computeIfAbsent(packed,k -> region);
                                    this.queriedLoads.remove(packed);
                        },level.getServer());
            }
        }

        for (RegionPos regionPos : forcedRegions) {
            long packed = regionPos.pack();

            if (!this.activeRegions.containsKey(packed)) {
                CompletableFuture<WorldRegion> future = this.queriedLoads.get(packed);

                if (future != null) {
                    WorldRegion region = future.join();
                    this.activeRegions.put(packed, region);
                    this.queriedLoads.remove(packed);
                } else {
                    WorldRegion region = loadOrCreate(regionPos);
                    this.activeRegions.put(packed, region);
                }
            }
        }


        this.activeRegions.forEach((key,region) -> {
                    if (!region.hasBeenSaved()){
                        this.regionManager.save(region);
                        region.setSaved();
                    }
        }
        );
       for (WorldRegion worldRegion : this.activeRegions.values()) {
           if (!nearbyRegions.contains(worldRegion.getPos()) && worldRegion.queryCount() <= 0 && this.shouldRemove(worldRegion,players) && worldRegion.hasBeenSaved()) {
                   long packed = worldRegion.getPos().pack();
                   this.activeRegions.remove(packed);
                   this.queriedLoads.remove(packed);
               LOGGER.info("Unloaded region {} from memory", worldRegion);

           }
       }

        BiomeSource source = level.getChunkSource().getGenerator().getBiomeSource();
        if (source instanceof  RebornBiomeSource) {
            ((RebornBiomeSource)level.getChunkSource().getGenerator().getBiomeSource()).setMemory(this);
        }

    }

    private boolean shouldRemove(WorldRegion worldRegion, List<ServerPlayer> players) {
        float minDistance = Float.MAX_VALUE;
        for (LivingEntity player : players) {
            minDistance = Math.min(worldRegion.getDistanceTo(player),minDistance);
        }
        return minDistance >= MIN_DIST_TO_REMOVE;
    }

    private boolean isWithinDistance(Vec3i playerPos, RegionPos regionPos, int threshold) {
        int regionMinX = regionPos.getMinBlockX();
        int regionMaxX = regionPos.getMaxBlockX();

        int regionMinZ = regionPos.getMinBlockZ();
        int regionMaxZ = regionPos.getMaxBlockZ();

        int dx;
        if (playerPos.getX() < regionMinX) {
            dx = regionMinX - playerPos.getX();
        } else if (playerPos.getX() > regionMaxX) {
            dx = playerPos.getX() - regionMaxX;
        } else {
            int localX = playerPos.getX() - regionMinX;
            dx = Math.min(localX, RegionPos.REGION_SIZE - localX);
        }

        int dz;
        if (playerPos.getZ() < regionMinZ) {
            dz = regionMinZ - playerPos.getZ();
        } else if (playerPos.getZ() > regionMaxZ) {
            dz = playerPos.getZ() - regionMaxZ;
        } else {
            int localZ = playerPos.getZ() - regionMinZ;
            dz = Math.min(localZ, RegionPos.REGION_SIZE - localZ);
        }

        return dx <= threshold && dz <= threshold;

    }

    private WorldRegion loadOrCreate(RegionPos regionPos) {
        return this.regionManager.getOrCreateCompleteWorldRegion(regionPos);
    }

    public void initialize(ServerLevel level) {
        LOGGER.info("Initializing RegionMemory for level {}", level);
        this.random = level.getRandom();
        this.seed = level.getSeed();
        this.regionManager = new RegionManager(level);
        List<CompletableFuture<WorldRegion>> futures = new ArrayList<>();

        for (int x = -LOAD_RANGE; x <= LOAD_RANGE - 1; x++) {
            for (int z = -LOAD_RANGE; z <= LOAD_RANGE - 1; z++) {
                RegionPos regionPos = new RegionPos(x, z);
                futures.add(CompletableFuture.supplyAsync(() -> loadOrCreate(regionPos)));
            }
        }

        this.initializationFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    }

    public void shutdown() {
        LOGGER.info("Shutting down RegionMemory ({} active regions)", activeRegions.size());
        for (WorldRegion worldRegion : activeRegions.values()) {
            this.regionManager.save(worldRegion);
        }
        this.initializationFutures = null;
        activeRegions.clear();
        this.regionManager.close();
        this.regionManager = null;
    }

    @Override
    public void close() {
        this.shutdown();
    }

    public void updateManager() {
        this.regionManager.joinCompletedRegions();
    }

    public Pair<Vector2i, Float> spiralLocate(RegionLayerTypes layer, float targetValue, int searchRange, Vec2 position) {


        int scale = 4;
        int sampleRadius = Math.floorDiv(searchRange, scale);
        Vector2i pos = new Vector2i(
                Math.floorDiv(Mth.floor(position.x), scale),
                Math.floorDiv(Mth.floor(position.y), scale));
        Vector2i bestPos = new Vector2i(
                Math.floorDiv(Mth.floor(position.x), scale),
                Math.floorDiv(Mth.floor(position.y), scale));
        float bestValue = Float.MAX_VALUE;
        float lastDifference = 0.5f;
        double lastDistance = Double.MAX_VALUE;


        for (int xO = -sampleRadius; xO < sampleRadius; xO++) {
            for (int zO = -sampleRadius; zO < sampleRadius; zO++) {
                int blockX = pos.x + xO * scale;
                int blockZ = pos.y + zO * scale;
                float v = this.getRegionAt(blockX,blockZ).layers().getOfIndex(layer.getIndex()).getUpsampled(blockX, blockZ,this);
                float difference = targetValue - v;
                Vector2i p = new Vector2i(blockX, blockZ);
                double distance = p.distance(pos.x(),pos.y());
                if (difference < lastDifference - 0.05 && distance < lastDistance) {
                    bestValue = v;
                    lastDistance = distance;
                    lastDifference = difference;
                    bestPos = p;
                }
            }



        }

        return Pair.of(bestPos,bestValue);

    }

    public Map<RegionPos, WorldRegion> fetchRegionsAround(BlockPos middleBlockPosition, int range) {
        return null;
    }

    public EnvironmentSampler environment(double x, double z){
        int xPos = Mth.floor(x);
        int zPos = Mth.floor(z);
        WorldRegion regionAt = this.getRegionAt(xPos, zPos);

        return new EnvironmentSampler(
                regionAt.layers().getVegetation().getUpsampled(xPos,zPos,this),
                regionAt.layers().getHumidity().getUpsampled(xPos,zPos,this),
                regionAt.layers().getTemperature().getUpsampled(xPos,zPos,this),
                regionAt.layers().getContinentalness().getUpsampled(xPos,zPos,this)
        );
    }

    public boolean readyForChunkReading(BlockPos middleBlockPosition) {
        WorldRegion regionAt = getRegionAt(middleBlockPosition);
        return regionAt != null && regionAt.getPhase().isAtLeast(RegionPhase.LOADED_AND_FINISHED) && regionAt.isReadyToLoad();
    }
    public boolean readyForChunkReading(int x, int z) {
        WorldRegion regionAt = getRegionAt(x,z);
        return regionAt != null && regionAt.getPhase().isAtLeast(RegionPhase.LOADED_AND_FINISHED) && regionAt.isReadyToLoad();
    }
}
