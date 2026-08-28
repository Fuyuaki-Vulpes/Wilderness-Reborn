package com.fuyuaki.r_wilderness.util;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.slf4j.Logger;

import java.util.Arrays;

public class RUtil {

    public static final Direction[] DIRECTIONS = Direction.values();
    public static final DyeColor[] DYE_COLORS = DyeColor.values();
    public static final DyeColor[] DYE_COLORS_NOT_WHITE = Arrays.stream(DYE_COLORS).filter(e -> e != DyeColor.WHITE).toArray(DyeColor[]::new);

    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isBlock(BlockState block, Block other)
    {
        return block.is(other);
    }


    public static double average(double... doubles){
        MutableDouble mutableDouble = new MutableDouble(0);
        Arrays.stream(doubles).forEach(mutableDouble::add);
        return mutableDouble.getValue() / doubles.length;
    }

    public static double averageSized(double size, double... doubles){
        MutableDouble mutableDouble = new MutableDouble(0);
        Arrays.stream(doubles).forEach(mutableDouble::add);
        return mutableDouble.getValue() / size;
    }

    public static double catmullrom(double delta, double controlPoint1, double controlPoint2, double controlPoint3, double controlPoint4) {
        return 0.5F
                * (
                2.0F * controlPoint2
                        + (controlPoint3 - controlPoint1) * delta
                        + (2.0F * controlPoint1 - 5.0F * controlPoint2 + 4.0F * controlPoint3 - controlPoint4) * delta * delta
                        + (3.0F * controlPoint2 - controlPoint1 - 3.0F * controlPoint3 + controlPoint4) * delta * delta * delta
        );
    }
    public static double diamondAngle(double x, double y)
    {
        if (y >= 0)
            return (x >= 0 ? y / (x + y) : 1 - x / (-x + y));
        else
            return (x < 0 ? 2 - y / (-x - y) : 3 + x / (x - y));
    }

    public static BlockState pickWaterBlockStateFor(boolean riverWater, Vec2 waterFlow) {
        if (!riverWater) return Blocks.WATER.defaultBlockState();
        BlockState waterState = Blocks.WATER.defaultBlockState();
        return waterState;
    }

    public static void centeredTextWithWordWrap(GuiGraphicsExtractor gui, Font font, FormattedText string, int x, int y, int width, int col) {
        centeredTextWithWordWrap(gui,font, string, x, y, width, col, true);
    }

    public static void centeredTextWithWordWrap(GuiGraphicsExtractor gui, Font font, FormattedText string, int x, int y, int width, int col, boolean dropShadow) {
        for (FormattedCharSequence line : font.split(string, width)) {
            gui.centeredText(font, line, x, y, col);
            y += 9;
        }
    }
}
