package com.fuyuaki.r_wilderness.world.environment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;

public record HydrationProperties(int hydration, float saturation, boolean canAlwaysDrink) implements ConsumableListener {
    public static final Codec<HydrationProperties> DIRECT_CODEC = RecordCodecBuilder.create(
            p_366390_ -> p_366390_.group(
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("hydration").forGetter(HydrationProperties::hydration),
                            Codec.FLOAT.fieldOf("saturation").forGetter(HydrationProperties::saturation),
                            Codec.BOOL.optionalFieldOf("can_always_drink", false).forGetter(HydrationProperties::canAlwaysDrink)
                    )
                    .apply(p_366390_, HydrationProperties::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, HydrationProperties> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            HydrationProperties::hydration,
            ByteBufCodecs.FLOAT,
            HydrationProperties::saturation,
            ByteBufCodecs.BOOL,
            HydrationProperties::canAlwaysDrink,
            HydrationProperties::new
    );

    @Override
    public void onConsume(Level level, LivingEntity living, ItemStack stack, Consumable consumable) {
        RandomSource randomsource = living.getRandom();
        level.playSound(
                null, living.getX(), living.getY(), living.getZ(), consumable.sound().value(), SoundSource.NEUTRAL, 1.0F, randomsource.triangle(1.0F, 0.4F)
        );
        if (living instanceof Player player) {
            ((PlayerEnvironment)player).getHydrationData().drink(this);
        }
    }

    public static class Builder {
        private int nutrition;
        private float saturationModifier;
        private boolean canAlwaysDrink;

        public HydrationProperties.Builder nutrition(int nutrition) {
            this.nutrition = nutrition;
            return this;
        }

        public HydrationProperties.Builder saturationModifier(float saturationModifier) {
            this.saturationModifier = saturationModifier;
            return this;
        }

        public HydrationProperties.Builder alwaysDrinkable() {
            this.canAlwaysDrink = true;
            return this;
        }

        public HydrationProperties build() {
            float f = FoodConstants.saturationByModifier(this.nutrition, this.saturationModifier);
            return new HydrationProperties(this.nutrition, f, this.canAlwaysDrink);
        }
    }
}