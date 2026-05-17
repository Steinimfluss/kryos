package net.kryos.event.listener.impl.level;

import net.kryos.event.impl.level.UpdateBlockEvent;
import net.kryos.event.listener.EventListener;

public interface UpdateBlockListener extends EventListener {
	void update(UpdateBlockEvent event);
}