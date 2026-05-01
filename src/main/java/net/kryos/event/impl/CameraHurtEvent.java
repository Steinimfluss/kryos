package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.CameraHurtListener;

public class CameraHurtEvent extends Event<CameraHurtListener> {
	private boolean cancelled;
	
	public void cancel() {
		cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void post(CameraHurtListener listener) {
		listener.hurtCamera(this);
	}

	@Override
	public Class<CameraHurtListener> getListenerType() {
		return CameraHurtListener.class;
	}
}