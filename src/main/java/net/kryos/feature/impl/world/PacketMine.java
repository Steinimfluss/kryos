package net.kryos.feature.impl.world;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.impl.RenderLevelEvent;
import net.kryos.event.impl.StartDestroyEvent;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.event.listener.impl.RenderLevelListener;
import net.kryos.event.listener.impl.StartDestroyListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.mixin.ClientLevelAccessor;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.kryos.util.BlockUtil;
import net.kryos.util.InventoryUtil;
import net.kryos.util.LevelRenderUtil;
import net.kryos.util.RotationUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PacketMine extends Feature implements Rotator, StartDestroyListener, PlayerTickListener, RenderLevelListener {
    private final NumberSetting<Float> maxDistance = new NumberSetting<>("Max Distance", 4.0F, 0F, 10F, 0.1F);
    private final NumberSetting<Integer> timeout = new NumberSetting<>("Timeout", 40, 0, 1000, 1);
    private final BooleanSetting rotate = new BooleanSetting("Rotate");

    private int startTick;
    public BlockPos breakPos;
    public BlockState breakState;
    public boolean mining;

    public PacketMine() {
        super("PacketMine", FeatureCategory.WORLD);
        setSettings(maxDistance, timeout, rotate);
    }

    @Override
    protected void onEnable() {
        breakPos = null;
        mining = false;
        
        Kryos.eventBus.subscribe(this);
        Kryos.rotationBus.unsubscribe(this);
    }

    @Override
    protected void onDisable() {
        Kryos.eventBus.unsubscribe(this);
        Kryos.rotationBus.unsubscribe(this);

        breakPos = null;
        mining = false;
    }

    public void destroy(StartDestroyEvent event, boolean swing) {
    	if (mc.player.gameMode() != GameType.SURVIVAL) return;

        BlockStatePredictionHandler handler =
            ((ClientLevelAccessor) mc.level).kryos$getBlockStatePredictionHandler();

        if (mining) {
        	event.cancel();
        	return;
        }
        
        Kryos.rotationBus.subscribe(this);
        float[] rot = RotationUtil.getRotationsTo(event.getPos().getCenter());
        if(!Kryos.rotationBus.rotate(rot[0], rot[1], this)) {
        	event.cancel();
        	return;
        }
        
        breakPos = event.getPos();
        breakState = mc.level.getBlockState(breakPos);
        mining = true;
        startTick = mc.player.tickCount;

        try (var prediction = handler.startPredicting()) {
            int sequence = prediction.currentSequence();

            mc.getConnection().send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                event.getPos(),
                event.getDirection(),
                sequence
            ));
        }
        
        try (var prediction = handler.startPredicting()) {
            int sequence = prediction.currentSequence();
	        mc.getConnection().send(new ServerboundPlayerActionPacket(
	            ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                event.getPos(),
                event.getDirection(),
	            sequence
	        ));
        }
        
        if(swing) {
        	mc.player.swing(InteractionHand.MAIN_HAND);
        }
        
        event.cancel();
    }
    
    @Override
    public void startDestroy(StartDestroyEvent event) {
        destroy(event, false);
    }

    @Override
    public void onPre(Pre event) {
        Kryos.rotationBus.unsubscribe(this);

    	if(mining) {
            double breakDelta = BlockUtil.getBreakDelta(mc.player.getActiveItem(), breakState, breakPos);
            double rawProgress = breakDelta * ((mc.player.tickCount - startTick) + 1);
            if(rawProgress > 1) {
                Kryos.rotationBus.subscribe(this);
                float[] rot = RotationUtil.getRotationsTo(breakPos.getCenter());
                Kryos.rotationBus.rotate(rot[0], rot[1], this);
            	mining = false;
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
        if (!mining) return;

        BlockState breakState = mc.level.getBlockState(breakPos);
        ItemStack bestStack = mc.player.getActiveItem();
        int bestSlot = InventoryUtil.getSlotFromStack(bestStack);
        if (bestSlot == -1) return;

        double breakDelta = BlockUtil.getBreakDelta(bestStack, breakState, breakPos);
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

	    poseStack.translate(cx, cy + 1, cz);
	
	    poseStack.mulPose(Axis.YP.rotationDegrees(-camera.yRot()));
	    poseStack.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
	
	    float scale = 0.025f;
	    poseStack.scale(-scale, -scale, scale);
	
	    String text = String.format("%.0f%%", clamped * 100);
	    mc.font.drawInBatch(
	        text,
	        -mc.font.width(text) / 2f,
	        0,
	        0xFFFFFFFF,
	        false,
	        poseStack.last().pose(),
	        mc.renderBuffers().bufferSource(),
	        Font.DisplayMode.NORMAL,
	        0,
	        15728880
	    );
	    poseStack.popPose();
	
		LevelRenderUtil.drawFilledBox(poseStack, box, color);
    }
}
