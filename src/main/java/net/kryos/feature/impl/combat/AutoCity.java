package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.StartDestroyEvent;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.util.BlockUtil;
import net.kryos.util.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AutoCity extends Feature implements PlayerTickListener {
	private NumberSetting<Float> reach = new NumberSetting<Float>("Reach", 4.0F, 0.0F, 10.0F, 0.5F);
	
	public AutoCity() {
		super("AutoCity", FeatureCategory.COMBAT);
		setSettings(reach);
	}

	@Override
	protected void onEnable() {
		if(!Kryos.featureManager.packetMine.isEnabled()) {setEnabled(false);return;}
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
	}

	@Override
	public void onPre(Pre event) {
		if(!Kryos.featureManager.packetMine.isEnabled()) {setEnabled(false);return;}
		BlockPos pos = getBestBlock();
		if(pos == null) return;
		
		Kryos.featureManager.packetMine.destroy(new StartDestroyEvent(pos, Direction.UP), true);
	}

	@Override
	public void onPost(Post event) {
		
	}
	
	public BlockPos getBestBlock() {
	    BlockPos best = null;
	    double bestSpeed = Double.MAX_VALUE;

	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (!(entity instanceof LivingEntity)) continue;
	        LivingEntity e = (LivingEntity) entity;

	        if (e == mc.player) continue;
	        if (e.isDeadOrDying()) continue;
	        if(entity == mc.player) continue;

	        switch (e.getType().getCategory()) {
	            case CREATURE:
	            case WATER_CREATURE:
	            case AMBIENT:
	            case MONSTER:
	            case MISC:
	                break;
	            default:
	                continue;
	        }
	        
	        int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
	        
	        for(int[] offset : offsets) {
	        	BlockPos pos = e.blockPosition().offset(offset[0], 0, offset[1]);
	        	if(mc.level.getBlockState(pos.below()).getBlock() != Blocks.OBSIDIAN && mc.level.getBlockState(pos.below()).getBlock() != Blocks.BEDROCK) continue;
	        	
	        	if(pos.distSqr(mc.player.blockPosition()) > reach.getValue() * reach.getValue()) continue;
	        	
	        	BlockState state = mc.level.getBlockState(pos);
	        	if(state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.BARRIER) continue;
	            double speed = BlockUtil.getDestroySpeed(InventoryUtil.findFastestTool(state, pos), state);
	            if (speed < bestSpeed) {best = pos; bestSpeed = speed;}
	        }
	    }

	    return best;
	}
}