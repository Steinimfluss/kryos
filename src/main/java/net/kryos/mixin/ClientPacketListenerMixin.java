package net.kryos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.HandleSetEntityMotionEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(method = "handleSetEntityMotion", at = @At("HEAD"))
    private void handleSetEntityMotion(ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {
        HandleSetEntityMotionEvent event = new HandleSetEntityMotionEvent(packet.id(), packet.movement());
        Kryos.eventBus.post(event);
        ((ClientboundSetEntityMotionPacketAccessor)(Object)packet).setMovement(event.getMovement());
    }
}