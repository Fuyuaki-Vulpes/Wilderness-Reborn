package com.fuyuaki.r_wilderness.world.environment;

public interface ServerPlayerEnvironment {

    HydrationData getHydrationData();
    TemperatureData getTemperatureData();
    void addExhaustion(float exhaustion);
    void addEnergy(float exhaustion);
    void drink(int hydrationLevelModifier, float saturationLevelModifier);
    void drink(HydrationProperties foodProperties);

}
