package net.kryos.event.impl.player;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.player.SendRotationListener;

public class SendPositionEvent extends Event<SendRotationListener> {
	private double x, y, z;
	private float yaw, pitch;
	
	public SendPositionEvent(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public double getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
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