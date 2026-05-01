package net.kryos.feature.impl.world;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.impl.RenderLevelEvent;
import net.kryos.event.impl.StartDestroyEvent;
import net.kryos.event.impl.SwapSlotEvent;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.event.listener.impl.RenderLevelListener;
import net.kryos.event.listener.impl.StartDestroyListener;
import net.kryos.event.listener.impl.SwapSlotListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.BooleanSettingBuilder;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.ModeSettingBuilder;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.NumberSettingBuilder;
import net.kryos.mixin.ClientLevelAccessor;
import net.kryos.mixin.MultiPlayerGameModeAccessor;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.kryos.util.BlockUtil;
import net.kryos.util.InventoryUtil;
import net.kryos.util.LevelRenderUtil;
import net.kryos.util.RotationUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PacketMine extends Feature implements Rotator, StartDestroyListener, PlayerTickListener, RenderLevelListener, SwapSlotListener {
    private final NumberSetting<Float> maxDistance = new NumberSettingBuilder<Float>()
    		.name("Max Distance")
    		.value(4F)
    		.min(0F)
    		.max(10F)
    		.step(0.1F)
    		.build();
    
    private final NumberSetting<Float> rotateStart = new NumberSettingBuilder<Float>()
    		.name("Start Rotate")
    		.value(1F)
    		.min(0F)
    		.max(2F)
    		.step(0.05F)
    		.build();
    
    private final BooleanSetting rotate = new BooleanSettingBuilder()
    		.name("Rotate")
    		.value(false)
    		.setting(rotateStart)
    		.build();
    
    private final NumberSetting<Float> destroyStop = new NumberSettingBuilder<Float>()
    		.name("Destroy Stop")
    		.value(1F)
    		.min(1F)
    		.max(2F)
    		.step(0.05F)
    		.build();
    
    private final BooleanSetting swing = new BooleanSettingBuilder()
    		.name("Swing")
    		.value(true)
    		.build();
    
    private final BooleanSetting swap = new BooleanSettingBuilder()
    		.name("Swap Back")
    		.value(true)
    		.build();
    
    private final ModeSetting swapMode = new ModeSettingBuilder()
    		.name("Swap Type")
    		.mode("Inventory")
    		.mode("Verbose")
    		.mode("None")
    		.value("Inventory")
    		.build();
    
    private final ModeSetting hotbarMode = new ModeSettingBuilder()
    		.name("Hotbar Mode")
    		.mode("Abort")
    		.mode("Prevent")
    		.mode("Ignore")
    		.value("Abort")
    		.build();
    
    private int desiredSlot;
    
    private int startTick;
    public BlockPos breakPos;
    public BlockState breakState;
    public Direction breakDir;
    public boolean mining;
    
    private boolean swapBack;
    private int swapSlot;

    public PacketMine() {
        super("PacketMine", FeatureCategory.WORLD);
        
        addSettings(maxDistance, destroyStop, rotate, swing, swap, swapMode, hotbarMode);
    }

    @Override
    protected void onEnable() {
        mining = false;
        
        Kryos.eventBus.subscribe(this);
        Kryos.rotationBus.unsubscribe(this);
    }

    @Override
    protected void onDisable() {
        Kryos.eventBus.unsubscribe(this);
        Kryos.rotationBus.unsubscribe(this);
    }

    public void destroy(StartDestroyEvent event) {
    	if (mc.player.gameMode() != GameType.SURVIVAL) return;

        BlockStatePredictionHandler handler =
            ((ClientLevelAccessor) mc.level).kryos$getBlockStatePredictionHandler();

        if (mining) {
        	if(event.getPos().equals(breakPos)) {
        		event.cancel();
        		return;
        	}
        	
        	abort();
        }

        mining = true;
        startTick = mc.player.tickCount;
        breakPos = event.getPos();
        breakState = mc.level.getBlockState(breakPos);
        breakDir = event.getDirection();
        
        ItemStack bestTool = InventoryUtil.findFastestTool(breakState, breakPos);
        int bestSlot = InventoryUtil.getSlotFromStack(bestTool);
        int oldSlot = mc.player.getInventory().getSelectedSlot();

        if(swapMode.getValue().getName().equalsIgnoreCase("Verbose") || swapMode.getValue().getName().equalsIgnoreCase("Inventory")) {
	        desiredSlot = bestSlot;
	        mc.player.getInventory().setSelectedSlot(bestSlot);
	        mc.getConnection().send(new ServerboundSetCarriedItemPacket(bestSlot));
        }
        
    	swapSlot = mc.player.getInventory().getSelectedSlot();
    	
        try (var prediction = handler.startPredicting()) {
            int sequence = prediction.currentSequence();

            mc.getConnection().send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                event.getPos(),
                event.getDirection(),
                sequence
            ));
        }
        
        if(swapMode.getValue().getName().equalsIgnoreCase("Inventory")) {
	        int oldContainerSlot = oldSlot;
	        int newContainerSlot = 36 + bestSlot;
	
	        mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, newContainerSlot, oldContainerSlot, ContainerInput.SWAP, mc.player);
        }
        
        if(swing.enabled) {
        	mc.player.swing(InteractionHand.MAIN_HAND);
        }
        
        event.cancel();
    }
    
    public void abort() {
        mc.getConnection().send(new ServerboundPlayerActionPacket(
            ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK,
            breakPos,
            breakDir
        ));

        MultiPlayerGameModeAccessor acc = (MultiPlayerGameModeAccessor) mc.gameMode;

        acc.kryos$setIsDestroying(false);
        acc.kryos$setDestroyProgress(0f);
        mc.level.destroyBlockProgress(mc.player.getId(), breakPos, -1);
        mc.player.resetAttackStrengthTicker();

        mining = false;
    }

    @Override
    public void startDestroy(StartDestroyEvent event) {
        destroy(event);
    }

    @Override
    public void onPre(Pre event) {
		setSuffix(swapMode.getValue().getName());
        Kryos.rotationBus.unsubscribe(this);
        
    	if(mining) {
    		if(!breakPos.closerThan(mc.player.blockPosition(), maxDistance.getValue())) {
    			abort();
    			return;
    		}
    		
            double breakDelta = BlockUtil.getBreakDelta(InventoryUtil.findFastestTool(breakState, breakPos), breakState, breakPos);
            double progress = breakDelta * ((mc.player.tickCount - startTick) + 1);

            if(progress >= rotateStart.getValue()) {
	            Kryos.rotationBus.subscribe(this);
	            float[] rot = RotationUtil.getRotationsTo(breakPos, breakDir);
	            Kryos.rotationBus.rotate(rot[0], rot[1], this);
            }
            
            if(progress >= destroyStop.getValue()) {
                ItemStack bestTool = InventoryUtil.findFastestTool(breakState, breakPos);
                int bestSlot = InventoryUtil.getSlotFromStack(bestTool);

                if(swap.enabled) {
                	swapBack = true;
                }
                
                if(swapMode.getValue().getName().equalsIgnoreCase("Verbose") || swapMode.getValue().getName().equalsIgnoreCase("Inventory")) {
                    desiredSlot = bestSlot;
                    mc.player.getInventory().setSelectedSlot(bestSlot);
                    mc.getConnection().send(new ServerboundSetCarriedItemPacket(bestSlot));
                }

                BlockStatePredictionHandler handler =
                        ((ClientLevelAccessor) mc.level).kryos$getBlockStatePredictionHandler();
                
            	try (var prediction = handler.startPredicting()) {
                    int sequence = prediction.currentSequence();
        	        mc.getConnection().send(new ServerboundPlayerActionPacket(
        	            ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                        breakPos,
                        breakDir,
        	            sequence
        	        ));
                }
            }
            
    		if(mc.level.getBlockState(breakPos) != breakState) {
    			mining = false;
    			
    			if(swapBack) {
    				swapBack = false;
                    desiredSlot = swapSlot;
    				mc.player.getInventory().setSelectedSlot(swapSlot);
                    mc.getConnection().send(new ServerboundSetCarriedItemPacket(swapSlot));
    			}
    		}
    	}
    }

	@Override
	public void swapSlot(SwapSlotEvent event) {
		if(mining && event.getDesired() != desiredSlot) {
			if(hotbarMode.getValue().getName().equalsIgnoreCase("Abort")) {
				abort();
			} else if(hotbarMode.getValue().getName().equalsIgnoreCase("Prevent")) {
				event.cancel();
			}
		}
	}

    @Override
    public void onPost(Post event) {
    	
    }

    @Override
    public RotationPrivilege getRotationPrivilege() {
        return RotationPrivilege.HIGHEST;
    }

    @Override
    public void renderLevel(RenderLevelEvent event) {
        if (!mining || breakPos == null || breakState == null || breakDir == null) return;

        BlockState breakState = mc.level.getBlockState(breakPos);
        ItemStack bestTool = InventoryUtil.findFastestTool(breakState, breakPos);
        int bestSlot = InventoryUtil.getSlotFromStack(bestTool);
        if (bestSlot == -1) return;

        double breakDelta = BlockUtil.getBreakDelta(bestTool, breakState, breakPos);
        double rawProgress = breakDelta * ((mc.player.tickCount - startTick) + 1);
        double clamped = Mth.clamp(rawProgress, 0.0, 1.0);

        double size = clamped;
        double half = size / 2.0;

        double cx = breakPos.getX() + 0.5;
        double cy = breakPos.getY() + 0.5;
        double cz = breakPos.getZ() + 0.5;

        AABB box = new AABB(
            cx - half, cy - half, cz - half,
            cx + half, cy + half, cz + half
        );

        int colorProgress = (int) Mth.clamp(clamped * 255.0, 0.0, 255.0);
        Color color = new Color(255 - colorProgress, colorProgress, 0, 100);

        Camera camera = mc.gameRenderer.getMainCamera();
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().mul(event.getModelViewMatrix());
        poseStack.translate(
            -camera.position().x,
            -camera.position().y,
            -camera.position().z
        );
        
        poseStack.pushPose();
		LevelRenderUtil.drawFilledBox(poseStack, box, color);
		poseStack.popPose();
    }
}
