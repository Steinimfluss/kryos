package net.kryos.mixin.level.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;

@Mixin(ClientLevel.class)
public interface ClientLevelAccessor {
    @Accessor("blockStatePredictionHandler")
    BlockStatePredictionHandler getHandler();
}