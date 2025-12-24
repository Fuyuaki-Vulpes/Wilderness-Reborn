package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.generation.ChunkGeneratorExtension;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(Player.class)
public class PlayerMixin {
    @Shadow public int experienceLevel;

    @Inject(method = "getXpNeededForNextLevel", at = @At(value = "HEAD"), cancellable = true)
    private void getXpNeededForNextLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(100);
    }
}
