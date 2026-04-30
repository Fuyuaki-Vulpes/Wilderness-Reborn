package com.fuyuaki.r_wilderness.world.item.components;

import net.minecraft.sounds.SoundEvents;

public class Drinkables {

    public static final Drinkable APPLE = drinkable(1).saturation(1.0F).build();
    public static final Drinkable HONEY = drinkable(1).saturation(0.5F).build();
    public static final Drinkable WATERMELON = drinkable(6).saturation(8.0F).sound(SoundEvents.GENERIC_EAT).build();
    public static final Drinkable GLISTERING_WATERMELON = drinkable(12).saturation(20.0F).drinkSeconds(2.0F).doBurp().hasConsumeParticles().sound(SoundEvents.GENERIC_EAT).build();
    public static final Drinkable WATER_BOTTLE = drinkable(8).saturation(12.0F).build();
    public static final Drinkable MILK = drinkable(7).saturation(5.0F).build();
    public static final Drinkable BEETROOT = drinkable(5).saturation(7.0F).sound(SoundEvents.GENERIC_EAT).doBurp().build();
    public static final Drinkable BEETROOT_SOUP = drinkable(8).saturation(12.0F).sound(SoundEvents.GENERIC_EAT).build();
    public static final Drinkable BERRY = drinkable(2).saturation(5.0F).drinkSeconds(0.25F).sound(SoundEvents.GENERIC_EAT).build();
    public static final Drinkable STEW = drinkable(4).saturation(0.5F).build();

    private static Drinkable.Builder drinkable(int hydration) {
        return Drinkable.builder(hydration);
    }
}
