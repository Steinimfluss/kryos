package net.kryos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.KeyPressEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
	private void keyPress(final long handle, @KeyEvent.Action final int action, final KeyEvent event, CallbackInfo ci) {
    	Kryos.eventBus.post(new KeyPressEvent(handle, action, event));
    }
}