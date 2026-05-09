package net.kryos.event.impl.entity;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.entity.SetEntityMotionListener;
import net.minecraft.world.phys.Vec3;

public class SetEntityMotionEvent extends Event<SetEntityMotionListener> {
	private final int id;
	private Vec3 movement;
	
	public SetEntityMotionEvent(int id, Vec3 movement) {
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
	public void post(SetEntityMotionListener listener) {
		listener.setMotion(this);
	}

	@Override
	public Class<SetEntityMotionListener> getListenerType() {
		return SetEntityMotionListener.class;
	}
}