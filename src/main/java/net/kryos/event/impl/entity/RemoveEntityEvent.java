package net.kryos.event.impl.entity;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.entity.RemoveEntityListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;

public class RemoveEntityEvent extends Event<RemoveEntityListener> {
	private int id;
	private Entity.RemovalReason reason;
	
	public RemoveEntityEvent(int id, RemovalReason reason) {
		this.id = id;
		this.reason = reason;
	}
	
	public int getId() {
		return id;
	}

	public Entity.RemovalReason getReason() {
		return reason;
	}

	@Override
	public void post(RemoveEntityListener listener) {
		listener.remove(this);
	}

	@Override
	public Class<RemoveEntityListener> getListenerType() {
		return RemoveEntityListener.class;
	}
}