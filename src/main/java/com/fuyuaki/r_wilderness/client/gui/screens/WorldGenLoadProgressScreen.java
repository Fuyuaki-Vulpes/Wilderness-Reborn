package com.fuyuaki.r_wilderness.client.gui.screens;

import com.fuyuaki.r_wilderness.util.RUtil;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.Comparator;

public class WorldGenLoadProgressScreen extends Screen {
    private static final String LOADING_REGIONS_TEXT = "r_wilderness.generation.regions";
    private static final Component WARNING_1 = Component.translatable("r_wilderness.generation.warn").withColor(TextColor.YELLOW);
    private static final Component WARNING_2 = Component.translatable("r_wilderness.generation.warn_second_line").withColor(TextColor.YELLOW);
    private static final int MAX_DOTS = 4;
    private static final int GAP = 4;
    private static final int SIZE = 16;
    private static final int STRIDE = SIZE + GAP;

    private final BufferSquare[] cubes = new BufferSquare[]{ new BufferSquare(0), new BufferSquare(1), new BufferSquare(3) };
    private final BufferSquare[] renderSortedCubes = new BufferSquare[3];
    private final Comparator<BufferSquare> depthComparator = Comparator.comparingDouble(c -> c.gx + c.gy);

    private float animationSpeed = 0.5f;
    private float stateTicks = 0.0f;
    private int sequenceIndex = 0;

    private static final float SLIDE_TICKS = 8.0f;
    private static final float PAUSE_TICKS = 4.0f;
    private static final float TOTAL_STEP_TICKS = SLIDE_TICKS + PAUSE_TICKS;

    private static final float[][] SLOTS = {
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1}
    };

    private static final int[][] SLIDE_SEQUENCE = {
            {1, 2},
            {0, 1},
            {3, 0},
            {2, 3}
    };

    private int currentDots = 1;
    private float dotDelta = 1.0f;


    public WorldGenLoadProgressScreen() {
        super(GameNarrator.NO_TITLE);
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

        this.stateTicks += this.animationSpeed;

        if (this.stateTicks >= TOTAL_STEP_TICKS) {
            this.stateTicks -= TOTAL_STEP_TICKS;

            int[] currentStep = SLIDE_SEQUENCE[this.sequenceIndex];
            BufferSquare movedCube = getSquareInSlot(currentStep[0]);
            if (movedCube != null) {
                movedCube.slot = currentStep[1];
            }

            this.sequenceIndex = (this.sequenceIndex + 1) % SLIDE_SEQUENCE.length;
        }

    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int xCenter = this.width / 2;
        int yCenter = this.height / 2;
        int topPosition = yCenter - 50;

        StringBuilder loadingDots = new StringBuilder();
        for (int dots = 1; dots <= MAX_DOTS; dots ++){
            if (currentDots >= dots){
                loadingDots.append(".");
            }
        }
        graphics.centeredText(this.font, Component.translatable(LOADING_REGIONS_TEXT).append(loadingDots.toString()), xCenter, topPosition, -1);

        int maxTextWidth = Mth.floor(this.width * 0.75f);
        topPosition = this.height - 64;
        RUtil.centeredTextWithWordWrap(graphics,this.font, WARNING_1, xCenter, topPosition, maxTextWidth,-1);
        topPosition += 16;
        RUtil.centeredTextWithWordWrap(graphics,this.font, WARNING_2, xCenter, topPosition, maxTextWidth,-1);
        float renderTicks = this.stateTicks;


        int[] step = SLIDE_SEQUENCE[this.sequenceIndex];
        BufferSquare movingCube = getSquareInSlot(step[0]);

        if (movingCube != null) {
            if (renderTicks < SLIDE_TICKS) {
                float progress = easeInOutCubic(Math.min(1.0f, renderTicks / SLIDE_TICKS));
                float[] from = SLOTS[step[0]];
                float[] to = SLOTS[step[1]];
                movingCube.gx = from[0] + (to[0] - from[0]) * progress;
                movingCube.gy = from[1] + (to[1] - from[1]) * progress;
            } else {
                float[] to = SLOTS[step[1]];
                movingCube.gx = to[0];
                movingCube.gy = to[1];
            }
        }

        System.arraycopy(this.cubes, 0, this.renderSortedCubes, 0, 3);
        Arrays.sort(this.renderSortedCubes, this.depthComparator);
        float totalGridSize = (2.0f * SIZE) + GAP;
        float originX = (this.width - totalGridSize) / 2.0f;
        float originY = (this.height - totalGridSize) / 2.0f;

        int fX = Mth.floor(originX);
        int fY = Mth.floor(originY);
        graphics.fill(fX - 4, fY - 4, fX + STRIDE + SIZE + 4, fY+ STRIDE + SIZE + 4,ARGB.opaque(2105376));

        for (int xI = 0; xI <= 1; xI ++) {
            for  (int yI = 0; yI <= 1; yI ++) {
                int x = fX + (STRIDE * xI);
                int y = fY + (STRIDE * yI);
                graphics.fill(x - 1,y - 1, x + SIZE + 1, y + SIZE + 1, ARGB.opaque(5263440));
            }
        }
        for (BufferSquare square : this.renderSortedCubes) {
            float x = originX + (square.gx * STRIDE);
            float y = originY + (square.gy * STRIDE);
            graphics.fill(Mth.floor(x), Mth.floor(y), Mth.floor(x) + SIZE, Mth.floor(y) + SIZE, ARGB.opaque(14024652));
        }
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

    private BufferSquare getSquareInSlot(int slot) {
        for (BufferSquare c : this.cubes) {
            if (c.slot == slot) return c;
        }
        return null;
    }

    private float easeInOutCubic(float t) {
        return (float) Mth.smoothstep(t);
    }

    private static class BufferSquare {
        int slot;
        float gx, gy;

        BufferSquare(int slot) {
            this.slot = slot;
            this.gx = SLOTS[slot][0];
            this.gy = SLOTS[slot][1];
        }
    }

}
