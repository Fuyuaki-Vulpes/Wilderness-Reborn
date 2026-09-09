package com.fuyuaki.r_wilderness.world.environment;

import static com.fuyuaki.r_wilderness.world.environment.TemperatureData.*;

public record EnvironmentSampler (
            float vegetationDensity,
            float humidity,
            float temperature,
            float continentalness
    ){
        public float temperatureInDegrees(){
            float baseTemp = ENVIRONMENT_MEDIAN + (temperature * ENVIRONMENT_DEVIATION);

            float continentalEffect = 1.0f + continentalness * 0.25f;
            float adjustedTemperature = baseTemp * continentalEffect;

            float tempDeviation = adjustedTemperature - ENVIRONMENT_MEDIAN;

            float environmentalFactor = humidity * 0.15f - vegetationDensity * 0.15f;

            return adjustedTemperature + (tempDeviation * environmentalFactor);
        }

    }

