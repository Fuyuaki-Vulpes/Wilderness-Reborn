package com.fuyuaki.r_wilderness.init;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class RDamageTypes {
    public static final ResourceKey<DamageType> HYPOTHERMIA = create("hypothermia");
    public static final ResourceKey<DamageType> HYPERTHERMIA = create("hyperthermia");


    private static ResourceKey<DamageType> create (String key){
        return ResourceKey.create(Registries.DAMAGE_TYPE, RWildernessMod.modLocation(key));
    }


    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(HYPOTHERMIA, new DamageType(
                "hypothermia",
                DamageScaling.NEVER,
                0.25F,
                DamageEffects.FREEZING
        ));

        context.register(HYPERTHERMIA, new DamageType(
                "hyperthermia",
                DamageScaling.NEVER,
                0.25F,
                DamageEffects.BURNING
        ));

    }
}
