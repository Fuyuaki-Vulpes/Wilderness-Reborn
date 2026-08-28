package com.fuyuaki.r_wilderness.client.gui.screens;

import com.fuyuaki.r_wilderness.util.RUtil;
import com.fuyuaki.r_wilderness.world.generation.progress.RegionLoadTracker;
import com.fuyuaki.r_wilderness.world.generation.progress.RegionProgress;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class WorldGenLoadProgressScreen extends Screen {
    private static final Component LOADING_REGIONS_TEXT = Component.translatable("r_wilderness.generation.regions");
    private static final Component WARNING_1 = Component.translatable("r_wilderness.generation.warn");
    private static final Component WARNING_2 = Component.translatable("r_wilderness.generation.warn_second_line");
    private static final int MAX_DOTS = 4;
    private RegionLoadTracker tracker;
    private static final int PROGRESS_BAR_WIDTH = 200;
    private float smoothedProgress = 0.0f;

    private boolean complete = false;
    private int currentDots = 1;
    private float dotDelta = 1.0f;

    public WorldGenLoadProgressScreen(RegionLoadTracker tracker) {
        super(GameNarrator.NO_TITLE);
        this.tracker = tracker;
    }

    public void setTracker(RegionLoadTracker tracker) {
        this.tracker = tracker;
    }

    public RegionLoadTracker getTracker() {
        return tracker;
    }

    @Override
    public void tick() {
        super.tick();
        dotDelta += 0.05f;
        currentDots = Mth.floor(dotDelta);
        if (currentDots > MAX_DOTS){
            currentDots = 1;
            dotDelta = 1.0f;
        }

        if (this.tracker == null) return;
        this.smoothedProgress = this.smoothedProgress + (this.tracker.getProgress() - this.smoothedProgress) * 0.2F;
        if (this.tracker.isFinished()) {
            this.complete = true;
            this.onClose();
        }
    }

    public boolean isComplete() {
        return complete;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int xCenter = this.width / 2;
        int yCenter = this.height / 2;
        int topPosition = yCenter - 50;

        graphics.centeredText(this.font, LOADING_REGIONS_TEXT, xCenter, topPosition, -1);
        topPosition += 12;
        int totalBarSize = Math.min(400, (int) (this.width * 0.75f));
        if(this.tracker == null) return;
        float totalProgress = Math.min(1.0f,this.tracker.getProgress() + (this.tracker.getCurrentStage().ordinal() / (RegionLoadTracker.Stage.values().length + 1.0f)));
        this.drawProgressBar(graphics,xCenter - (totalBarSize / 2), topPosition, totalBarSize,4,totalProgress);
        topPosition += 32;
        StringBuilder loadingDots = new StringBuilder();
        for (int dots = 1; dots <= MAX_DOTS; dots ++){
            if (currentDots >= dots){
                loadingDots.append(".");
            }
        }
        graphics.centeredText(this.font, Component.translatable(this.tracker.getCurrentStage().getLabel()).append(loadingDots.toString()), xCenter, topPosition , -1);
        topPosition += 12;
        int trackerSize = Math.min(200, (int) (this.width * 0.5f));
        this.drawProgressBar(graphics, xCenter - (trackerSize / 2), topPosition, trackerSize, 2, this.smoothedProgress);
        int maxTextWidth = Mth.floor(this.width * 0.75f);
        topPosition = this.height - 64;
        RUtil.centeredTextWithWordWrap(graphics,this.font, WARNING_1, xCenter, topPosition, maxTextWidth,-1);
        topPosition += 16;
        RUtil.centeredTextWithWordWrap(graphics,this.font, WARNING_2, xCenter, topPosition, maxTextWidth,-1);


    }

    private void drawProgressBar(GuiGraphicsExtractor graphics, int left, int top, int width, int height, float progress) {
        graphics.fill(left, top, left + width, top + height, -16777216);
        graphics.fill(left, top, left + Math.round(progress * width), top + height, -16711936);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.extractPanorama(graphics, a);
        this.extractBlurredBackground(graphics);
        this.extractMenuBackground(graphics);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


}
