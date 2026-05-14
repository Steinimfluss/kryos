package net.kryos.feature.impl.movement;

import net.kryos.Kryos;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.player.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.FloatSetting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Step extends Feature implements PlayerTickListener {
	private Setting<Float> height = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("height")
			.name("Height")
			.min(0)
			.max(10)
			.step(1)
			.defaultValue(2)
			.build());
	
	private float oldHeight;
	
	public Step() {
		super("step", "Step", FeatureCategory.MOVEMENT, Component.literal("Allows you to step up blocks"));
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
		
		if(mc.player == null) return;
		
		oldHeight = mc.player.maxUpStep();
		super.onEnable();
	}
	
	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		if(mc.player == null) return;
		
		mc.player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(oldHeight);
		super.onDisable();
	}

	@Override
	public void onPre(Pre event) {
		mc.player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(height.getValue());
		
	}

	@Override
	public void onPost(Post event) {
		
	}
}