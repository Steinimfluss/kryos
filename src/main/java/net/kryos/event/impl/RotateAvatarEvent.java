package net.kryos.event.impl;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.RotateAvatarListener;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class RotateAvatarEvent extends Event<RotateAvatarListener> {
	private final AvatarRenderState state;
	private final PoseStack poseStack;
	private float bodyRot;
	private float entityScale;
	
	public RotateAvatarEvent(AvatarRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
		this.state = state;
		this.poseStack = poseStack;
		this.bodyRot = bodyRot;
		this.entityScale = entityScale;
	}

	@Override
	public void post(RotateAvatarListener listener) {
		listener.rotateAvatar(this);
	}

	@Override
	public Class<RotateAvatarListener> getListenerType() {
		return RotateAvatarListener.class;
	}

	public float getBodyRot() {
		return bodyRot;
	}

	public void setBodyRot(float bodyRot) {
		this.bodyRot = bodyRot;
	}

	public float getEntityScale() {
		return entityScale;
	}

	public void setEntityScale(float entityScale) {
		this.entityScale = entityScale;
	}

	public AvatarRenderState getState() {
		return state;
	}

	public PoseStack getPoseStack() {
		return poseStack;
	}
}