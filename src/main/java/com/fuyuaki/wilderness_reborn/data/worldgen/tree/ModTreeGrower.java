package com.fuyuaki.wilderness_reborn.data.worldgen.tree;

import com.fuyuaki.wilderness_reborn.data.worldgen.features.ModFeatureUtils;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class ModTreeGrower {
    public static final TreeGrower ALPINE_TREE = new TreeGrower(MODID + "alpine_tree",
            Optional.empty(), Optional.of(ModFeatureUtils.ALPINE_TREE), Optional.empty());
}
