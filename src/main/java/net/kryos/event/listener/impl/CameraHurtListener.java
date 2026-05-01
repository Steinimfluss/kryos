package net.kryos.event.listener.impl;

import net.kryos.event.impl.CameraHurtEvent;
import net.kryos.event.listener.EventListener;

public interface CameraHurtListener extends EventListener {
	void hurtCamera(CameraHurtEvent event);
}