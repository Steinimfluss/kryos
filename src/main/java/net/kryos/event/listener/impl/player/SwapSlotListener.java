package net.kryos.event.listener.impl.player;

import net.kryos.event.impl.player.SwapSlotEvent;
import net.kryos.event.listener.EventListener;

public interface SwapSlotListener extends EventListener {
	void swapSlot(SwapSlotEvent event);
}