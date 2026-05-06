package net.kryos.event.listener.impl.render;

import net.kryos.event.impl.render.ExtractLevelEvent;
import net.kryos.event.listener.EventListener;

public interface ExtractLevelListener extends EventListener {
	void extractLevel(ExtractLevelEvent event);
}