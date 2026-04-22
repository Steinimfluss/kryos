package net.kryos.event.listener.impl;

import net.kryos.event.impl.RenderLevelEvent;
import net.kryos.event.listener.EventListener;

public interface RenderLevelListener extends EventListener {
	void renderLevel(RenderLevelEvent event);
}