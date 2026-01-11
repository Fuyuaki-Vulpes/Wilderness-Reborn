package com.fuyuaki.r_wilderness.network;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.common.RTags;
import com.fuyuaki.r_wilderness.world.environment.HydrationData;
import com.fuyuaki.r_wilderness.world.environment.ServerPlayerEnvironment;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DrinkWaterInWorldPacket(BlockPos position) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DrinkWaterInWorldPacket> TYPE = new CustomPacketPayload.Type<>(RWildernessMod.modLocation("drink_in_world"));

    public static final StreamCodec<ByteBuf, DrinkWaterInWorldPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            DrinkWaterInWorldPacket::position,
            DrinkWaterInWorldPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(DrinkWaterInWorldPacket packet, IPayloadContext context)
    {
        Player player = context.player();

            Level level = player.level();

            if (level.mayInteract(player, packet.position) && level.getFluidState(packet.position).is(FluidTags.WATER))
            {
                Holder<Biome> biome = level.getBiome(packet.position);

                if (biome.is(RTags.Biomes.HAS_SALT_WATER) && level.random.nextFloat() < HydrationData.THISRT_CHANCE)
                {
                    ((ServerPlayerEnvironment)player).drinkInWorld(4,0);

//                    player.addEffect(new MobEffectInstance(ModEffects.THIRST, 600));
                }else{
                    ((ServerPlayerEnvironment)player).drinkInWorld(8,8);
                }
            }
    }
}
