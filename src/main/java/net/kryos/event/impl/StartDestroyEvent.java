package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.StartDestroyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class StartDestroyEvent extends Event<StartDestroyListener> {
	private BlockPos pos;
	private Direction direction;
	private boolean cancelled;
	
	public StartDestroyEvent(BlockPos pos, Direction direction) {
		this.pos = pos;
		this.direction = direction;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void cancel() {
		this.cancelled = true;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public BlockPos getPos() {
		return pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public void post(StartDestroyListener listener) {
		listener.startDestroy(this);
	}

	@Override
	public Class<StartDestroyListener> getListenerType() {
		return StartDestroyListener.class;
	}
}