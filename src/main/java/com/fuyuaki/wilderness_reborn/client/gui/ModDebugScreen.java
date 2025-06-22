package com.fuyuaki.wilderness_reborn.client.gui;

import com.fuyuaki.wilderness_reborn.data.pack.levelgen.PackNoiseRouterData;
import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModNoiseRouterData;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debugchart.*;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModDebugScreen {
    private static final int COLOR_GREY = 14737632;
    private static final int MARGIN_RIGHT = 2;
    private static final int MARGIN_LEFT = 2;
    private static final int MARGIN_TOP = 2;
    private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES = Maps.newEnumMap(
            Map.of(
                    Heightmap.Types.WORLD_SURFACE_WG,
                    "SW",
                    Heightmap.Types.WORLD_SURFACE,
                    "S",
                    Heightmap.Types.OCEAN_FLOOR_WG,
                    "OW",
                    Heightmap.Types.OCEAN_FLOOR,
                    "O",
                    Heightmap.Types.MOTION_BLOCKING,
                    "M",
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    "ML"
            )
    );
    private final Minecraft minecraft;
    private final Font font;
    private final TpsDebugChart tpsChart;
    private HitResult block;
    private HitResult liquid;
    @Nullable
    private ChunkPos lastPos;
    @Nullable
    private LevelChunk clientChunk;
    @Nullable
    private CompletableFuture<LevelChunk> serverChunk;
    private final LocalSampleLogger tickTimeLogger = new LocalSampleLogger(TpsDebugDimensions.values().length);

    public ModDebugScreen(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.font = minecraft.font;
        this.tpsChart = new TpsDebugChart(this.font, this.tickTimeLogger, () -> minecraft.level.tickRateManager().millisecondsPerTick());
    }
    public void clearChunkCache() {
        this.serverChunk = null;
        this.clientChunk = null;
    }
    public void render(GuiGraphics guiGraphics) {
        if (minecraft.getDebugOverlay().showDebugScreen()) return;
        ProfilerFiller profilerfiller = Profiler.get();
        Entity entity = this.minecraft.getCameraEntity();
        BlockPos blockpos = entity.blockPosition();
        List<String> list = Lists.newArrayList();
        ServerLevel serverlevel = this.getServerLevel();
        if (serverlevel != null) {
            ServerChunkCache chunkSource = serverlevel.getChunkSource();
            RandomState randomstate = chunkSource.randomState();
            Climate.Sampler sampler = randomstate.sampler();

            DecimalFormat decimalformat = new DecimalFormat("0.0000");
            NoiseRouter router = randomstate.router();
            DensityFunction.SinglePointContext densityfunction$singlepointcontext = new DensityFunction.SinglePointContext(
                    blockpos.getX(), blockpos.getY(), blockpos.getZ()
            );
            double d0 = router.ridges().compute(densityfunction$singlepointcontext);
            list.add("Biome: ");
            list.add(minecraft.level.getBiome(blockpos).unwrap().map(biomeKey -> biomeKey.location().toString(), biome -> "[unregistered " + biome + "]"));
            list.add(" ");

            list.add(
                    "NoiseRouter:");
            list.add("Temperature: "
                    + decimalformat.format(router.temperature().compute(densityfunction$singlepointcontext)));
            list.add("Vegetation: "
                    + decimalformat.format(router.vegetation().compute(densityfunction$singlepointcontext)));
            list.add("Continents: "
                    + decimalformat.format(router.continents().compute(densityfunction$singlepointcontext)));
            list.add("Erosion: "
                    + decimalformat.format(router.erosion().compute(densityfunction$singlepointcontext)));
            list.add("Depth: "
                    + decimalformat.format(router.depth().compute(densityfunction$singlepointcontext)));
            list.add("Ridges: "
                    + decimalformat.format(d0));
            list.add("Peaks & Valleys: "
                    + decimalformat.format(PackNoiseRouterData.peaksAndValleys((float) d0)));
            list.add("Density No Jaggedness: "
                    + decimalformat.format(router.initialDensityWithoutJaggedness().compute(densityfunction$singlepointcontext)));
            list.add("Final Density: "
                    + decimalformat.format(router.finalDensity().compute(densityfunction$singlepointcontext)));


            int i = QuartPos.fromBlock(blockpos.getX());
            int j = QuartPos.fromBlock(blockpos.getY());
            int k = QuartPos.fromBlock(blockpos.getZ());
            Climate.TargetPoint climate$targetpoint = sampler.sample(i, j, k);
            float f = Climate.unquantizeCoord(climate$targetpoint.continentalness());
            float f1 = Climate.unquantizeCoord(climate$targetpoint.erosion());
            float f2 = Climate.unquantizeCoord(climate$targetpoint.temperature());
            float f3 = Climate.unquantizeCoord(climate$targetpoint.humidity());
            float f4 = Climate.unquantizeCoord(climate$targetpoint.weirdness());
            double d = NoiseRouterData.peaksAndValleys(f4);
            OverworldBiomeBuilder overworldbiomebuilder = new OverworldBiomeBuilder();
            list.add(" ");
            list.add("Biome builder:");
            list.add("Peaks & Valleys: "
                    + OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(d));
            list.add(
                    "Continentalness: "
                            + overworldbiomebuilder.getDebugStringForContinentalness(f));
            list.add(
                    "Erosion: "
                            + overworldbiomebuilder.getDebugStringForErosion(f1));
            list.add(
                    "Temperature: "
                            + overworldbiomebuilder.getDebugStringForTemperature(f2));
            list.add(
                    "Humidity: "
                            + overworldbiomebuilder.getDebugStringForHumidity(f3));


            DecimalFormat df = new DecimalFormat("0");

            list.add(" ");
            list.add(
                    "X: " + df.format(entity.getOnPos().getX())
            );
            list.add(
                    "Y: " + df.format(entity.getOnPos().getY())
            );
            list.add(
                    " Z: " + df.format(entity.getOnPos().getZ())
            );


        }

        this.renderLines(guiGraphics,list,true);
    }



    @Nullable
    private ServerLevel getServerLevel() {
        IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
        return integratedserver != null ? integratedserver.getLevel(this.minecraft.level.dimension()) : null;
    }


    private void renderLines(GuiGraphics guiGraphics, List<String> lines, boolean leftSide) {
        int i = 9;

        for (int j = 0; j < lines.size(); j++) {
            String s = lines.get(j);
            if (!Strings.isNullOrEmpty(s)) {
                int k = this.font.width(s);
                int l = leftSide ? 2 : guiGraphics.guiWidth() - 2 - k;
                int i1 = 2 + i * j;
                guiGraphics.fill(l - 1, i1 - 1, l + k + 1, i1 + i - 1, -1873784752);
            }
        }

        for (int j1 = 0; j1 < lines.size(); j1++) {
            String s1 = lines.get(j1);
            if (!Strings.isNullOrEmpty(s1)) {
                int k1 = this.font.width(s1);
                int l1 = leftSide ? 2 : guiGraphics.guiWidth() - 2 - k1;
                int i2 = 2 + i * j1;
                guiGraphics.drawString(this.font, s1, l1, i2, 14737632, false);
            }
        }
    }

}
