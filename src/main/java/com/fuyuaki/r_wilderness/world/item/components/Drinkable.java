package com.fuyuaki.r_wilderness.world.item.components;

import com.fuyuaki.r_wilderness.world.environment.ServerPlayerEnvironment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.ArrayList;
import java.util.List;

public record Drinkable(float drinkSeconds, int hydration, float saturation,ItemUseAnimation animation, Holder<SoundEvent> sound, boolean hasConsumeParticles,boolean doBurp, List<ConsumeEffect> onDrinkEffects) {

    public static final Codec<Drinkable> CODEC = RecordCodecBuilder.create(
            p_366542_ -> p_366542_.group(
                            ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("drink_seconds", 0.8F).forGetter(Drinkable::drinkSeconds),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("hydration").forGetter(Drinkable::hydration),
                            ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("saturation", 3.0F).forGetter(Drinkable::saturation),
                            ItemUseAnimation.CODEC.optionalFieldOf("animation", ItemUseAnimation.EAT).forGetter(Drinkable::animation),
                            SoundEvent.CODEC.optionalFieldOf("sound", SoundEvents.GENERIC_EAT).forGetter(Drinkable::sound),
                            Codec.BOOL.optionalFieldOf("has_consume_particles", true).forGetter(Drinkable::hasConsumeParticles),
                            Codec.BOOL.optionalFieldOf("do_burp", true).forGetter(Drinkable::doBurp),
                            ConsumeEffect.CODEC.listOf().optionalFieldOf("on_consume_effects", List.of()).forGetter(Drinkable::onDrinkEffects)
                    )
                    .apply(p_366542_, Drinkable::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Drinkable> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            Drinkable::drinkSeconds,
            ByteBufCodecs.INT,
            Drinkable::hydration,
            ByteBufCodecs.FLOAT,
            Drinkable::saturation,
            ItemUseAnimation.STREAM_CODEC,
            Drinkable::animation,
            SoundEvent.STREAM_CODEC,
            Drinkable::sound,
            ByteBufCodecs.BOOL,
            Drinkable::hasConsumeParticles,
            ByteBufCodecs.BOOL,
            Drinkable::doBurp,
            ConsumeEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),
            Drinkable::onDrinkEffects,
            Drinkable::new
    );
    public int drinkTicks() {
        return (int)(this.drinkSeconds * 20.0F);
    }

    public InteractionResult startConsuming(LivingEntity entity, ItemStack stack, InteractionHand hand) {

        boolean flag = this.drinkTicks() > 0;
        if (flag) {
            entity.startUsingItem(hand);
            return InteractionResult.CONSUME;
        } else {
            ItemStack itemstack = this.onConsume(entity.level(), entity, stack);
            return InteractionResult.CONSUME.heldItemTransformedTo(itemstack);
        }

    }

    public ItemStack onConsume(Level level, LivingEntity entity, ItemStack stack) {
        RandomSource randomsource = entity.getRandom();
        this.emitParticlesAndSounds(randomsource, entity, stack, 16);
        if (entity instanceof ServerPlayer serverplayer) {
            serverplayer.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            ((ServerPlayerEnvironment)serverplayer).drink(this);
        }

        if (!level.isClientSide()) {
            this.onDrinkEffects.forEach(effect -> effect.apply(level, stack, entity));
        }
        if (entity instanceof Player player && this.doBurp) {
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.PLAYER_BURP,
                    SoundSource.PLAYERS,
                    0.5F,
                    Mth.randomBetween(randomsource, 0.9F, 1.0F)
            );
        }

        entity.gameEvent(GameEvent.DRINK);
        stack.consume(1, entity);
        return stack;
    }
    public void onConsumeAdditive(Level level, LivingEntity entity, ItemStack stack) {
        if (entity instanceof ServerPlayer serverplayer) {
            serverplayer.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            ((ServerPlayerEnvironment)serverplayer).drink(this);
        }

        if (!level.isClientSide()) {
            this.onDrinkEffects.forEach(effect -> effect.apply(level, stack, entity));
        }
    }
    public void emitParticlesAndSounds(RandomSource random, LivingEntity entity, ItemStack stack, int amount) {
        float f = random.nextBoolean() ? 0.5F : 1.0F;
        float f1 = random.triangle(1.0F, 0.2F);
        float f2 = 0.5F;
        float f3 = Mth.randomBetween(random, 0.9F, 1.0F);
        float f4 = this.animation == ItemUseAnimation.DRINK ? f2: f;
        float f5 = this.animation == ItemUseAnimation.DRINK ? f3 : f1;
        if (this.hasConsumeParticles) {
            entity.spawnItemParticles(stack, amount);
        }

        SoundEvent soundevent = entity instanceof Consumable.OverrideConsumeSound consumable$overrideconsumesound
                ? consumable$overrideconsumesound.getConsumeSound(stack)
                : this.sound.value();
        entity.playSound(soundevent, f4, f5);
    }

    public boolean shouldEmitParticlesAndSounds(int remainingUseDuration) {
        int i = this.drinkTicks() - remainingUseDuration;
        int j = (int)(this.drinkTicks() * 0.21875F);
        boolean flag = i > j;
        return flag && remainingUseDuration % 4 == 0;
    }


    public static Drinkable.Builder builder(int hydration) {
        return new Drinkable.Builder(hydration);
    }

    public static class Builder {
        private float consumeSeconds = 1.6F;
        private float saturation = 0.0F;
        private final int hydration;
        private ItemUseAnimation animation = ItemUseAnimation.EAT;
        private Holder<SoundEvent> sound = SoundEvents.GENERIC_DRINK;
        private boolean hasConsumeParticles = false;
        private final List<ConsumeEffect> onConsumeEffects = new ArrayList<>();
        private boolean doBurp = false;

        Builder(int hydration) {
            this.hydration = hydration;
        }

        public Drinkable.Builder drinkSeconds(float consumeSeconds) {
            this.consumeSeconds = consumeSeconds;
            return this;
        }

        public Drinkable.Builder saturation(float saturation) {
            this.saturation = saturation;
            return this;
        }

        public Drinkable.Builder doBurp(){
            this.doBurp = true;
            return this;
        }
        public Drinkable.Builder animation(ItemUseAnimation animation) {
            this.animation = animation;
            return this;
        }

        public Drinkable.Builder sound(Holder<SoundEvent> sound) {
            this.sound = sound;
            return this;
        }

        public Drinkable.Builder soundAfterConsume(Holder<SoundEvent> consumptionSound) {
            return this.onConsume(new PlaySoundConsumeEffect(consumptionSound));
        }


        public Drinkable.Builder hasConsumeParticles(boolean hasConsumeParticles) {
            this.hasConsumeParticles = hasConsumeParticles;
            return this;
        }
        public Drinkable.Builder hasConsumeParticles() {
            this.hasConsumeParticles = true;
            return this;
        }

        public Drinkable.Builder onConsume(ConsumeEffect effect) {
            this.onConsumeEffects.add(effect);
            return this;
        }

        public Drinkable build() {
            return new Drinkable(this.consumeSeconds, this.hydration,this.saturation,this.animation, this.sound, this.hasConsumeParticles,this.doBurp, this.onConsumeEffects);
        }
    }
}
