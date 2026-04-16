package net.kryos.event.impl;

import org.joml.Vector4f;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.ComputeFogColorListener;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;

public class ComputeFogColorEvent extends Event<ComputeFogColorListener> {
	private Camera camera;
	private float partialTicks;
	private ClientLevel level;
	private int renderDistance;
	private float darkenWorldAmount;
	private Vector4f dest;
	
	public ComputeFogColorEvent(Camera camera, float partialTicks, ClientLevel level, int renderDistance,
			float darkenWorldAmount, Vector4f dest) {
		this.camera = camera;
		this.partialTicks = partialTicks;
		this.level = level;
		this.renderDistance = renderDistance;
		this.darkenWorldAmount = darkenWorldAmount;
		this.dest = dest;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public void setPartialTicks(float partialTicks) {
		this.partialTicks = partialTicks;
	}

	public ClientLevel getLevel() {
		return level;
	}

	public void setLevel(ClientLevel level) {
		this.level = level;
	}

	public int getRenderDistance() {
		return renderDistance;
	}

	public void setRenderDistance(int renderDistance) {
		this.renderDistance = renderDistance;
	}

	public float getDarkenWorldAmount() {
		return darkenWorldAmount;
	}

	public void setDarkenWorldAmount(float darkenWorldAmount) {
		this.darkenWorldAmount = darkenWorldAmount;
	}

	public Vector4f getDest() {
		return dest;
	}

	public void setDest(Vector4f dest) {
		this.dest = dest;
	}

	@Override
	public void post(ComputeFogColorListener listener) {
		listener.computeFogColor(this);
	}

	@Override
	public Class<ComputeFogColorListener> getListenerType() {
		return ComputeFogColorListener.class;
	}
}