package net.kryos.feature.impl.world;

import java.util.Optional;
import java.util.Set;

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
import net.kryos.setting.impl.EnumSetting;
import net.kryos.setting.impl.FloatSetting;
import net.kryos.util.entity.CrystalUtil;
import net.kryos.util.entity.DamageUtil;
import net.kryos.util.item.InventoryUtil;
import net.kryos.util.level.BlockUtil;
import net.kryos.util.math.RotationUtil;
import net.kryos.util.render.ColorUtil;
import net.kryos.util.render.LevelRenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class PacketMine extends LockingFeature implements PlayerTickListener, StartDestroyListener, ExtractLevelListener {
	private Setting<Float> reach = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("reach")
			.name("Reach")
			.min(1)
			.max(10)
			.step(0.05F)
			.description(Component.literal("Maximum distance at which destroying can occur"))
			.build());

	private Setting<Float> stopAt = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("stop_at")
			.name("Stop at")
			.min(1)
			.max(2)
			.step(0.05F)
			.description(Component.literal("The percentage at which the client marks the destroying as complete"))
			.build());
	
	private Setting<Boolean> rotate = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("rotate")
			.name("Rotate")
			.description(Component.literal("Rotates the local player towards the block"))
			.build());
	
	private Setting<Float> rotateAt = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("rotate_at")
			.name("Rotate at")
			.min(0)
			.max(2)
			.step(0.05F)
			.requirement(() -> rotate.getValue())
			.description(Component.literal("The destroy percentage at which a rotation should occur"))
			.build());

	private Setting<Float> timeout = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("timeout")
			.name("Timeout")
			.min(10)
			.max(1000)
			.step(10)
			.description(Component.literal("The time after which a destroy is cancelled"))
			.build());

	private Setting<Boolean> swap = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("swap")
			.name("Swap")
			.defaultValue(true)
			.description(Component.literal("Whether the client should swap to the best tool"))
			.build());

	private Setting<SwapMode> swapMode = addSetting(new EnumSetting.EnumSettingBuilder<SwapMode>()
			.id("swap_mode")
			.name("Swap mode")
			.defaultValue(SwapMode.HOTBAR)
			.requirement(() -> swap.getValue())
			.description(Component.literal("The mode with which swapping should occur"))
			.build());
	
	private Setting<Boolean> crystal = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("crystal")
			.name("Crystal")
			.defaultValue(true)
			.description(Component.literal("Places a crystal before packet mine completes"))
			.build());
	
	private Setting<Float> crystalAt = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("crystal_at")
			.name("Crystal at")
			.min(0)
			.max(1)
			.step(0.05F)
			.description(Component.literal("The percentage at which the client places a crystal"))
			.requirement(() -> crystal.getValue())
			.build());
	
	private Setting<Float> minCrystalDamage = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("min_crystal_damage")
			.name("Min crystal damage")
			.min(1)
			.max(20)
			.step(0.5F)
			.description(Component.literal("The minimum damage a crystal should do after destroying completed"))
			.requirement(() -> crystal.getValue())
			.build());
	
	// The current block being destroyed
	private Optional<DestroyBlock> destroyBlock;
	
	// The stack the player was holding before the initial swap
	private Optional<ItemStack> oldStack;
	
	// Updates the current slot on the next tick
	private boolean swapBack;
	
	public PacketMine() {
		super("packet_mine", "PacketMine", FeatureCategory.WORLD, Component.literal("Uses packets to mine blocks allowing for multi tasking while destroying"), LockPrivilege.HIGH);
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
    	Kryos.lockManager.free(this);
		
		super.onDisable();
	}

	@Override
	public void onPre(Pre event) {
		Kryos.lockManager.free(this);
		if(destroyBlock.isEmpty())
			return;
		
		DestroyBlock block = destroyBlock.get();
		block.tick();
		
		// Swap back
		if(swapBack && mc.level.getBlockState(block) != block.getState()) {
			swapBack = false;
			destroyBlock = Optional.empty();
			completeSwap(SwapMode.HOTBAR);
			return;
		}
		
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
		
		//Crystal
		if(block.getProgress() >= crystalAt.getValue()) {
			if(!Kryos.lockManager.acquire(this)) return;
			
			float[] rot = RotationUtil.getRotationsTo(block, Direction.UP);

			if(InventoryUtil.hasItemIn(Items.END_CRYSTAL, InteractionHand.OFF_HAND)) {
				crystalPos(block).ifPresent(b -> {
					if(rotate.getValue())
						Kryos.rotationManager.rotate(rot[0], rot[1]);
					
			        mc.gameMode.useItemOn(
			                mc.player,
			                InteractionHand.OFF_HAND,
			                new BlockHitResult(
			                        BlockUtil.getClosestPointOnFace(b, Direction.UP),
			                        Direction.UP,
			                        b,
			                        false
			                )
			        );
				});
			}
		}

		// Rotate
		if(block.getProgress() >= rotateAt.getValue()) {
			float[] rot = RotationUtil.getRotationsTo(block, block.getDir());

			if(!Kryos.lockManager.acquire(this)) return;
			
			if(rotate.getValue())
				Kryos.rotationManager.rotate(rot[0], rot[1]);
		}
		
		// Complete
		if(block.getProgress() >= stopAt.getValue()) {
			if(swap.getValue()) {
				// Swapping back should always occur in hotbar mode because its more stable and (most) anticheats only flag the initial swap
				beginSwap(block, SwapMode.HOTBAR);
				swapBack = true;
			}
			block.stop();
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
		
		// Swap
		ItemStack stack = beginSwap(block, swapMode.getValue());
		block.start(stack);
		completeSwap(swapMode.getValue());
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
	
	public Optional<BlockPos> crystalPos(BlockPos destroy) {
		int[][] offsets = {{1, -1, 0}, {-1, -1, 0}, {0, -1, 1}, {0, -1, -1}, {0, 0, 0}, {2, -1, 0}, {-2, -1, 0}, {0, -1, 2}, {0, -1, -2}};
		
		BlockPos bestPos = null;
		float bestDamage = 0;
		
		for(int[] offset : offsets) {
			BlockPos pos = destroy.offset(offset[0], offset[1], offset[2]);
			
			Block block = mc.level.getBlockState(pos).getBlock();
			
			if(block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) continue;
			
			BlockPos above = pos.above();
			Block aboveBlock = mc.level.getBlockState(above).getBlock();

			if(aboveBlock != Blocks.AIR) continue;

			if(CrystalUtil.placeIntercect(above)) continue;
			
			for(Entity entity : mc.level.entitiesForRendering()) {
				if(entity == mc.player) continue;
				
				if(!(entity instanceof LivingEntity living)) continue;
				
				float damage = DamageUtil.getCrystalDamage(living, living.position(), above.getBottomCenter(), Set.of(destroy));
				
				if(damage < minCrystalDamage.getValue()) continue;
				
				if(damage > bestDamage) {
					bestDamage = damage;
					bestPos = pos;
				}
			}
		}
		
		if(bestPos == null) return Optional.empty();
		
		return Optional.of(bestPos);
	}
	
	public ItemStack beginSwap(DestroyBlock block, SwapMode swapMode) {
		ItemStack stack = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
		
		if(swap.getValue()) {
			ItemStack oldStack = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
			stack = InventoryUtil.findFastestTool(block.getState(), block);
			InventoryUtil.getSlotWithStack(stack).ifPresent(slot -> {
				switch(swapMode) {
					case HOTBAR:
						InventoryUtil.slotUpdate(slot);
						break;
					case INVENTORY:
						mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, slot + 36, mc.player.getInventory().getSelectedSlot(), ContainerInput.SWAP, mc.player);
						this.oldStack = Optional.of(oldStack);
						break;
				}
			});
		}
		
		return stack;
	}
	
	public void completeSwap(SwapMode swapMode) {
		if(swap.getValue()) {
			switch(swapMode) {
				case HOTBAR:
					InventoryUtil.slotUpdate(mc.player.getInventory().getSelectedSlot());
					break;
				case INVENTORY:
					if(oldStack.isPresent()) {
						InventoryUtil.getSlotWithStack(oldStack.get()).ifPresent(slot -> {
							mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, slot + 36, mc.player.getInventory().getSelectedSlot(), ContainerInput.SWAP, mc.player);
						});
					}
					break;
			}
		}
	}
	
	public static class DestroyBlock extends BlockPos {
        private BlockStatePredictionHandler handler = ((ClientLevelAccessor) mc.level).getHandler();
        private ItemStack stack;
		private BlockState state;
		private Direction dir;
		
		private int start;
		private double progress;
		
		public DestroyBlock(BlockPos pos, Direction dir) {
			super(pos.getX(), pos.getY(), pos.getZ());
			
			this.dir = dir;
			this.state = mc.level.getBlockState(pos);
		}
		
		public void start(ItemStack stack) {
            predict(Action.START_DESTROY_BLOCK);
            start = mc.player.tickCount;
            this.stack = stack;
		}
		
		public void stop() {
			predict(Action.STOP_DESTROY_BLOCK);
		}
		
		public void abort() {
			predict(Action.ABORT_DESTROY_BLOCK);
		}
		
		public void tick() {
			progress = BlockUtil.getBreakDelta(stack, state, this) * ((mc.player.tickCount - start) + 1);
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
	
	static enum SwapMode {
		HOTBAR,
		INVENTORY;
	}
	
	static enum HotbarMode {
		ABORT,
		PREVENT,
		IGNORE;
	}
}