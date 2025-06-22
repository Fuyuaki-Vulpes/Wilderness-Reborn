package com.fuyuaki.wilderness_reborn.mixin;


import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(OverworldBiomeBuilder.class)
public class OverworldBiomeBuilderMixin {


    @Shadow @Final private Climate.Parameter FULL_RANGE;

    @Shadow @Final private Climate.Parameter nearInlandContinentalness;

    @Shadow @Final private Climate.Parameter mushroomFieldsContinentalness;

    @Shadow @Final private Climate.Parameter deepOceanContinentalness;

    @Shadow @Final private Climate.Parameter oceanContinentalness;

    @Shadow @Final private Climate.Parameter coastContinentalness;

    @Shadow @Final private Climate.Parameter inlandContinentalness;

    @Shadow @Final private Climate.Parameter farInlandContinentalness;

    @Shadow @Final private Climate.Parameter midInlandContinentalness;

    @Inject(method = "addBiomes",at = @At("HEAD"), cancellable = true)
    public void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> key, CallbackInfo ci) {
        key.accept(Pair.of(
                Climate.parameters
                        (
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.farInlandContinentalness,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                0.01F
                        ),
                Biomes.DESERT
        ));
        key.accept(Pair.of(
                Climate.parameters
                        (
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.midInlandContinentalness,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                0.01F
                        ),
                Biomes.WINDSWEPT_SAVANNA
        ));

        key.accept(Pair.of(
                Climate.parameters
                        (
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.nearInlandContinentalness,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                0.01F
                        ),
                Biomes.PLAINS
        ));

        key.accept(Pair.of(
                Climate.parameters
                        (
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.coastContinentalness,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                0.01F
                        ),
                Biomes.BEACH
        ));

        key.accept(Pair.of(
                Climate.parameters
                        (
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.oceanContinentalness,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                0.01F
                        ),
                Biomes.OCEAN
        ));

        key.accept(Pair.of(
                Climate.parameters
                        (
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.deepOceanContinentalness,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                0.01F
                        ),
                Biomes.DEEP_OCEAN
        ));

        key.accept(Pair.of(
                Climate.parameters
                        (
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.mushroomFieldsContinentalness,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                this.FULL_RANGE,
                                0.01F
                        ),
                Biomes.MUSHROOM_FIELDS
        ));

        ci.cancel();
    }

}
