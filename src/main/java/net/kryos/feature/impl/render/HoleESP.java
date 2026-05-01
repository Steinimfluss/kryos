package net.kryos.feature.impl.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.RenderLevelEvent;
import net.kryos.event.listener.impl.RenderLevelListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.NumberSettingBuilder;
import net.kryos.util.LevelRenderUtil;
import net.kryos.util.Timer;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

public class HoleESP extends Feature implements RenderLevelListener {
	private Color semi = new Color(255, 0, 0, 100);
	private Color full = new Color(0, 255, 0, 100);
	
	private Timer cacheTimer = new Timer();
	private Map<BlockPos, Safety> cache = new HashMap<BlockPos, Safety>();
	
	private NumberSetting<Integer> scanRange = new NumberSettingBuilder<Integer>()
			.name("Scan Range")
			.value(8)
			.min(2)
			.max(24)
			.step(1)
			.build();
	
	private NumberSetting<Long> scanCooldown = new NumberSettingBuilder<Long>()
			.name("Scan Cooldown")
			.value(500L)
			.min(0L)
			.max(1000L)
			.step(50L)
			.build();
	
	public HoleESP() {
		super("HoleESP", FeatureCategory.RENDER);
		setSettings(scanRange, scanCooldown);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
	}
	
	@Override
	public void renderLevel(RenderLevelEvent event) {
		setSuffix("Cache: " + scanCooldown.getValue());
		if(cacheTimer.check(scanCooldown.getValue())) {
			scan();
			
			cacheTimer.reset();
		}

        Camera camera = mc.gameRenderer.getMainCamera();
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().mul(event.getModelViewMatrix());
        poseStack.translate(
            -camera.position().x,
            -camera.position().y,
            -camera.position().z
        );
		
		for(Map.Entry<BlockPos, Safety> entry : cache.entrySet()) {
			Color color = Color.white;
			
			switch(entry.getValue()) {
				case SEMI:
					color = semi;
					break;
				case FULL:
					color = full;
					break;
				case NONE:
					continue;
			}

			AABB aabb = new AABB(entry.getKey());
			AABB box = new AABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY + 0.05, aabb.maxZ);
			
			poseStack.pushPose();
			LevelRenderUtil.drawFilledBox(poseStack, box, color);
			poseStack.popPose();
		}
	}
	
	private void scan() {
		cache.clear();
		
		for(int i = -scanRange.getValue(); i < scanRange.getValue(); i++) {
			for(int j = -scanRange.getValue(); j < scanRange.getValue(); j++) {
				for(int k = -scanRange.getValue(); k < scanRange.getValue(); k++) {
					BlockPos pos = mc.player.blockPosition().offset(i, j, k);
					cache.put(pos, getPosSafety(pos));
				}
			}
		}
	}
	
	private Safety getPosSafety(BlockPos pos) {
	    if(mc.level.getBlockState(pos).getBlock() != Blocks.AIR) return Safety.NONE;
	    	
	    int[][] offsets = {
	        {1, 0, 0}, {-1, 0, 0},
	        {0, 0, 1}, {0, 0, -1},
	        {0, -1, 0}
	    };

	    boolean allBedrock = true;
	    boolean allSafeBlocks = true;
	    

	    for (int[] o : offsets) {
	        BlockPos p = pos.offset(o[0], o[1], o[2]);
	        var block = mc.level.getBlockState(p).getBlock();

	        if (block != Blocks.BEDROCK)
	            allBedrock = false;

	        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN)
	            allSafeBlocks = false;
	    }

	    if (allBedrock)
	        return Safety.FULL;

	    if (allSafeBlocks)
	        return Safety.SEMI;

	    return Safety.NONE;
	}

	
	static enum Safety {
		NONE,
		SEMI,
		FULL;
	}
}