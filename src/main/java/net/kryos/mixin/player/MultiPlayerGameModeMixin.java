package net.kryos.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.kryos.Kryos;
import net.kryos.event.impl.player.StartDestroyEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Inject(method = "startDestroyBlock", at = @At("Head"), cancellable = true)
	public void startDestroyBlock(final BlockPos pos, final Direction direction, CallbackInfoReturnable<Boolean> cir) {
		StartDestroyEvent event = new StartDestroyEvent(pos, direction);
		Kryos.eventBus.post(event);
		
		if(event.isCancelled())
			cir.setReturnValue(false);
	}
}