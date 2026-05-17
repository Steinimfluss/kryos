package net.kryos.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.entity.SetEntityMotionEvent;
import net.kryos.mixin.network.accessor.ClientboundSetEntityMotionPacketAccessor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(method = "handleSetEntityMotion", at = @At("HEAD"))
    private void handleSetEntityMotion(ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {
		SetEntityMotionEvent event = new SetEntityMotionEvent(packet.id(), packet.movement());
        Kryos.eventBus.post(event);
        ((ClientboundSetEntityMotionPacketAccessor)(Object)packet).setMovement(event.getMovement());
    }
}