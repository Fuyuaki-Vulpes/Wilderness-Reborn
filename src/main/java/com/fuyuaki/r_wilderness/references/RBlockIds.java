package com.fuyuaki.r_wilderness.references;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColorCollection;

public class RBlockIds {
    public static final ResourceKey<Block> RIVER_WATER = create("river_water");

    private static ResourceKey<Block> create(String name) {
        return ResourceKey.create(Registries.BLOCK, RWildernessMod.identifier(name));
    }

    private static ColorCollection<ResourceKey<Block>> createSimpleColored(String baseName) {
        return ColorCollection.prefixWithColor(ColorCollection.create(baseName)).map(RBlockIds::create);
    }

}
