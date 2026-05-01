package net.kryos.event.listener.impl;

import net.kryos.event.impl.CameraBobEvent;
import net.kryos.event.listener.EventListener;

public interface CameraBobListener extends EventListener {
	void bobCamera(CameraBobEvent event);
}
