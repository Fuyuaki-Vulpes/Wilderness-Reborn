package com.fuyuaki.r_wilderness.world.generation.hydrology.river.old_backup;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class WaterBasinGenerator {


    private static final int MAX_CACHE_SIZE = 600;
    private final Map<ChunkPos, List<RiverData>> riverCache = new ConcurrentHashMap<>();


    private static final int RIVER_SEARCH_RANGE = 1;
    private PositionalRandomFactory randomFactory;
    public volatile Map<WaterRegionPos, WaterRegion> map = new ConcurrentHashMap<>();


    public WaterBasinGenerator(PositionalRandomFactory randomFactory) {
        this.randomFactory = randomFactory;
    }


    public List<RiverData> riverBranchesAtAndCache(ChunkPos pos) {
        List<RiverData> riverData = riverBranchesAt(pos);
        safeInsertCache(pos, riverData);
        if (riverData == null){
            return new ArrayList<>();
        }
        return riverData;
    }
    public List<RiverData> riverBranchesAt(ChunkPos pos){
        if (riverCache.containsKey(pos)){
            return riverCache.get(pos);
        }
        int x = WaterRegionPos.sectionToWaterRegionCoord(pos.x);
        int z = WaterRegionPos.sectionToWaterRegionCoord(pos.z);
        Set<RiverData> list = new HashSet<>();
        for (int xO = -RIVER_SEARCH_RANGE; xO < RIVER_SEARCH_RANGE; xO ++){
            for (int zO = -RIVER_SEARCH_RANGE; zO < RIVER_SEARCH_RANGE; zO ++){
                WaterRegionPos wPos = new WaterRegionPos(x + xO, z + zO);
                if (this.map.containsKey(wPos)){
                    list.addAll(this.map.get(wPos).getRiverAtList(pos));
                }
            }
        }


        return list.stream().toList();
    }

    private void safeInsertCache(ChunkPos pos, List<RiverData> list) {
        if (this.riverCache.size() > MAX_CACHE_SIZE){
            this.riverCache.clear();
        }
        if (list == null || pos == null) return;
        this.riverCache.put(pos, list);
    }

    public List<RiverData> riverAllBranchesAt(ChunkPos pos){
        List<RiverData> list = new ArrayList<>();
        int x = WaterRegionPos.sectionToWaterRegionCoord(pos.x);
        int z = WaterRegionPos.sectionToWaterRegionCoord(pos.z);

        for (int xO = -RIVER_SEARCH_RANGE * 2; xO < RIVER_SEARCH_RANGE * 2; xO ++){
            for (int zO = -RIVER_SEARCH_RANGE * 2; zO < RIVER_SEARCH_RANGE * 2; zO ++){
                WaterRegionPos wPos = new WaterRegionPos(x + xO, z + zO);
                if (this.map.containsKey(wPos)){
                    list.addAll(this.map.get(wPos).getRiverAtListHighRange(pos));

                }
            }
        }
        return list;
    }

    public synchronized WaterBasinGenerator createAt(ChunkPos pos, TerrainParameters parameters) {
        int x = WaterRegionPos.sectionToWaterRegionCoord(pos.x);
        int z = WaterRegionPos.sectionToWaterRegionCoord(pos.z);
        for (int xO = -1; xO < 1; xO++) {
            for (int zO = -1; zO < 1; zO++) {
                WaterRegionPos waterPos = new WaterRegionPos(x + xO, z + zO);
                if (this.map.containsKey(waterPos)) return this;
                RWildernessMod.LOGGER.debug("Generating Water Region at Position {},{}", waterPos.x, waterPos.z);
                WaterRegion region = WaterRegion.generate(this.randomFactory, waterPos, parameters, this);
                WaterRegion region1 = this.map.putIfAbsent(waterPos, region);
                if (region1 == null) {
                    this.map.put(waterPos, region);
                    this.map.putIfAbsent(waterPos, region);
                }
            }
        }
        return this;
    }

    public void destroy(WaterRegionPos position, RiverData riverData) {
        WaterRegion region = this.map.get(position);
        if (region != null) {
            this.map.get(position).destroy(riverData);
        }
    }

    public boolean contains(ChunkPos pos) {
        return this.map.containsKey(new WaterRegionPos(pos));
    }


    public record WaterRegion(WaterRegionPos pos, List<RiverData> rivers, RandomSource source, TerrainParameters terrainParameters){

        private static final int MAX_REGION_RIVERS = 4;
        private static final int RIVER_CHUNK_RANGE = 8;

        public synchronized static WaterRegion generate(PositionalRandomFactory factory, WaterRegionPos pos, TerrainParameters parameters,WaterBasinGenerator basin){
            RandomSource random = factory.at(pos.x,0,pos.z);
            int i = random.nextIntBetweenInclusive(1, MAX_REGION_RIVERS);
            List<RiverData> rivers = new ArrayList<>();
            for (int r = 0; r < i; r++){
                RiverData riverData = new RiverData(random,pos,basin,parameters);
                if (!riverData.markedForRemoval()) {
                    rivers.add(riverData);
                }
            }
            int i1 = random.nextIntBetweenInclusive(2, MAX_REGION_RIVERS);

            if (rivers.isEmpty()){
                for (int r = 0; r < i1; r++){
                    RiverData riverData = new RiverData(random,pos,basin,parameters);
                    if (!riverData.markedForRemoval()) {
                        rivers.add(riverData);
                    }
                }
            }
            return new WaterRegion(pos,rivers,random,parameters);
        }

        public List<RiverData> getRiverAtList(ChunkPos chunkPos) {
            List<RiverData> riverDataList = new ArrayList<>();
            for (RiverData r : rivers){
                for (int x = 0; x < 4; x++){
                    for (int z = 0; z < 4; z++) {
                        if (r.insideRiver(chunkPos.getBlockX(x * 4),chunkPos.getBlockZ(z * 4))) {
                            riverDataList.add(r);
                        }
                    }
                }
            }
            return riverDataList;
        }
        public List<RiverData> getRiverAtListHighRange(ChunkPos chunkPos) {
            List<RiverData> riverDataList = new ArrayList<>();
            for (RiverData r : rivers) {
                for (int xC = -RIVER_CHUNK_RANGE; xC < RIVER_CHUNK_RANGE; xC++) {
                    for (int zC = -RIVER_CHUNK_RANGE; zC < RIVER_CHUNK_RANGE; zC++) {
                        for (int x = 0; x < 4; x++) {
                            for (int z = 0; z < 4; z++) {
                                if (r.insideRiver(chunkPos.getBlockX(x * 4) + (xC * 16), chunkPos.getBlockZ(z * 4) + (zC * 16))) {
                                    riverDataList.add(r);
                                }
                            }
                        }
                    }
                }
            }
                return riverDataList;
        }

        public void destroy(RiverData riverData) {
            this.rivers.remove(riverData);

        }
    }
    public static class WaterRegionPos{
        public static final Codec<WaterRegionPos> CODEC = Codec.INT_STREAM
                .<WaterRegionPos>comapFlatMap(
                        dataResult -> Util.fixedSize(dataResult, 2).map(ints -> new WaterRegionPos(ints[0], ints[1])),
                        pos -> IntStream.of(pos.x, pos.z)
                )
                .stable();
        public static final StreamCodec<ByteBuf, WaterRegionPos> STREAM_CODEC = new StreamCodec<ByteBuf, WaterRegionPos>() {
            public WaterRegionPos decode(ByteBuf buffer) {
                return WaterRegionPos.readChunkPos(buffer);
            }

            public void encode(ByteBuf byteBuf, WaterRegionPos pos) {
                WaterRegionPos.writeChunkPos(byteBuf, pos);
            }
        };

        public static final WaterRegionPos ZERO = new WaterRegionPos(0,0);

        public final int x;
        public final int z;

        public WaterRegionPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public WaterRegionPos(ChunkPos pos) {
            this.x = sectionToWaterRegionCoord(pos.x);
            this.z = sectionToWaterRegionCoord(pos.z);
        }

        public WaterRegionPos(BlockPos pos) {
            this.x = blockToWaterRegionCoord(pos.getX());
            this.z = blockToWaterRegionCoord(pos.getZ());
        }


        public WaterRegionPos(long packedPos) {
            this.x = (int)packedPos;
            this.z = (int)(packedPos >> 32);
        }

        @Override
        public int hashCode() {
            return hash(this.x, this.z);
        }

        public static int hash(int x, int z) {
            int i = 1664525 * x + 1013904223;
            int j = 1664525 * (z ^ -559038737) + 1013904223;
            return i ^ j;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else {
                return other instanceof WaterRegionPos waterRegionPos && this.x == waterRegionPos.x && this.z == waterRegionPos.z;
            }
        }

        public static WaterRegionPos readChunkPos(ByteBuf buffer) {
            return new WaterRegionPos(buffer.readLong());
        }

        public static void writeChunkPos(ByteBuf buffer, WaterRegionPos waterRegionPos) {
            buffer.writeLong(waterRegionPos.toLong());
        }


        public long toLong() {
            return asLong(this.x, this.z);
        }

        /**
         * Converts the chunk coordinate pair to a long
         */
        public static long asLong(int x, int z) {
            return x & 4294967295L | (z & 4294967295L) << 32;
        }

        public static long asLong(BlockPos pos) {
            return asLong(blockToWaterRegionCoord(pos.getX()), blockToWaterRegionCoord(pos.getZ()));
        }

        public static int blockFromWaterRegionCoord(int pos) {
            return pos << 13;
        }
        public static int blockToWaterRegionCoord(int pos) {
            return pos >> 13;
        }
        public static int sectionFromWaterRegionCoord(int pos) {
            return pos << 9;
        }
        public static int sectionToWaterRegionCoord(int pos) {
            return pos >> 9;
        }
        public static int riverSectionFromWaterRegionCoord(int pos) {
            return pos << 7;
        }
        public static int riverSectionToWaterRegionCoord(int pos) {
            return pos >> 7;
        }

        public int getRiverX() {
            return riverSectionFromWaterRegionCoord(x);
        }

        public int getRiverZ() {
            return riverSectionFromWaterRegionCoord(z);
        }

        public int getCenterX() {
            return blockFromWaterRegionCoord(x) + (blockFromWaterRegionCoord(1) / 2);
        }

        public int getCenterZ() {
            return blockFromWaterRegionCoord(z) + (blockFromWaterRegionCoord(1) / 2);
        }

        public static int getX(long waterAsLong) {
            return (int)(waterAsLong & 4294967295L);
        }

        public static int getZ(long waterAsLong) {
            return (int)(waterAsLong >>> 32 & 4294967295L);
        }

    }

    public record Coordinates(int x, int z){

        public static Coordinates fromBlock(int x, int z){
            return new Coordinates(QuartPos.fromBlock(x),QuartPos.fromBlock(z));
        }
        public int toBlockX(){
            return QuartPos.fromBlock(x);
        }
        public int toBlockZ(){
            return QuartPos.fromBlock(x);
        }
    }


}
