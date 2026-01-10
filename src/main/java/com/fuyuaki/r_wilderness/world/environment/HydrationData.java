package com.fuyuaki.r_wilderness.world.environment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class HydrationData implements ValueIOSerializable {
    private static final int DEFAULT_TICK_TIMER = 0;
    private static final float DEFAULT_EXHAUSTION_LEVEL = 0.0F;
    private int waterLevel = 20;
    private float saturationLevel = 5.0F;
    private float exhaustionLevel;
    private int tickTimer;

    public static final StreamCodec<RegistryFriendlyByteBuf, HydrationData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            HydrationData::getWaterLevel,
            ByteBufCodecs.FLOAT,
            HydrationData::getSaturationLevel,
            HydrationData::new
    );

    public HydrationData(){

    }

    public HydrationData(int waterLevel, float saturationLevel) {
        this.waterLevel = waterLevel;
        this.saturationLevel = saturationLevel;
    }

    private void add(int waterLevel, float saturationLevel) {
        this.waterLevel = Mth.clamp(waterLevel + this.waterLevel, 0, 20);
        this.saturationLevel = Mth.clamp(saturationLevel + this.saturationLevel, 0.0F, (float)this.waterLevel);
    }


    public void tick(ServerPlayer player) {
        ServerLevel serverlevel = player.level();
        Difficulty difficulty = serverlevel.getDifficulty();
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.waterLevel = Math.max(this.waterLevel - 1, 0);
            }
        }else{
            this.exhaustionLevel += 0.005F;
        }

        if (this.waterLevel <= 0) {
            this.tickTimer++;
            if (this.tickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurtServer(serverlevel, player.damageSources().starve(), 1.0F);
                }

                this.tickTimer = 0;
            }
        }
    }
    public int getWaterLevel() {
        return this.waterLevel;
    }

    public boolean hasEnoughFood() {
        return this.getWaterLevel() > 6.0F;
    }

    public boolean needsFood() {
        return this.waterLevel < 20;
    }

    /**
     * Adds input to {@code foodExhaustionLevel} to a max of 40.
     */
    public void addExhaustion(float exhaustion) {
        this.exhaustionLevel = Math.min(this.exhaustionLevel + exhaustion, 40.0F);
    }
    public void drink(int hydrationLevelModifier, float saturationLevelModifier) {
        this.add(hydrationLevelModifier, FoodConstants.saturationByModifier(hydrationLevelModifier, saturationLevelModifier));
    }

    public void drink(HydrationProperties foodProperties) {
        this.add(foodProperties.hydration(), foodProperties.saturation());
    }

    public float getSaturationLevel() {
        return this.saturationLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setSaturation(float saturationLevel) {
        this.saturationLevel = saturationLevel;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("foodLevel", this.waterLevel);
        output.putInt("foodTickTimer", this.tickTimer);
        output.putFloat("foodSaturationLevel", this.saturationLevel);
        output.putFloat("foodExhaustionLevel", this.exhaustionLevel);

    }

    @Override
    public void deserialize(ValueInput input) {
            this.waterLevel = input.getIntOr("waterLevel", 20);
            this.tickTimer = input.getIntOr("waterTickTimer", 0);
            this.saturationLevel = input.getFloatOr("waterSaturationLevel", 5.0F);
            this.exhaustionLevel = input.getFloatOr("waterExhaustionLevel", 0.0F);

    }
}
