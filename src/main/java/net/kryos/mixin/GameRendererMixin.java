package net.kryos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.CameraBobEvent;
import net.kryos.event.impl.CameraHurtEvent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void onBobHurt(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo ci) {
    	CameraHurtEvent event = new CameraHurtEvent();
    	Kryos.eventBus.post(event);
    	
    	if(event.isCancelled())
    		ci.cancel();
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo ci) {
    	CameraBobEvent event = new CameraBobEvent();
    	Kryos.eventBus.post(event);
    	
    	if(event.isCancelled())
    		ci.cancel();
    }
}