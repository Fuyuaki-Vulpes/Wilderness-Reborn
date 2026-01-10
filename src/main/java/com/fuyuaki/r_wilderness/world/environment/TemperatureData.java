package com.fuyuaki.r_wilderness.world.environment;

import com.fuyuaki.r_wilderness.api.common.ModTags;
import com.fuyuaki.r_wilderness.world.generation.WildChunkGenerator;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class TemperatureData implements ValueIOSerializable {


    public static final float COLD_THRESHOLD = 5.0F;
    public static final float HOT_THRESHOLD = 30.0F;
    public static final float ENVIRONMENT_COLDEST = -50.0F;
    public static final float ENVIRONMENT_HOTTEST = 40.0F;
    public static final double ENVIRONMENT_MEDIAN = 20.0F;
    private static final float BASE_BODY_TEMPERATURE = 37.0F;
    private static final int BLOCK_SCAN_TICK_RATE = 5;
    private float bodyTemperature = 37.0F;
    private float environmentTemperature = 20.0F;
    private float energy = 0.0F;
    public static final StreamCodec<RegistryFriendlyByteBuf, TemperatureData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            TemperatureData::getBodyTemperature,
            ByteBufCodecs.FLOAT,
            TemperatureData::getEnvironmentTemperature,
            ByteBufCodecs.FLOAT,
            TemperatureData::getEnergy,
            TemperatureData::new
    );

    public TemperatureData(float waterLevel, float saturationLevel, float energy){
        this.bodyTemperature = waterLevel;
        this.environmentTemperature = saturationLevel;
        this.energy = energy;
    }
    public TemperatureData(){}

    public float getBodyTemperature() {
        return bodyTemperature;
    }

    public float getEnvironmentTemperature() {
        return environmentTemperature;
    }

    public float getEnergy() {
        return energy;
    }

    public void tick(ServerPlayer player) {
        ServerLevel serverlevel = player.level();
        Difficulty difficulty = serverlevel.getDifficulty();
        doTick(serverlevel,player,difficulty);
    }

    private void doTick(ServerLevel serverlevel, ServerPlayer player, Difficulty difficulty) {
        if (Float.isNaN(this.bodyTemperature)){
            this.bodyTemperature = BASE_BODY_TEMPERATURE;
        }
        float temperatureIntake = 0;
        float bodyAdjustment = 0;
        float hydration = ((PlayerEnvironment)player).getHydrationData().getWaterLevel();

        ChunkGenerator gen = serverlevel.getChunkSource().getGenerator();
        if (gen instanceof WildChunkGenerator generator){
            TerrainParameters.Environment environment = generator.terrainParameters().environment(player.getX(),player.getZ());
            this.environmentTemperature = environment.temperatureInDegrees();

            if (difficulty != Difficulty.PEACEFUL) {
                float envApparent = environment.temperature() / 1.5F;
                temperatureIntake += envApparent * 0.0025F;
                ((ServerPlayerEnvironment) player).addExhaustion(Math.max(envApparent,0) * 0.001F);
                bodyAdjustment -= envApparent * 0.0015F;
            }
        }
        if (difficulty != Difficulty.PEACEFUL) {
            float difference = Math.abs(this.bodyTemperature - BASE_BODY_TEMPERATURE);
            if (difference < 0.5F) {
                float multiplier = (float) Math.clamp(difference / 0.5, 0, 1);
                if (this.bodyTemperature > BASE_BODY_TEMPERATURE) {
                    bodyAdjustment -= 0.0005F * multiplier;

                } else {
                    bodyAdjustment += 0.0005F * multiplier;

                }


            }else if (difference < 0.85F){
                if (this.bodyTemperature < BASE_BODY_TEMPERATURE) {
                    bodyAdjustment += 0.0005F;
                    ((ServerPlayerEnvironment) player).addExhaustion(Math.max(temperaturePercentage(this.environmentTemperature), 0) * 0.00025F);

                }

                if (this.bodyTemperature > BASE_BODY_TEMPERATURE) {
                    bodyAdjustment -= 0.0005F;
                    ((ServerPlayerEnvironment) player).addExhaustion(Math.max(temperaturePercentage(this.environmentTemperature), 0) * 0.001F);

                }
            }else{
                if (this.bodyTemperature < BASE_BODY_TEMPERATURE) {
                    bodyAdjustment += 0.001F;
                    ((ServerPlayerEnvironment) player).addExhaustion(Math.max(temperaturePercentage(this.environmentTemperature), 0) * 0.00025F);

                }

                if (this.bodyTemperature > BASE_BODY_TEMPERATURE) {
                    bodyAdjustment -= 0.001F;
                    ((ServerPlayerEnvironment) player).addExhaustion(Math.max(temperaturePercentage(this.environmentTemperature), 0) * 0.05F);

                }
            }



        }
        if (player.tickCount % BLOCK_SCAN_TICK_RATE == 0){

            for (BlockPos pos : BlockPos.betweenClosed(player.getBoundingBox().inflate(5,5,5))){
                BlockState state = serverlevel.getBlockState(pos);
                if (state.is(ModTags.Blocks.INCREASES_TEMPERATURE)){
                    if (state.is(ModTags.Blocks.SLIGHTLY_INCREASES_TEMPERATURE)){
                        temperatureIntake += 0.000025F;
                    }else if (state.is(ModTags.Blocks.MODERATELY_INCREASES_TEMPERATURE)){
                        temperatureIntake += 0.00005F;

                    }else if (state.is(ModTags.Blocks.GREATLY_INCREASES_TEMPERATURE)){
                        temperatureIntake += 0.0001F;

                    }
                }
                else if (state.is(ModTags.Blocks.DECREASES_TEMPERATURE)){
                    if (state.is(ModTags.Blocks.SLIGHTLY_DECREASES_TEMPERATURE)){
                        temperatureIntake -= 0.000025F;

                    }else if (state.is(ModTags.Blocks.MODERATELY_DECREASES_TEMPERATURE)){
                        temperatureIntake -= 0.00005F;

                    }else if (state.is(ModTags.Blocks.GREATLY_DECREASES_TEMPERATURE)){
                        temperatureIntake -= 0.0001F;

                    }
                }
            }
        }

        if (energy > 0){
            boolean isMoving = player.getDeltaMovement().horizontalDistance() > 0.1F;
            float movementHydrationMultiplier = isMoving ? 1.0F : 0.01F;


            this.energy = (float) Math.max(this.energy - (hydration / 20 * 0.01),0);
            temperatureIntake += energy * 0.000005F;
            ((ServerPlayerEnvironment) player).addExhaustion(Math.max(temperaturePercentage(this.environmentTemperature),0) * 0.005F * movementHydrationMultiplier);

        }

        this.bodyTemperature += temperatureIntake;
        this.bodyTemperature += bodyAdjustment * (hydration / 20 );

    }


    @Override
    public void serialize(ValueOutput output) {
        output.putFloat("body", this.bodyTemperature);
        output.putFloat("environment", this.environmentTemperature);
        output.putFloat("energy", this.energy);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.bodyTemperature = input.getFloatOr("body", 37.0F);
        this.environmentTemperature = input.getFloatOr("environment", 25.0F);
        this.energy = input.getFloatOr("energy", 25.0F);
    }



    public static float temperaturePercentage(float temperature){
        if (temperature >= ENVIRONMENT_MEDIAN){
            return (float) Math.clamp(((temperature - ENVIRONMENT_MEDIAN) / (ENVIRONMENT_HOTTEST - ENVIRONMENT_MEDIAN) / 2) + 0.5,0.5,1);
        }
        return (float) Math.clamp((temperature - ENVIRONMENT_COLDEST) / (ENVIRONMENT_MEDIAN - ENVIRONMENT_COLDEST) / 2,0,0.5);
    }

    private static float toScale(float input){
        return Math.clamp((input + 1.5F) / 2,0,1);
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }
    public void addEnergy(float energy) {
        this.energy += energy;
    }
}
