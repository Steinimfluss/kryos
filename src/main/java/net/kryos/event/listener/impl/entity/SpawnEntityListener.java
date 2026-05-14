package net.kryos.event.listener.impl.entity;

import net.kryos.event.impl.entity.SpawnEntityEvent;
import net.kryos.event.listener.EventListener;

public interface SpawnEntityListener extends EventListener {
	void spawn(SpawnEntityEvent event);
}