package net.kryos.event.impl.entity;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.entity.RemoveEntityListener;

public class RemoveEntityEvent extends Event<RemoveEntityListener> {
	@Override
	public void post(RemoveEntityListener listener) {
		listener.remove(this);
	}

	@Override
	public Class<RemoveEntityListener> getListenerType() {
		return RemoveEntityListener.class;
	}
}