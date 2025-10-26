package com.fuyuaki.r_wilderness.world.generation.chunk;

import com.fuyuaki.r_wilderness.init.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public class ChunkData {
    public static final ChunkData EMPTY = new ChunkData.Immutable();

    /**
     * Accesses the chunk data from a given level, at a given position. This method <strong>may deadlock</strong> if called on a {@link ServerLevel}
     * from within a world generation context, as it will try and load the chunk. Make sure you are accessing the correct level for the context provided.
     *
     * @see #get(ChunkAccess)
     */
    public static ChunkData get(LevelReader level, BlockPos pos)
    {
        return get(level.getChunk(pos));
    }

    /**
     * Accesses the chunk data from a given level, at a given position. This method <strong>may deadlock</strong> if called on a {@link ServerLevel}
     * from within a world generation context, as it will try and load the chunk. Make sure you are accessing the correct level for the context provided.
     *
     * @see #get(ChunkAccess)
     */
    public static ChunkData get(LevelReader level, ChunkPos pos)
    {
        return get(level.getChunk(pos.x, pos.z));
    }

    /**
     * Accesses the chunk data from the given chunk. This is safe to call at all points during world generation, on server, or on client if proper
     * access to a chunk is already made. It may return different things when called in different contexts:
     * <ul>
     *     <li><strong>On Client</strong>, this will return a client-side, shallow copy of the chunk data, which is synced on chunk watch and unwatch
     *     to individual players</li>
     *     <li><strong>On Server</strong>, when invoked with a {@link LevelChunk}, this will return the full view of the chunk data.</li>
     *     <li><strong>During World Generation</strong>, this will always return a view of the chunk data, generated as much as possible. If the chunk
     *     is an impostor, the view of the underlying chunk will be returned.</li>
     * </ul>
     * Note that this should only be used for mutable access when the caller has ensured that the chunk is mutable at the time - an impostor, or empty
     * chunk will not allow mutation, or the chunk data on client should not be mutated under any case!
     *
     * @see #get(LevelReader, BlockPos)
     * @see #get(LevelReader, ChunkPos)
     */
    public static ChunkData get(ChunkAccess chunk)
    {
        return chunk instanceof ImposterProtoChunk impostor ? get(impostor.getWrapped())
                : chunk instanceof EmptyLevelChunk ? ChunkData.EMPTY
                : chunk.getData(ModAttachments.CHUNK_DATA);
    }

    private final ChunkPos pos;

    private Status status;

    @Nullable
    private int[] aquiferSurfaceHeight;

    private long lastRandomTick;


    public ChunkData(ChunkPos pos)
    {
        this.pos = pos;
        this.status = Status.EMPTY;
        this.lastRandomTick = -1;
    }

    public ChunkPos getPos()
    {
        return pos;
    }

    public int[] getAquiferSurfaceHeight()
    {
        assert aquiferSurfaceHeight != null;
        return aquiferSurfaceHeight;
    }


    public Status status()
    {
        return status;
    }

    public long getLastRandomTick()
    {
        return lastRandomTick;
    }







    public void serializeNBT(ValueOutput nbt)
    {

        nbt.putByte("status", (byte) status.ordinal());
        if (status == Status.FULL)
        {
            assert aquiferSurfaceHeight != null;

            nbt.putIntArray("aquiferSurfaceHeight", aquiferSurfaceHeight);
        }
        if (status == Status.FULL || status == Status.PARTIAL)
        {
            nbt.putLong("lastRandomTick", lastRandomTick);
        }
    }

    public void deserializeNBT(ValueInput nbt)
    {
        status = Status.valueOf(nbt.getByteOr("status", (byte) 0));
        if (status == Status.FULL)
        {
            aquiferSurfaceHeight = nbt.getIntArray("aquiferSurfaceHeight").get();
        }
        if (status == Status.FULL || status == Status.PARTIAL)
        {
            lastRandomTick = nbt.getLong("lastRandomTick").get();
        }
    }

    @Override
    public String toString()
    {
        return "ChunkData{pos=" + pos + ", status=" + status + ", hashCode=" + Integer.toHexString(hashCode()) + '}';
    }

    public enum Status
    {
        EMPTY, // Default, un-generated chunk data
        CLIENT, // Client-side shallow copy
        PARTIAL, // Partially generated (before fill noise)
        FULL, // Fully generated chunk data
        INVALID; // Invalid chunk data

        private static final Status[] VALUES = values();

        public static Status valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : EMPTY;
        }
    }

    /**
     * Only used for the empty instance, this will enforce that it never leaks data
     * New empty instances can be constructed via constructor, EMPTY instance is specifically for an immutable empty copy, representing invalid chunk data
     */
    private static final class Immutable extends ChunkData
    {
        private Immutable()
        {
            super(new ChunkPos(ChunkPos.INVALID_CHUNK_POS));
        }

        @Override
        public void deserializeNBT(ValueInput nbt)
        {
            error();
        }

        @Override
        public Status status()
        {
            return Status.INVALID;
        }

        @Override
        public String toString()
        {
            return "ImmutableChunkData";
        }

        private void error()
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }
    }
}
