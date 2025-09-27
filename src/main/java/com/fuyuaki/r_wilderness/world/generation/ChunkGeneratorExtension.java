package com.fuyuaki.r_wilderness.world.generation;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.neoforged.neoforge.client.event.RegisterPresetEditorsEvent;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

public interface ChunkGeneratorExtension {
    /**
     * Retrieves the {@link ChunkGeneratorExtension} from a structure generator state, if the underlying generator is present and
     * is a TFC compatible chunk generator. This is set in {@link #initRandomState(ChunkMap, ServerLevel)} in the individual generator,
     * by caching it through the {@link net.minecraft.world.level.levelgen.RandomState}.
     *
     * @param state The chunk generator structure state.
     * @return The underlying chunk generator.
     */
    static @Nullable ChunkGeneratorExtension getFromStructureState(ChunkGeneratorStructureState state) {
        return ((RandomStateExtension) (Object) state.randomState()).wildernessReborn$getChunkGeneratorExtension();
    }

    /**
     * @return The world generator settings.
     */
    WildGeneratorSettings settings();


    /**
     * Used on client to set the settings via the preset configuration screen.
     * This is technically compatible with any {@link ChunkGeneratorExtension} but will only exist if it is registered via {@link RegisterPresetEditorsEvent} for that screen.
     */
    void applySettings(UnaryOperator<WildGeneratorSettings> settings);


    Aquifer getOrCreateAquifer(ChunkAccess chunk);


    /**
     * Called from the initialization of {@link ChunkMap}, to initialize seed-based properties on any chunk generator implementing {@link ChunkGeneratorExtension}.
     */
    void initRandomState(ChunkMap chunkMap, ServerLevel level);

    default ChunkGenerator self() {
        return (ChunkGenerator) this;
    }
}
