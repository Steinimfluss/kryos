package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.SwapSlotListener;

public class SwapSlotEvent extends Event<SwapSlotListener> {
	private int desired;
	private boolean cancelled;

	public SwapSlotEvent(int desired) {
		this.desired = desired;
	}

	public int getDesired() {
		return desired;
	}

	public void setDesired(int desired) {
		this.desired = desired;
	}

	@Override
	public void post(SwapSlotListener listener) {
		listener.swapSlot(this);
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public void cancel() {
		this.cancelled = true;
	}

	@Override
	public Class<SwapSlotListener> getListenerType() {
		return SwapSlotListener.class;
	}
}