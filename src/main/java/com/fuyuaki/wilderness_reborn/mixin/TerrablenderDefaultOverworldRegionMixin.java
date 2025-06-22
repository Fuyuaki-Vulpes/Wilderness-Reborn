package com.fuyuaki.wilderness_reborn.mixin;


import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.worldgen.DefaultOverworldRegion;

import java.util.function.Consumer;

@Mixin(DefaultOverworldRegion.class)
public abstract class TerrablenderDefaultOverworldRegionMixin extends Region {
    public TerrablenderDefaultOverworldRegionMixin(ResourceLocation name, RegionType type, int weight) {
        super(name, type, weight);
    }

    @Unique
    private final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.5F, 1.5F);
/*
    @Inject(method = "addBiomes",at = @At("HEAD"), cancellable = true)
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, CallbackInfo ci) {
        mapper.accept(
                Pair.of(
                        Climate.parameters(this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.point(0.0F), this.FULL_RANGE, 0.01F),
                        Biomes.PLAINS
                )
        );
    }*/
}
