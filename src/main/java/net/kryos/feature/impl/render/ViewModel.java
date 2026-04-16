package net.kryos.feature.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.RenderArmWithItemEvent;
import net.kryos.event.listener.impl.RenderArmWithItemListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;

public class ViewModel extends Feature implements RenderArmWithItemListener {
	private NumberSetting<Float> xOffset = new NumberSetting<Float>("X Offset", 0F, -5F, 5F, 0.01F);
	private NumberSetting<Float> yOffset = new NumberSetting<Float>("Y Offset", 0F, -5F, 5F, 0.01F);
	private NumberSetting<Float> zOffset = new NumberSetting<Float>("Z Offset", 0F, -5F, 5F, 0.01F);

	private NumberSetting<Float> xScale = new NumberSetting<Float>("X Scale", 1F, 0F, 5F, 0.01F);
	private NumberSetting<Float> yScale = new NumberSetting<Float>("Y Scale", 1F, 0F, 5F, 0.01F);
	private NumberSetting<Float> zScale = new NumberSetting<Float>("Z Scale", 1F, 0F, 5F, 0.01F);
	
	public ViewModel() {
		super("ViewModel", FeatureCategory.RENDER);
		setSettings(xOffset, yOffset, zOffset,
				xScale, yScale, zScale);
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
	public void onArmRender(RenderArmWithItemEvent event) {
		PoseStack poseStack = event.getPoseStack();
		poseStack.translate(xOffset.getValue(), yOffset.getValue(), zOffset.getValue());;
		poseStack.scale(xScale.getValue(), yScale.getValue(), zScale.getValue());
	}
}