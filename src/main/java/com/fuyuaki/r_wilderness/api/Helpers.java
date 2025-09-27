package com.fuyuaki.r_wilderness.api;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class Helpers {

    public static final Direction[] DIRECTIONS = Direction.values();
    public static final DyeColor[] DYE_COLORS = DyeColor.values();
    public static final DyeColor[] DYE_COLORS_NOT_WHITE = Arrays.stream(DYE_COLORS).filter(e -> e != DyeColor.WHITE).toArray(DyeColor[]::new);

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int PRIME_X = 501125321;
    private static final int PRIME_Y = 1136930381;
    /**
     * @return A {@link ResourceLocation} with the {@code minecraft} namespace.
     */
    public static ResourceLocation identifierMC(String name)
    {
        return resourceLocation("minecraft", name);
    }

    /**
     * @return A {@link ResourceLocation} with an inferred namespace. If present, the namespace will be used, otherwise
     * {@code minecraft} will be used.
     */
    public static ResourceLocation resourceLocation(String name)
    {
        return ResourceLocation.parse(name);
    }
    public static ResourceLocation resourceLocation(String domain, String path)
    {
        return ResourceLocation.fromNamespaceAndPath(domain, path);
    }

// Math Functions
    // Some are duplicated from Mth, but kept here as they might have slightly different parameter order or names

    /**
     * Linearly interpolates between [min, max].
     */
    public static float lerp(float delta, float min, float max)
    {
        return min + (max - min) * delta;
    }

    public static double lerp(double delta, double min, double max)
    {
        return min + (max - min) * delta;
    }

    /**
     * Linearly interpolates between four values on a unit square.
     */
    public static float lerp4(float value00, float value01, float value10, float value11, float delta0, float delta1)
    {
        final float value0 = lerp(delta1, value00, value01);
        final float value1 = lerp(delta1, value10, value11);
        return lerp(delta0, value0, value1);
    }

    /**
     * @return A t = inverseLerp(value, min, max) s.t. lerp(t, min, max) = value;
     */
    public static float inverseLerp(float value, float min, float max)
    {
        return (value - min) / (max - min);
    }

    public static int hash(long salt, BlockPos pos)
    {
        return hash(salt, pos.getX(), pos.getY(), pos.getZ());
    }

    public static int hash(long salt, int x, int y, int z)
    {
        long hash = salt ^ ((long) x * PRIME_X) ^ ((long) y * PRIME_Y) ^ z;
        hash *= 0x27d4eb2d;
        return (int) hash;
    }

    public static RandomSource fork(RandomSource random)
    {
        return new XoroshiroRandomSource(random.nextLong(), random.nextLong());
    }

    /**
     * A triangle function, with input {@code value} and parameters {@code amplitude, midpoint, frequency}.
     * A period T = 1 / frequency, with a sinusoidal shape. triangle(0) = midpoint, with triangle(+/-1 / (4 * frequency)) = the first peak.
     */
    public static float triangle(float amplitude, float midpoint, float frequency, float value)
    {
        return midpoint + amplitude * (Math.abs(4f * frequency * value + 1f - 4f * Mth.floor(frequency * value + 0.75f)) - 1f);
    }

    public static double triangle(double amplitude, double midpoint, double frequency, double value)
    {
        return midpoint + amplitude * (Math.abs(4.0 * frequency * value + 1.0 - 4.0 * Mth.floor(frequency * value + 0.75)) - 1.0);
    }

    /**
     * @return A random integer, uniformly distributed in the range [min, max).
     */
    public static int uniform(RandomSource random, int min, int max)
    {
        return min == max ? min : min + random.nextInt(max - min);
    }

    public static int uniform(Random random, int min, int max)
    {
        return min == max ? min : min + random.nextInt(max - min);
    }

    /**
     * @return A random float, uniformly distributed in the range [min, max).
     */
    public static float uniform(RandomSource random, float min, float max)
    {
        return random.nextFloat() * (max - min) + min;
    }

    public static double uniform(RandomSource random, double min, double max)
    {
        return random.nextDouble() * (max - min) + min;
    }

    /**
     * @return A random float, distributed around [-1, 1] in a triangle distribution X ~ pdf(t) = 1 - |t|.
     */
    public static float triangle(RandomSource random)
    {
        return random.nextFloat() - random.nextFloat() * 0.5f;
    }

    /**
     * @return A random integer, distributed around (-range, range) in a triangle distribution X ~ pmf(t) ~= (1 - |t|)
     */
    public static int triangle(RandomSource random, int range)
    {
        return random.nextInt(range) - random.nextInt(range);
    }

    /**
     * @return A random float, distributed around [-delta, delta] in a triangle distribution X ~ pdf(t) ~= (1 - |t|)
     */
    public static float triangle(RandomSource random, float delta)
    {
        return (random.nextFloat() - random.nextFloat()) * delta;
    }

    public static double triangle(RandomSource random, double delta)
    {
        return (random.nextDouble() - random.nextDouble()) * delta;
    }

    public static float easeInOutCubic(float x)
    {
        return x < 0.5f ? 4 * x * x * x : 1 - cube(-2 * x + 2) / 2;
    }

    private static float cube(float x)
    {
        return x * x * x;
    }

    /**
     * Returns ceil(num / div)
     *
     * @see Math#floorDiv(int, int)
     */
    public static int ceilDiv(int num, int div)
    {
        return (num + div - 1) / div;
    }

    @Nullable
    @SuppressWarnings("DataFlowIssue") // BlockEntity.level is in practice never null, and the @Nullable C is not picked up correctly w.r.t getCapability()
    public static <T, C> T getCapability(BlockCapability<T, C> capability, BlockEntity entity, @Nullable C context)
    {
        return entity.getLevel().getCapability(capability, entity.getBlockPos(), entity.getBlockState(), entity, context);
    }
    public static <T> boolean mightHaveCapability(ItemStack stack, ItemCapability<T, Void> capability)
    {
        return stack.copyWithCount(1).getCapability(capability) != null;
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public static Level getUnsafeLevel(Object maybeLevel)
    {
        if (maybeLevel instanceof Level level)
        {
            return level; // Most obvious case, if we can directly cast up to level.
        }
        if (maybeLevel instanceof WorldGenRegion level)
        {
            return level.getLevel(); // Special case for world gen, when we can access the level unsafely
        }
        return null; // A modder has done a strange ass thing
    }
    public static boolean hasMoved(Entity entity)
    {
        return entity.xOld != entity.getX() && entity.zOld != entity.getZ();
    }

    public static void rotateEntity(Level level, Entity entity, Vec3 origin, float speed)
    {
        if (!entity.onGround() || entity.getDeltaMovement().y > 0 || speed == 0f)
        {
            return;
        }
        final float rot = (entity.getYHeadRot() + speed) % 360f;
        entity.setYRot(rot);
        if (level.isClientSide && entity instanceof Player)
        {
            final Vec3 offset = entity.position().subtract(origin).normalize();
            final Vec3 movement = new Vec3(-offset.z, 0, offset.x).scale(speed / 48f);
            entity.setDeltaMovement(entity.getDeltaMovement().add(movement));
            entity.hurtMarked = true; // resync movement
            return;
        }

        if (entity instanceof LivingEntity living)
        {
            entity.setYHeadRot(rot);
            entity.setYBodyRot(rot);
            entity.setOnGround(false);
            living.setNoActionTime(20);
            living.hurtMarked = true;
        }
    }

    public static BlockState copyProperties(BlockState copyTo, BlockState copyFrom)
    {
        for (Property<?> property : copyFrom.getProperties())
        {
            copyTo = copyProperty(copyTo, copyFrom, property);
        }
        return copyTo;
    }

    public static <T extends Comparable<T>> BlockState copyProperty(BlockState copyTo, BlockState copyFrom, Property<T> property)
    {
        return copyTo.hasProperty(property) ? copyTo.setValue(property, copyFrom.getValue(property)) : copyTo;
    }

    public static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, T value)
    {
        return state.hasProperty(property) ? state.setValue(property, value) : state;
    }


    /**
     * Damages {@code stack} by one point, when held by {@code entity} in {@code slot}
     */
    public static void damageItem(ItemStack stack, LivingEntity entity, EquipmentSlot slot)
    {
        stack.hurtAndBreak(1, entity, slot);
    }

    /**
     * Damages {@code stack} by {@code amount}, when held by {@code entity} in {@code hand}
     */
    public static void damageItem(ItemStack stack, int amount, LivingEntity entity, InteractionHand hand)
    {
        stack.hurtAndBreak(amount, entity, LivingEntity.getSlotForHand(hand));
    }

    /**
     * Damages {@code stack} by one point, when held by {@code entity} in {@code hand}
     */
    public static void damageItem(ItemStack stack, LivingEntity entity, InteractionHand hand)
    {
        stack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(hand));
    }

    /**
     * Damages {@code stack} without an entity present.
     */
    public static void damageItem(ItemStack stack, Level level)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            stack.hurtAndBreak(1, serverLevel, null, item -> {});
        }
    }

    /**
     * Damages {@code stack} without a level present. Note that this <strong>is not correct!</strong> as it doesn't account for enchantments,
     * but in this case it is the closest approximation we can do.
     *
     * @deprecated Prefer using any other overload than this
     */
    @Deprecated
    public static ItemStack damageItem(ItemStack stack)
    {
        if (stack.isDamageableItem())
        {
            final int amount = stack.getItem().damageItem(stack, 1, null, item -> {});
            final int damage = stack.getDamageValue() + amount;

            stack.setDamageValue(damage);
            if (damage >= stack.getMaxDamage())
            {
                stack.shrink(1);
            }
        }
        return stack;
    }


    /**
     * Rotates a VoxelShape 90 degrees. Assumes that the input facing is NORTH.
     */
    public static VoxelShape rotateShape(Direction direction, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return switch (direction)
        {
            case NORTH -> Block.box(x1, y1, z1, x2, y2, z2);
            case EAST -> Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
            case SOUTH -> Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
            case WEST -> Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
            default -> throw new IllegalArgumentException("Not horizontal!");
        };
    }

    /**
     * Follows indexes for Direction#get2DDataValue()
     */
    public static VoxelShape[] computeHorizontalShapes(Function<Direction, VoxelShape> shapeGetter)
    {
        return new VoxelShape[] {shapeGetter.apply(Direction.SOUTH), shapeGetter.apply(Direction.WEST), shapeGetter.apply(Direction.NORTH), shapeGetter.apply(Direction.EAST)};
    }

    public static boolean isItem(ItemStack stack, ItemLike item)
    {
        return stack.is(item.asItem());
    }

    public static boolean isItem(ItemStack stack, TagKey<Item> tag)
    {
        return stack.is(tag);
    }

    @SuppressWarnings("deprecation")
    public static boolean isItem(Item item, TagKey<Item> tag)
    {
        return item.builtInRegistryHolder().is(tag);
    }




    public static boolean isBlock(BlockState block, Block other)
    {
        return block.is(other);
    }

    public static boolean isBlock(BlockState state, TagKey<Block> tag)
    {
        return state.is(tag);
    }

    @SuppressWarnings("deprecation")
    public static boolean isBlock(Block block, TagKey<Block> tag)
    {
        return block.builtInRegistryHolder().is(tag);
    }

    public static boolean isFluid(FluidState state, TagKey<Fluid> tag)
    {
        return state.is(tag);
    }

    @SuppressWarnings("deprecation")
    public static boolean isFluid(Fluid fluid, TagKey<Fluid> tag)
    {
        return fluid.is(tag);
    }

    public static boolean isFluid(FluidState fluid, Fluid other)
    {
        return fluid.is(other);
    }
    public static boolean isEntity(Entity entity, TagKey<EntityType<?>> tag)
    {
        return isEntity(entity.getType(), tag);
    }

    public static boolean isEntity(EntityType<?> entity, TagKey<EntityType<?>> tag)
    {
        return entity.is(tag);
    }

}
