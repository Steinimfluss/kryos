package net.kryos.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.player.SwapSlotEvent;
import net.minecraft.world.entity.player.Inventory;

@Mixin(Inventory.class)
public class InventoryMixin {
	@Inject(method = "setSelectedSlot", at = @At("Head"), cancellable = true)
	public void setSelectedSlot(final int selected, CallbackInfo ci) {
		SwapSlotEvent event = new SwapSlotEvent(selected);
		Kryos.eventBus.post(event);
		
		if(event.isCancelled()) {
			ci.cancel();
		}
	}
}