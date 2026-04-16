package net.kryos.event.impl;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.RenderArmWithItemListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class RenderArmWithItemEvent extends Event<RenderArmWithItemListener> {
    private AbstractClientPlayer player;
    private float frameInterp;
    private float xRot;
    private InteractionHand hand;
    private float attack;
    private ItemStack itemStack;
    private float inverseArmHeight;
    private PoseStack poseStack;
    private SubmitNodeCollector submitNodeCollector;
    private int lightCoords;
    
	public RenderArmWithItemEvent(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand,
			float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector, int lightCoords) {
		this.player = player;
		this.frameInterp = frameInterp;
		this.xRot = xRot;
		this.hand = hand;
		this.attack = attack;
		this.itemStack = itemStack;
		this.inverseArmHeight = inverseArmHeight;
		this.poseStack = poseStack;
		this.submitNodeCollector = submitNodeCollector;
		this.lightCoords = lightCoords;
	}

	@Override
	public void post(RenderArmWithItemListener listener) {
		listener.onArmRender(this);
	}
	
	@Override
	public Class<RenderArmWithItemListener> getListenerType() {
		return RenderArmWithItemListener.class;
	}

	public AbstractClientPlayer getPlayer() {
		return player;
	}

	public float getFrameInterp() {
		return frameInterp;
	}

	public float getxRot() {
		return xRot;
	}

	public InteractionHand getHand() {
		return hand;
	}

	public float getAttack() {
		return attack;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public float getInverseArmHeight() {
		return inverseArmHeight;
	}

	public PoseStack getPoseStack() {
		return poseStack;
	}

	public SubmitNodeCollector getSubmitNodeCollector() {
		return submitNodeCollector;
	}

	public int getLightCoords() {
		return lightCoords;
	}
}