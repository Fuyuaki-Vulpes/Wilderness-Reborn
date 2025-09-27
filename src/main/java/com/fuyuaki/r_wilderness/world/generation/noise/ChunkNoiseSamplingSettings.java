package com.fuyuaki.r_wilderness.world.generation.noise;


//Borrowed from TerraFirmaCraft
public record ChunkNoiseSamplingSettings(int minY, int cellCountXZ, int cellCountY, int cellWidth, int cellHeight, int firstCellX, int firstCellY, int firstCellZ) {}