package com.fuyuaki.r_wilderness.world.generation.region.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

import java.util.stream.IntStream;

public record RegionPos(int x, int z) {
    public static final Codec<RegionPos> CODEC = Codec.INT_STREAM
            .<RegionPos>comapFlatMap(input -> Util.fixedSize(input, 2).map(ints -> new RegionPos(ints[0], ints[1])), pos -> IntStream.of(pos.x, pos.z))
            .stable();
    public static final StreamCodec<ByteBuf, RegionPos> STREAM_CODEC = new StreamCodec<ByteBuf, RegionPos>() {
        public RegionPos decode(ByteBuf input) {
            return readRegionPos(input);
        }

        public void encode(ByteBuf output, RegionPos value) {
            writeRegionPos(output, value);
        }
    };
    public static final int REGION_SIZE = 16384;
    public static final int REGION_BITS = Integer.numberOfTrailingZeros(REGION_SIZE);

    public static final int BLOCK_TO_REGION_MASK = REGION_SIZE - 1;
    public static final int CHUNKS_PER_REGION_SIDE = REGION_SIZE >> 4;
    public static final int CHUNK_TO_REGION_MASK = CHUNKS_PER_REGION_SIDE - 1;;
    public static final int CHUNK_SHIFT = Integer.numberOfTrailingZeros(CHUNKS_PER_REGION_SIDE);


    public static final int OVERLAY_SIZE = 256;
    public static final int RESOLUTION_SHIFT = 2;
    public static final int RESOLUTION_SHIFT_LARGE = 4;
    public static final RegionPos ZERO = new RegionPos(0,0);

    public RegionPos(Vec3i position) {
        this(fromBlockCoord(position.getX()), fromBlockCoord(position.getZ()));
    }

    public static RegionPos unpack(long key) {
        return new RegionPos((int)key, (int)(key >> 32));
    }

    public long pack() {
        return pack(this.x, this.z);    }

    public static long pack(int x, int z) {
        return x & 4294967295L | (z & 4294967295L) << 32;
    }

    @Override
    public @NonNull String toString() {
        return "RegionPos [X:" + this.x + ", Z:" + this.z + "]";
    }
    public static int toBlockCoord(int regionCoord) {
        return regionCoord << REGION_BITS;
    }

    public static int fromBlockCoord(int blockCoord) {
        return blockCoord >> REGION_BITS;
    }

    public static RegionPos fromBlockCoord(int x,int z) {
        return new RegionPos(fromBlockCoord(x),fromBlockCoord(z));
    }

    public static int chunkToRegionCoord(int chunkCoord) {
        return chunkCoord >> CHUNK_SHIFT;
    }
    public static int regionToChunkCoord(int regionCoord) {
        return regionCoord << CHUNK_SHIFT;
    }
    public static RegionPos readRegionPos(ByteBuf input) {
        return unpack(input.readLong());
    }
    public static void writeRegionPos(ByteBuf output, RegionPos regionPos) {
        output.writeLong(regionPos.pack());
    }
    public static int localChunkInRegion(int chunkCoord) {
        return chunkCoord & CHUNK_TO_REGION_MASK;
    }
    public static int localBlockInRegion(int blockPos) {
        return blockPos & BLOCK_TO_REGION_MASK;
    }


    public int getMinBlockX() {
        return toBlockCoord(this.x);
    }

    public int getMinBlockZ() {
        return toBlockCoord(this.z);
    }

    public int getMaxBlockX() {
        return this.getMinBlockX() + REGION_SIZE;
    }

    public int getMaxBlockZ() {
        return this.getMinBlockZ() + REGION_SIZE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RegionPos pos)) return false;
        return this.x == pos.x && this.z == pos.z;
    }

    @Override
    public int hashCode() {
        return hash(this.x,this.z);
    }


    public static int hash(int x, int z) {
        int xTransform = 1664525 * x + 1013904223;
        int zTransform = 1664525 * (z ^ -559038737) + 1013904223;
        return xTransform ^ zTransform;
    }

}
