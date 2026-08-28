package com.fuyuaki.r_wilderness.world.generation.progress;

import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPhase;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.mojang.logging.LogUtils;
import net.minecraft.util.Util;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RegionLoadTracker {
    public static final long LEVEL_LOAD_CLOSE_DELAY_MS = 500L;
    private final long closeDelayMs;
    private final ConcurrentHashMap<Long, RegionProgress> progress = new ConcurrentHashMap<>();

    private volatile Stage currentStage = Stage.INITIALIZING;
    private final AtomicInteger totalRegions = new AtomicInteger(1);
    private final AtomicInteger completedRegions = new AtomicInteger(0);
    private volatile long completedAtTimestamp = -1L;

    public RegionLoadTracker() {
        this(LEVEL_LOAD_CLOSE_DELAY_MS);
    }

    public RegionLoadTracker(long closeDelayMs) {
        this.closeDelayMs = closeDelayMs;
    }

    public void startStage(Stage stage, int totalRegionsForStage) {
        this.currentStage = stage;
        this.totalRegions.set(Math.max(1, totalRegionsForStage));
        this.completedRegions.set(0);
        this.completedAtTimestamp = -1L;
    }

    public void incrementTo(int total){
        if (this.completedRegions.get() >= total) return;
        this.completedRegions.set(total);

        if (total >= this.totalRegions.get() && this.completedAtTimestamp == -1L) {
            this.completedAtTimestamp = Util.getMillis();
        }
    }

    public float getProgress() {
        return Math.min(1.0f, (float) completedRegions.get() / totalRegions.get());
    }

    public Stage getCurrentStage() {
        return this.currentStage;
    }
    public boolean isReadyToTransition() {
        if (this.completedAtTimestamp == -1L) {
            return false;
        }
        return Util.getMillis() >= (this.completedAtTimestamp + this.closeDelayMs);
    }

    public boolean isFinished() {
        return this.isReadyToTransition() &&  this.currentStage == Stage.FINISHING;
    }

    public void reset() {
        this.currentStage = Stage.INITIALIZING;
        this.totalRegions.set(1);
        this.completedRegions.set(0);
        this.completedAtTimestamp = -1L;
    }

    public void setPos(RegionPos pos, RegionProgress p) {
        this.progress.put(pos.pack(), p);
    }

    public void tick() {
        AtomicInteger completedStage = new AtomicInteger();
        this.progress.forEach((key, value) -> {
            if (value.getPhase().isAtLeast(Stage.getPhase(this.currentStage))){
                completedStage.getAndIncrement();
            }
        });
        this.incrementTo(completedStage.get());
        synchronized (this) {
            if (this.isReadyToTransition()) {
                this.startStage(this.currentStage.next(), this.totalRegions.get());
            }
        }
    }

    public enum Stage {
        INITIALIZING("r_wilderness.generation.hint.initialize"),
        RASTERIZING("r_wilderness.generation.hint.raster"),
        PLATEAUS("r_wilderness.generation.hint.plateaus"),
        TERRAIN("r_wilderness.generation.hint.terrain"),
        GENERATING_RIVERS("r_wilderness.generation.hint.river"),
        POST_PROCESSING_RIVERS("r_wilderness.generation.hint.river_post"),
        FINISHING("r_wilderness.generation.hint.final");

        private final String label;
        Stage(String label) { this.label = label; }
        public String getLabel() { return label; }

        public static Stage getStageForPhase(RegionPhase phase) {
            return switch (phase){
                case EMPTY, INITIALIZED -> RegionLoadTracker.Stage.INITIALIZING;
                case RASTERIZATION -> RegionLoadTracker.Stage.RASTERIZING;
                case PLATEAU_AND_TERRAIN -> RegionLoadTracker.Stage.PLATEAUS;
                case ROUGH_HEIGHTS -> RegionLoadTracker.Stage.TERRAIN;
                case RIVERS -> RegionLoadTracker.Stage.GENERATING_RIVERS;
                case RIVERS_POST -> RegionLoadTracker.Stage.POST_PROCESSING_RIVERS;
                case LOADED_AND_FINISHED -> RegionLoadTracker.Stage.FINISHING;
            };
        }
        public static RegionPhase getPhase(Stage stage) {
            return switch (stage){
                case INITIALIZING -> RegionPhase.EMPTY;
                case RASTERIZING -> RegionPhase.RASTERIZATION;
                case PLATEAUS -> RegionPhase.PLATEAU_AND_TERRAIN;
                case TERRAIN -> RegionPhase.ROUGH_HEIGHTS;
                case GENERATING_RIVERS -> RegionPhase.RIVERS;
                case POST_PROCESSING_RIVERS -> RegionPhase.RIVERS_POST;
                case FINISHING -> RegionPhase.LOADED_AND_FINISHED;
            };
        }

        public Stage next() {
            if (this == FINISHING) {
                return this;
            }
            return values()[this.ordinal() + 1];
        }
    }

}
