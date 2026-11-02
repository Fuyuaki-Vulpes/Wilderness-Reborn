package com.fuyuaki.r_wilderness.references;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public class RBlockItemIds {


    private static ColorCollection<BlockItemId> createSimpleColored(String baseName) {
        return ColorCollection.prefixWithColor(ColorCollection.create(baseName)).map(RBlockItemIds::create);
    }

    private static WeatheringCopperCollection<BlockItemId> createSimpleCopper(String baseName) {
        return WeatheringCopperCollection.prefixWithState(WeatheringCopperCollection.create(baseName)).map(RBlockItemIds::create);
    }

    public static BlockItemId create(Identifier blockId, Identifier itemId) {
        return new BlockItemId(ResourceKey.create(Registries.BLOCK, blockId), ResourceKey.create(Registries.ITEM, itemId));
    }

    public static BlockItemId create(String blockName, String itemName) {
        return create(RWildernessMod.modLocation(blockName), RWildernessMod.modLocation(itemName));
    }

    public static BlockItemId create(String name) {
        Identifier id = RWildernessMod.modLocation(name);
        return create(id, id);
    }

}
