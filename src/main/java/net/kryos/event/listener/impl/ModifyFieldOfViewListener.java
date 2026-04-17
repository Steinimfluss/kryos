package net.kryos.event.listener.impl;

import net.kryos.event.impl.ModifyFieldOfViewEvent;
import net.kryos.event.listener.EventListener;

public interface ModifyFieldOfViewListener extends EventListener {
	void modifyFOV(ModifyFieldOfViewEvent event);
}