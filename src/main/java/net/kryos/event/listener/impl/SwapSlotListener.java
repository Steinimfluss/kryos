package net.kryos.event.listener.impl;

import net.kryos.event.impl.SwapSlotEvent;
import net.kryos.event.listener.EventListener;

public interface SwapSlotListener extends EventListener {
	void swapSlot(SwapSlotEvent event);
}