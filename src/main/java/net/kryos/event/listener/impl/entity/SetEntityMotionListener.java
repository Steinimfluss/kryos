package net.kryos.event.listener.impl.entity;

import net.kryos.event.impl.entity.SetEntityMotionEvent;
import net.kryos.event.listener.EventListener;

public interface SetEntityMotionListener extends EventListener {
	void setMotion(SetEntityMotionEvent event);
}