package com.fuyuaki.r_wilderness.client.renderer.block;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

public class RFluidStateModels {
    public static final FluidModel.Unbaked RIVER_WATER = new FluidModel.Unbaked(
            new Material(Identifier.withDefaultNamespace("block/water_flow")),
            new Material(Identifier.withDefaultNamespace("block/water_flow")),
            new Material(Identifier.withDefaultNamespace("block/water_overlay")),
            net.neoforged.neoforge.client.fluid.FluidTintSources.water()
    );


}
