package com.fuyuaki.r_wilderness.world.environment;

import com.fuyuaki.r_wilderness.world.item.components.Drinkable;

public interface ServerPlayerEnvironment {

    HydrationData getHydrationData();
    TemperatureData getTemperatureData();
    void addExhaustion(float exhaustion);
    void addEnergy(float exhaustion);
    void drink(int hydrationLevelModifier, float saturationLevelModifier);
    void drinkInWorld(int hydrationLevelModifier, float saturationLevelModifier);
    void drink(Drinkable drinkable);
    void addHeat(float heat);
    void addCooling(float cooling);

}
