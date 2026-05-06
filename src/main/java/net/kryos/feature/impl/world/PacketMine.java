package net.kryos.feature.impl.world;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.impl.player.StartDestroyEvent;
import net.kryos.event.impl.render.ExtractLevelEvent;
import net.kryos.event.listener.impl.player.PlayerTickListener;
import net.kryos.event.listener.impl.player.StartDestroyListener;
import net.kryos.event.listener.impl.render.ExtractLevelListener;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.LockingFeature;
import net.kryos.lock.LockPrivilege;
import net.kryos.mixin.level.accessor.ClientLevelAccessor;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.BooleanSetting;
import net.kryos.setting.impl.FloatSetting;
import net.kryos.util.level.BlockUtil;
import net.kryos.util.math.RotationUtil;
import net.kryos.util.render.ColorUtil;
import net.kryos.util.render.LevelRenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PacketMine extends LockingFeature implements PlayerTickListener, StartDestroyListener, ExtractLevelListener {
	private Setting<Float> reach = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("reach")
			.name("Reach")
			.min(1)
			.max(10)
			.step(0.05F)
			.build());
	
	private Setting<Float> stopAt = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("stop_at")
			.name("Stop at")
			.min(1)
			.max(2)
			.step(0.05F)
			.build());

	private Setting<Boolean> rotate = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("rotate")
			.name("Rotate")
			.build());
	
	private Setting<Float> rotateAt = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("rotate_at")
			.name("Rotate at")
			.min(0)
			.max(2)
			.step(0.05F)
			.requirement(() -> rotate.getValue())
			.build());
	
	private Setting<Float> timeout = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("timeout")
			.name("Timeout")
			.min(10)
			.max(1000)
			.step(10)
			.build());
	
	private Setting<Boolean> crystal = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("crystal")
			.name("Crystal")
			.defaultValue(false)
			.build());

	private Setting<Float> crystalAt = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("crystal_at")
			.name("Crystal at")
			.min(0)
			.max(2)
			.step(0.05F)
			.requirement(() -> crystal.getValue())
			.build());
	
	private Setting<Boolean> crystalIntercect = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("crystal_intercect")
			.name("Crystal intercect")
			.defaultValue(false)
			.requirement(() -> crystal.getValue())
			.build());

	private Setting<Float> minTargetDamage = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("min_target_damage")
			.name("Min target damage")
			.min(0)
			.max(100)
			.step(1)
			.defaultValue(5)
			.requirement(() -> crystal.getValue())
			.build());
	
	private Setting<Float> maxSelfDamage = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("max_self_damage")
			.name("Max self damage")
			.min(0)
			.max(100)
			.step(1)
			.defaultValue(5)
			.requirement(() -> crystal.getValue())
			.build());
	
	private Optional<DestroyBlock> destroyBlock;
	
	public PacketMine() {
		super("packet_mine", "PacketMine", FeatureCategory.WORLD, Optional.empty(), LockPrivilege.HIGH);
	}
	
	@Override
	protected void onEnable() {
		destroyBlock = Optional.empty();
		
		Kryos.eventBus.subscribe(this);
		
		super.onEnable();
	}
	
	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		
		super.onDisable();
	}

	@Override
	public void onPre(Pre event) {
		if(destroyBlock.isEmpty())
			return;
		
		DestroyBlock block = destroyBlock.get();
		block.tick();
		
		// Timeout
		if((mc.player.tickCount - block.getStart()) > timeout.getValue()) {
			block.abort();
			destroyBlock = Optional.empty();
			return;
		}
		
		// Out of range
		if(BlockUtil.getDistanceTo(block) > reach.getValue()) {
			block.abort();
			destroyBlock = Optional.empty();
			return;
		}

		// Rotate
		if(rotate.getValue() && block.getProgress() >= rotateAt.getValue()) {
			float[] rot = RotationUtil.getRotationsTo(block, block.getDir());
			Kryos.rotationManager.rotate(rot[0], rot[1]);
		}
		
		// Complete
		if(block.getProgress() >= stopAt.getValue()) {
			block.stop();
			destroyBlock = Optional.empty();
			return;
		}
	}
	
	@Override
	public void startDestroy(StartDestroyEvent event) {
		event.cancel();
		
		if(destroyBlock.isPresent()) {
			destroyBlock.get().abort();
		}
		
		DestroyBlock block = new DestroyBlock(event.getPos(), event.getDirection());
		
		destroyBlock = Optional.of(block);
		block.start();
	}

	@Override
	public void extractLevel(ExtractLevelEvent event) {
		if(destroyBlock.isEmpty()) return;
		
		DestroyBlock block = destroyBlock.get();
		
	    Camera camera = mc.gameRenderer.getMainCamera();
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().mul(event.getModelViewMatrix());
        poseStack.translate(
            -camera.position().x,
            -camera.position().y,
            -camera.position().z
        );
        
        poseStack.pushPose();
		LevelRenderUtil.drawFilledBox(poseStack, new AABB(block), ColorUtil.lerpColor(0x80FF0000, 0x8000FF00, block.getProgress()));
		poseStack.popPose();
	}
	
	@Override
	public void onPost(Post event) {
		
	}
	
	public static class DestroyBlock extends BlockPos {
        private BlockStatePredictionHandler handler = ((ClientLevelAccessor) mc.level).getHandler();
		private BlockState state;
		private Direction dir;
		
		private int start;
		private double progress;
		
		public DestroyBlock(BlockPos pos, Direction dir) {
			super(pos.getX(), pos.getY(), pos.getZ());
			
			this.dir = dir;
			this.state = mc.level.getBlockState(pos);
		}
		
		public void start() {
            predict(Action.START_DESTROY_BLOCK);
            start = mc.player.tickCount;
		}
		
		public void stop() {
			predict(Action.STOP_DESTROY_BLOCK);
		}
		
		public void abort() {
			predict(Action.ABORT_DESTROY_BLOCK);
		}
		
		public void tick() {
			progress = BlockUtil.getBreakDelta(mc.player.getItemInHand(InteractionHand.MAIN_HAND), state, this) * ((mc.player.tickCount - start) + 1);
		}
		
		public void predict(ServerboundPlayerActionPacket.Action action) {
			try (var prediction = handler.startPredicting()) {
                int sequence = prediction.currentSequence();
    	        mc.getConnection().send(new ServerboundPlayerActionPacket(
    	            action,
                    this,
                    dir,
    	            sequence
    	        ));
            }
		}

		public Direction getDir() {
			return dir;
		}

		public BlockStatePredictionHandler getHandler() {
			return handler;
		}

		public BlockState getState() {
			return state;
		}

		public int getStart() {
			return start;
		}

		public double getProgress() {
			return progress;
		}
	}
	
	record CrystalCandidate(BlockPos pos, float damage) {}
}