package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.EventListener;
import net.kryos.event.listener.impl.KeyPressListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.KeyEvent.Action;

public class KeyPressEvent extends Event {
	private final long handle;
	private @KeyEvent.Action final int action;
	private final KeyEvent event;
	
	public KeyPressEvent(long handle, @Action int action, KeyEvent event) {
		this.handle = handle;
		this.action = action;
		this.event = event;
	}

	public long getHandle() {
		return handle;
	}

	public int getAction() {
		return action;
	}

	public KeyEvent getEvent() {
		return event;
	}

	@Override
	public void post(EventListener event) {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends EventListener> Class<T> getListenerType() {
	    return (Class<T>) KeyPressListener.class;
	}
}