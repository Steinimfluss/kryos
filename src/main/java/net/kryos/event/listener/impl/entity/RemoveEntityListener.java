package net.kryos.event.listener.impl.entity;

import net.kryos.event.impl.entity.RemoveEntityEvent;
import net.kryos.event.listener.EventListener;

public interface RemoveEntityListener extends EventListener {
	void remove(RemoveEntityEvent event);
}