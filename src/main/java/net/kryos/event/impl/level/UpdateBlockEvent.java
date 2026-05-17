package net.kryos.event.impl.level;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.level.UpdateBlockListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class UpdateBlockEvent extends Event<UpdateBlockListener> {
	private BlockPos pos;
	private BlockState blockState;
	private int updateFlag;
	
	public UpdateBlockEvent(BlockPos pos, BlockState blockState, int updateFlag) {
		this.pos = pos;
		this.blockState = blockState;
		this.updateFlag = updateFlag;
	}
	
	public BlockPos getPos() {
		return pos;
	}

	public BlockState getBlockState() {
		return blockState;
	}

	public int getUpdateFlag() {
		return updateFlag;
	}

	@Override
	public void post(UpdateBlockListener listener) {
		listener.update(this);
	}

	@Override
	public Class<UpdateBlockListener> getListenerType() {
		return UpdateBlockListener.class;
	}
}