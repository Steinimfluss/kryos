package net.kryos.event.listener.impl.player;

import net.kryos.event.impl.player.SendPositionEvent;
import net.kryos.event.listener.EventListener;

public interface SendRotationListener extends EventListener {
	void sendRotation(SendPositionEvent event);
}