package com.fuyuaki.r_wilderness.data.generation.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.List;

public class RWAdvancements {

    public static SingleRegistryBootstrap<Advancement> create() {
    return new AdvancementProvider(
            List.of(
                    RWAdventureAdvancements::new
            )
    );
}
}