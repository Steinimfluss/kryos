package net.kryos.event.listener.impl;

import net.kryos.event.impl.SendRotationEvent;
import net.kryos.event.listener.EventListener;

public interface SendRotationListener extends EventListener {
	void sendRotation(SendRotationEvent event);
}