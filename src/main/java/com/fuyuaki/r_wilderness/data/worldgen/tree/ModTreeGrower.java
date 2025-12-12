package com.fuyuaki.r_wilderness.data.worldgen.tree;

import com.fuyuaki.r_wilderness.data.worldgen.features.ModFeatureUtils;
import com.fuyuaki.r_wilderness.data.worldgen.features.ModTreeFeatures;
import com.fuyuaki.r_wilderness.data.worldgen.placement.ModTreePlacements;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModTreeGrower {
    public static final TreeGrower ALPINE_TREE = new TreeGrower(MODID + "alpine_tree",
            Optional.empty(), Optional.of(ModTreeFeatures.ALPINE_TREE), Optional.empty());
}
