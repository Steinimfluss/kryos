package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.HandleSetEntityMotionListener;
import net.minecraft.world.phys.Vec3;

public class HandleSetEntityMotionEvent extends Event<HandleSetEntityMotionListener> {
	private final int id;
	private Vec3 movement;
	
	public HandleSetEntityMotionEvent(int id, Vec3 movement) {
		this.id = id;
		this.movement = movement;
	}

	public int getId() {
		return id;
	}

	public Vec3 getMovement() {
		return movement;
	}

	public void setMovement(Vec3 movement) {
		this.movement = movement;
	}

	@Override
	public void post(HandleSetEntityMotionListener listener) {
		listener.setMotion(this);
	}

	@Override
	public Class<HandleSetEntityMotionListener> getListenerType() {
		return HandleSetEntityMotionListener.class;
	}
}