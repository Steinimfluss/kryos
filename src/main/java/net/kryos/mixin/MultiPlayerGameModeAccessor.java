package net.kryos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {

    @Accessor("isDestroying")
    void kryos$setIsDestroying(boolean value);

    @Accessor("destroyProgress")
    void kryos$setDestroyProgress(float value);

    @Accessor("destroyBlockPos")
    BlockPos kryos$getDestroyBlockPos();
}
