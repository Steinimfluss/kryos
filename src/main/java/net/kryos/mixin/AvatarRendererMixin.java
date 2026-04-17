package net.kryos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.RotateAvatarEvent;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
    @Inject(method = "setupRotations", at = @At("HEAD"))
	public void setupRotations(final AvatarRenderState state, final PoseStack poseStack, final float bodyRot, final float entityScale, CallbackInfo ci) {
    	RotateAvatarEvent event = new RotateAvatarEvent(state, poseStack, bodyRot, entityScale);
    	Kryos.eventBus.post(event);
    }
}