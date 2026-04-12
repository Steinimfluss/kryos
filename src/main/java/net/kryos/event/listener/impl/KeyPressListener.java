package net.kryos.event.listener.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.EventListener;

public interface KeyPressListener extends EventListener {
	void onKeyPress(Event event);
}