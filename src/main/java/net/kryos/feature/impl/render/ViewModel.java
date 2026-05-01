package net.kryos.feature.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.RenderArmWithItemEvent;
import net.kryos.event.listener.impl.RenderArmWithItemListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.NumberSettingBuilder;
import net.kryos.feature.setting.SplitterSetting;
import net.kryos.feature.setting.SplitterSettingBuilder;

public class ViewModel extends Feature implements RenderArmWithItemListener {
	private NumberSetting<Float> xOffset = new NumberSettingBuilder<Float>()
			.name("X")
			.value(0F)
			.min(-5F)
			.max(5F)
			.step(0.01F)
			.build();
	
	private NumberSetting<Float> yOffset = new NumberSettingBuilder<Float>()
			.name("Y")
			.value(0F)
			.min(-5F)
			.max(5F)
			.step(0.01F)
			.build();
	
	private NumberSetting<Float> zOffset = new NumberSettingBuilder<Float>()
			.name("Z")
			.value(0F)
			.min(-5F)
			.max(5F)
			.step(0.01F)
			.build();
	
	private SplitterSetting offsetSplitter = new SplitterSettingBuilder()
			.name("Offset")
			.setting(xOffset)
			.setting(yOffset)
			.setting(zOffset)
			.build();

	private NumberSetting<Float> xScale = new NumberSettingBuilder<Float>()
			.name("X")
			.value(1F)
			.min(0.1F)
			.max(5F)
			.step(0.01F)
			.build();
	
	private NumberSetting<Float> yScale = new NumberSettingBuilder<Float>()
			.name("Y")
			.value(1F)
			.min(0.1F)
			.max(5F)
			.step(0.01F)
			.build();
	
	private NumberSetting<Float> zScale = new NumberSettingBuilder<Float>()
			.name("Z")
			.value(1F)
			.min(0.1F)
			.max(5F)
			.step(0.01F)
			.build();
	
	private SplitterSetting scaleSplitter = new SplitterSettingBuilder()
			.name("Scale")
			.setting(xScale)
			.setting(yScale)
			.setting(zScale)
			.build();
	
	public ViewModel() {
		super("ViewModel", FeatureCategory.RENDER);
		setSettings(offsetSplitter, scaleSplitter);
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