package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.CameraBobListener;

public class CameraBobEvent extends Event<CameraBobListener> {
	private boolean cancelled;
	
	public void cancel() {
		cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void post(CameraBobListener listener) {
		listener.bobCamera(this);
	}

	@Override
	public Class<CameraBobListener> getListenerType() {
		return CameraBobListener.class;
	}
}