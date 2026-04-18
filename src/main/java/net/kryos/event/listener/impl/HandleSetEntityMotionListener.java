package net.kryos.event.listener.impl;

import net.kryos.event.impl.HandleSetEntityMotionEvent;
import net.kryos.event.listener.EventListener;

public interface HandleSetEntityMotionListener extends EventListener {
	void setMotion(HandleSetEntityMotionEvent event);
}