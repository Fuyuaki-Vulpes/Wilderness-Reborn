package com.fuyuaki.r_wilderness.client.menu.hud;


import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.environment.HydrationData;
import com.fuyuaki.r_wilderness.world.environment.PlayerEnvironment;
import com.fuyuaki.r_wilderness.world.environment.TemperatureData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;

public class RWildernessGui {

    private static final Identifier THIRST_HYDRATION_EMPTY_SPRITE = RWildernessMod.modLocation("hud/thirst_hydration_empty");
    private static final Identifier THIRST_HYDRATION_HALF_SPRITE = RWildernessMod.modLocation("hud/thirst_hydration_half");
    private static final Identifier THIRST_HYDRATION_FULL_SPRITE = RWildernessMod.modLocation("hud/thirst_hydration_full");
    private static final Identifier HYDRATION_EMPTY_SPRITE = RWildernessMod.modLocation("hud/hydration_empty");
    private static final Identifier HYDRATION_HALF_SPRITE = RWildernessMod.modLocation("hud/hydration_half");
    private static final Identifier HYDRATION_FULL_SPRITE = RWildernessMod.modLocation("hud/hydration_full");

    private static final Identifier TEMPERATURE_POINTER_COLD_SPRITE = RWildernessMod.modLocation("hud/temperature_pointer_cold");
    private static final Identifier TEMPERATURE_POINTER_SPRITE = RWildernessMod.modLocation("hud/temperature_pointer");
    private static final Identifier TEMPERATURE_POINTER_HOT_SPRITE = RWildernessMod.modLocation("hud/temperature_pointer_hot");
    private static final Identifier TEMPERATURE_BAR_SPRITE = RWildernessMod.modLocation("hud/temperature");

    private static final Identifier BODY_TEMPERATURE_HYPOTHERMIA_SPRITE = RWildernessMod.modLocation("hud/body_temperature/hypothermia");
    private static final Identifier BODY_TEMPERATURE_FREEZING_SPRITE = RWildernessMod.modLocation("hud/body_temperature/freezing");
    private static final Identifier BODY_TEMPERATURE_COLD_SPRITE = RWildernessMod.modLocation("hud/body_temperature/cold");
    private static final Identifier BODY_TEMPERATURE_NEUTRAL_SPRITE = RWildernessMod.modLocation("hud/body_temperature/neutral");
    private static final Identifier BODY_TEMPERATURE_HOT_SPRITE = RWildernessMod.modLocation("hud/body_temperature/hot");
    private static final Identifier BODY_TEMPERATURE_BOILING_SPRITE = RWildernessMod.modLocation("hud/body_temperature/boiling");
    private static final Identifier BODY_TEMPERATURE_HYPERTHERMIA_SPRITE = RWildernessMod.modLocation("hud/body_temperature/hyperthermia");

    public static void environmentTemperatureLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) return;
        Entity entity = minecraft.getCameraEntity();
        if (entity instanceof Player player) {
            TemperatureData temperatureData = ((PlayerEnvironment) player).getTemperatureData();

            int left = guiGraphics.guiWidth() / 2 + 91 + 8;
            int top = guiGraphics.guiHeight() - 4;
            DecimalFormat decimalformat = new DecimalFormat("0.0");

            float temperature = temperatureData.getEnvironmentTemperature();
            float offset = TemperatureData.temperaturePercentage(temperature) * 32 - 3;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEMPERATURE_BAR_SPRITE, left, top - 5, 32, 5);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, pointerSprite(temperature), Mth.floor(left + offset), top - 9, 6, 8);
            guiGraphics.drawString(minecraft.font,decimalformat.format(temperature) + "o C",left + 36,top - 10, ARGB.white(1.0F),true);

        }
    }

    private static Identifier pointerSprite(float temperature) {
        if (temperature < TemperatureData.COLD_THRESHOLD){
            return TEMPERATURE_POINTER_COLD_SPRITE;
        }
        if (temperature > TemperatureData.HOT_THRESHOLD){
            return TEMPERATURE_POINTER_HOT_SPRITE;
        }
        return TEMPERATURE_POINTER_SPRITE;
    }

    public static void bodyTemperatureLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) return;
        Entity entity = minecraft.getCameraEntity();
        if (entity instanceof Player player) {
            DecimalFormat decimalformat = new DecimalFormat("0.000");

            TemperatureData temperatureData = ((PlayerEnvironment) player).getTemperatureData();

            int left = guiGraphics.guiWidth() / 2 + 91;
            int top = guiGraphics.guiHeight() - 28;

            float temperature = temperatureData.getBodyTemperature();
            Identifier sprite = bodyTemperatureSpriteFor(temperature);

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, left, top, 16, 16);


            guiGraphics.drawString(minecraft.font,decimalformat.format(temperature) + "o C",left + 16,top + 4, ARGB.white(1.0F),true);

        }
    }

    private static Identifier bodyTemperatureSpriteFor(float temperature) {
        if (temperature > 37.5){
            if (temperature > 38.0){
                if (temperature > 38.5){
                    return BODY_TEMPERATURE_HYPERTHERMIA_SPRITE;
                }
                return BODY_TEMPERATURE_BOILING_SPRITE;
            }
            return BODY_TEMPERATURE_HOT_SPRITE;
        }
        else if (temperature < 36.5){
            if (temperature < 36.0){
                if (temperature < 35.5){
                    return BODY_TEMPERATURE_HYPOTHERMIA_SPRITE;
                }
                return BODY_TEMPERATURE_FREEZING_SPRITE;
            }
            return BODY_TEMPERATURE_COLD_SPRITE;
        }
        return BODY_TEMPERATURE_NEUTRAL_SPRITE;
    }

    public static void hydrationLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) return;
        Entity entity = minecraft.getCameraEntity();
        if (entity instanceof Player player && minecraft.gameMode.canHurtPlayer()) {

            HydrationData hydrationData = ((PlayerEnvironment) player).getHydrationData();
            int waterLevel = hydrationData.getWaterLevel();

            int x = guiGraphics.guiWidth() / 2 + 91;
            int y = guiGraphics.guiHeight() - 49;
            int maxAir = player.getMaxAirSupply();
            int currentAir = Math.clamp((long)player.getAirSupply(), 0, maxAir);
            if (currentAir < maxAir){
                y -= 10;
            }
            Identifier empty;
            Identifier half;
            Identifier full;

            if (player.hasEffect(MobEffects.HUNGER)) {
                empty = THIRST_HYDRATION_EMPTY_SPRITE;
                half = THIRST_HYDRATION_HALF_SPRITE;
                full = THIRST_HYDRATION_FULL_SPRITE;
            } else {
                empty = HYDRATION_EMPTY_SPRITE;
                half = HYDRATION_HALF_SPRITE;
                full = HYDRATION_FULL_SPRITE;
            }

            for (int j = 0; j < 10; j++) {
                int spriteY = y;

                if (player.getFoodData().getSaturationLevel() <= 0.0F && player.tickCount % (waterLevel * 3 + 1) == 0) {
                    spriteY = y + (RandomSource.create().nextInt(3) - 1);
                }

                int spriteX = x - j * 8 - 9;
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, empty, spriteX, spriteY, 9, 9);
                if (j * 2 + 1 < waterLevel) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, full, spriteX, spriteY, 9, 9);
                }

                if (j * 2 + 1 == waterLevel) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, half, spriteX, spriteY, 9, 9);
                }

            }
        }
    }

}
