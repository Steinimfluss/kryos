package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.SendRotationListener;

public class SendRotationEvent extends Event<SendRotationListener> {
	private float yaw, pitch;
	
	public SendRotationEvent(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	@Override
	public void post(SendRotationListener listener) {
		listener.sendRotation(this);
	}

	@Override
	public Class<SendRotationListener> getListenerType() {
		return SendRotationListener.class;
	}
}