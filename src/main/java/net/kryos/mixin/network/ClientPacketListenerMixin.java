package net.kryos.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.entity.RemoveEntityEvent;
import net.kryos.event.impl.entity.SetEntityMotionEvent;
import net.kryos.event.impl.entity.SpawnEntityEvent;
import net.kryos.mixin.network.accessor.ClientboundSetEntityMotionPacketAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.Entity;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	private static final Minecraft mc = Minecraft.getInstance();
	
	@Inject(method = "handleSetEntityMotion", at = @At("HEAD"))
    private void handleSetEntityMotion(ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {
		SetEntityMotionEvent event = new SetEntityMotionEvent(packet.id(), packet.movement());
        Kryos.eventBus.post(event);
        ((ClientboundSetEntityMotionPacketAccessor)(Object)packet).setMovement(event.getMovement());
    }

	@Inject(method = "handleAddEntity", at = @At("TAIL"))
	private void handleAddEntity$TAIL(ClientboundAddEntityPacket packet, CallbackInfo ci) {
	    Entity entity = mc.level.getEntity(packet.getId());
	    if (entity == null) return;

	    SpawnEntityEvent event = new SpawnEntityEvent(entity);
	    Kryos.eventBus.post(event);
	}
	
	@Inject(method = "handleRemoveEntities", at = @At("TAIL"))
	public void handleRemoveEntities$TAIL(final ClientboundRemoveEntitiesPacket packet, CallbackInfo ci) {
	    RemoveEntityEvent event = new RemoveEntityEvent();
	    Kryos.eventBus.post(event);
	}
}