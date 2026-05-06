package net.kryos.event.impl.player;

import net.kryos.event.CancellableEvent;
import net.kryos.event.listener.impl.player.SwapSlotListener;

public class SwapSlotEvent extends CancellableEvent<SwapSlotListener> {
	private int desired;

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

	@Override
	public Class<SwapSlotListener> getListenerType() {
		return SwapSlotListener.class;
	}
}