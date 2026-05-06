package net.kryos.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.player.PlayerTickEvent;
import net.kryos.event.impl.player.SendPositionEvent;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	private static final Minecraft mc = Minecraft.getInstance();
	private double packet$lastX, packet$lastY, packet$lastZ;
	private float packet$lastYaw, packet$lastPitch;
	
    @Inject(method = "sendPosition", at = @At("HEAD"))
	private void kryos$sendPosition$HEAD(CallbackInfo ci) {
    	packet$lastYaw = mc.player.getYRot();
    	packet$lastPitch = mc.player.getXRot();
    	packet$lastX = mc.player.position().x;
    	packet$lastY = mc.player.position().y;
    	packet$lastZ = mc.player.position().z;
    	
    	SendPositionEvent event = new SendPositionEvent(packet$lastX, packet$lastY, packet$lastZ, packet$lastYaw, packet$lastPitch);
    	Kryos.eventBus.post(event);
    	
    	mc.player.setYRot(event.getYaw());
    	mc.player.setXRot(event.getPitch());
    }
    
    @Inject(method = "sendPosition", at = @At("TAIL"))
	private void sendPosition$TAIL(CallbackInfo ci) {
    	mc.player.setYRot(packet$lastYaw);
    	mc.player.setXRot(packet$lastPitch);
    	mc.player.setPos(packet$lastX, packet$lastY, packet$lastZ);
    }

    @Inject(method = "tick", at = @At("HEAD"))
	public void tick$HEAD(CallbackInfo ci) {
		PlayerTickEvent.Pre event = new Pre();
		Kryos.eventBus.post(event);
	}
    
    @Inject(method = "tick", at = @At("TAIL"))
   	public void tick$TAIL(CallbackInfo ci) {
		PlayerTickEvent.Post event = new Post();
		Kryos.eventBus.post(event);
   	}
}