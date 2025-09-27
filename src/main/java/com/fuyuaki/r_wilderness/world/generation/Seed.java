package com.fuyuaki.r_wilderness.world.generation;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import javax.annotation.Nullable;

public class Seed {

    /**
     * @return A new {@code Seed} that is capable of generating sequential {@code next()} values.
     */
    public static Seed of(long levelSeed)
    {
        return new Seed(levelSeed, new XoroshiroRandomSource(levelSeed));
    }

    /**
     * @return A new {@code Seed} that is only capable of generating level-seed dependent structures,
     * typically for use during world generation.
     */
    public static Seed unsafeOf(long levelSeed)
    {
        return new Seed(levelSeed, null);
    }

    private final long seed;
    private final @Nullable XoroshiroRandomSource next;

    private Seed(long seed, @Nullable XoroshiroRandomSource next)
    {
        this.seed = seed;
        this.next = next;
    }

    /**
     * @return The original level seed.
     */
    public long seed()
    {
        return seed;
    }

    /**
     * @return A new, sequentially generated seed.
     */
    public long next()
    {
        assert next != null : "Unsafe to use next() in this context";
        return next.nextLong();
    }

    /**
     * @return A new {@link RandomSource}, forked from the current sequentially generated seed.
     */
    public RandomSource fork()
    {
        return new XoroshiroRandomSource(next(), next());
    }

    /**
     * @return A new {@link Seed}, which is populated exactly as this one was initially.
     */
    public Seed forkStable()
    {
        return of(seed);
    }
}
