package com.fuyuaki.r_wilderness.server.commands;

import com.fuyuaki.r_wilderness.server.commands.arguments.RegionLayerArgument;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayerTypes;
import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2i;
import org.slf4j.Logger;

import java.time.Duration;

public class RegionLocateCommand {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int MAX_STRUCTURE_SEARCH_RADIUS = 100;
    private static final int MAX_BIOME_SEARCH_RADIUS = 6400;
    private static final int BIOME_SAMPLE_RESOLUTION_HORIZONTAL = 32;
    private static final int BIOME_SAMPLE_RESOLUTION_VERTICAL = 64;
    private static final int POI_SEARCH_RADIUS = 256;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                Commands.literal("region_locate")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)).then(
                                Commands.argument("layer_name", RegionLayerArgument.argument())
                                        .then(
                                                Commands.argument("target_value", FloatArgumentType.floatArg())
                                                        .then(
                                                                Commands.argument("search_range", IntegerArgumentType.integer(64,RegionPos.REGION_SIZE))
                                                                        .then(Commands.argument("position", Vec2Argument.vec2(true))
                                                                                .executes(
                                                                                        c -> searchForMatch(
                                                                                                c.getSource(),
                                                                                                RegionLayerArgument.getType(c,"layer_name"),
                                                                                                FloatArgumentType.getFloat(c, "target_value"),
                                                                                                IntegerArgumentType.getInteger(c, "search_range"),
                                                                                                Vec2Argument.getVec2(c, "position")
                                                                                        )
                                                                                )
                                                                        )
                                                                        .executes(
                                                                                c -> searchForMatch(
                                                                                        c.getSource(),
                                                                                        RegionLayerArgument.getType(c,"layer_name"),
                                                                                        FloatArgumentType.getFloat(c, "target_value"),
                                                                                        IntegerArgumentType.getInteger(c, "search_range"),
                                                                                        new Vec2((float) c.getSource().getPosition().x(), (float) c.getSource().getPosition().z())
                                                                                )
                                                                        )))));
    }

    private static int searchForMatch(CommandSourceStack source, RegionLayerTypes layer, float targetValue, int searchRange, Vec2 position) {
        RegionMemory memory = RegionMemory.getInstance(source.getLevel());

        Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
        Pair<Vector2i, Float> result = memory.spiralLocate(layer,targetValue,searchRange,position);

            return showLocateResult(source, layer, position, result, targetValue, stopwatch.elapsed(),searchRange);
    }

    private static int showLocateResult(
            CommandSourceStack source,
            RegionLayerTypes layer,
            Vec2 origin,
            Pair<Vector2i, Float> found,
            float target,
            Duration taskDuration,
            int range
    ){

        if (found.getSecond() >= target + 100) {
            source.sendFailure(Component.translatable("commands.r_wilderness.arguments.region_layer.fail", target, range));
            return -1;
        }
        BlockPos foundPos = new BlockPos(found.getFirst().x,0, found.getFirst().y);
        int distance = Mth.floor(dist(Mth.floor(origin.x), Mth.floor(origin.y), foundPos.getX(), foundPos.getZ()));

        Component coordinates = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", foundPos.getX(), "~", foundPos.getZ()))
                .withStyle(
                        s -> s.withColor(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent.SuggestCommand("/tp @s " + foundPos.getX() + " " + "~" + " " + foundPos.getZ()))
                                .withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip")))
                );


        source.sendSuccess(() -> Component.translatable("commands.r_wilderness.arguments.region_layer.success", target, layer.getName(), found.getSecond(), coordinates, distance), false);
        LOGGER.info("Locating element {} took {} ms", layer.getName(), taskDuration.toMillis());
        return distance;
    }
    private static float dist(int x1, int z1, int x2, int z2) {
        int dx = x2 - x1;
        int dz = z2 - z1;
        return Mth.sqrt(dx * dx + dz * dz);
    }
}
