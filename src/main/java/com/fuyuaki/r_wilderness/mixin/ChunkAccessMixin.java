package com.fuyuaki.r_wilderness.mixin;


import com.fuyuaki.r_wilderness.mixin.accessor.ChunkAccessAccessor;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import com.fuyuaki.r_wilderness.world.level.levelgen.util.ChunkAccessModifier;
import com.fuyuaki.r_wilderness.world.level.levelgen.util.LevelChunkSectionModifier;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements ChunkAccessModifier, BiomeManager.NoiseBiomeSource, LightChunk, StructureAccess, net.neoforged.neoforge.attachment.IAttachmentHolder  {
    @Shadow public abstract ChunkPos getPos();

    @Shadow public abstract LevelHeightAccessor getHeightAccessorForGeneration();

    @Shadow public abstract LevelChunkSection getSection(int index);

    @Override
    public void fillBiomesReborn(RebornBiomeSource biomeSource, RandomState state) {
        ChunkPos chunkpos = this.getPos();
        int x = chunkpos.getMinBlockX();
        int z = chunkpos.getMinBlockZ();
        LevelHeightAccessor levelheightaccessor = this.getHeightAccessorForGeneration();

        for (int k = levelheightaccessor.getMinSectionY(); k <= levelheightaccessor.getMaxSectionY(); k++) {
            LevelChunkSection levelchunksection = this.getSection(this.getSectionIndexFromSectionY(k));
            int y = SectionPos.sectionToBlockCoord(k);
            ((LevelChunkSectionModifier)levelchunksection).fillBiomesReborn(biomeSource, state, x, y, z);
        }
    }
}
