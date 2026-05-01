package net.kryos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.impl.SendRotationEvent;
import net.kryos.rotation.RotationCorrection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	private static final Minecraft mc = Minecraft.getInstance();
	private float packet$lastYaw, packet$lastPitch;
	
    @Inject(method = "sendPosition", at = @At("HEAD"))
	private void sendPosition$HEAD(CallbackInfo ci) {
    	packet$lastYaw = mc.player.getYRot();
    	packet$lastPitch = mc.player.getXRot();
    	
    	SendRotationEvent event = new SendRotationEvent(packet$lastYaw, packet$lastPitch);
    	Kryos.eventBus.post(event);
    	
    	mc.player.setYRot(event.getYaw());
    	mc.player.setXRot(event.getPitch());
    }
    
    @Inject(method = "sendPosition", at = @At("TAIL"))
	private void sendPosition$TAIL(CallbackInfo ci) {
    	mc.player.setYRot(packet$lastYaw);
    	mc.player.setXRot(packet$lastPitch);
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
    
    private float pick$lastYaw;
    private float pick$oldYaw;

    private static RotationCorrection getRotationCorrection() {
    	return RotationCorrection.getFromString(Kryos.featureManager.rotationFeature.rotationCorrection.getValue().getName());
    }
    
    @Inject(method = "pick", at = @At("HEAD"))
    private static void pick$HEAD(
            Entity cameraEntity,
            double blockInteractionRange,
            double entityInteractionRange,
            float partialTicks,
            CallbackInfoReturnable<HitResult> cir
    ) {
    	if(Kryos.rotationBus.lastRotating && getRotationCorrection() == RotationCorrection.STRICT) {
	        cameraEntity.setYRot(cameraEntity.getYRot());
	        cameraEntity.setXRot(cameraEntity.getXRot());
	
	        LocalPlayerMixin mixin = (LocalPlayerMixin)(Object) cameraEntity;
	        mixin.pick$lastYaw = cameraEntity.getYRot();
	        mixin.pick$oldYaw = cameraEntity.getXRot();
	
	        cameraEntity.setYRot(Kryos.rotationBus.yaw);
	        cameraEntity.setXRot(Kryos.rotationBus.pitch);
    	}
    }

    @Inject(method = "pick", at = @At("RETURN"))
    private static void pick$RETURN(
            Entity cameraEntity,
            double blockInteractionRange,
            double entityInteractionRange,
            float partialTicks,
            CallbackInfoReturnable<HitResult> cir
    ) {
        if (Kryos.rotationBus.lastRotating && getRotationCorrection() == RotationCorrection.STRICT) {
            LocalPlayerMixin mixin = (LocalPlayerMixin)(Object) cameraEntity;
            cameraEntity.setYRot(mixin.pick$lastYaw);
            cameraEntity.setXRot(mixin.pick$oldYaw);
        }
    }
}