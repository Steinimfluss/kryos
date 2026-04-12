package net.kryos.event;

import net.kryos.event.listener.EventListener;

public abstract class Event {
	public abstract void post(EventListener event);
	
	public abstract <T extends EventListener> Class<T> getListenerType();
}