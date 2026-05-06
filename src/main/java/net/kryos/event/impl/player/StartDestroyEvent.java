package net.kryos.event.impl.player;

import net.kryos.event.CancellableEvent;
import net.kryos.event.listener.impl.player.StartDestroyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class StartDestroyEvent extends CancellableEvent<StartDestroyListener> {
	private BlockPos pos;
	private Direction direction;
	
	public StartDestroyEvent(BlockPos pos, Direction direction) {
		this.pos = pos;
		this.direction = direction;
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