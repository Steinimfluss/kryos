package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.HandleSetEntityMotionEvent;
import net.kryos.event.listener.impl.HandleSetEntityMotionListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.NumberSettingBuilder;

public class Velocity extends Feature implements HandleSetEntityMotionListener {
	private NumberSetting<Float> xzSetting = new NumberSettingBuilder<Float>()
			.name("XZ")
			.value(1.0F)
			.min(0.0F)
			.max(1.0F)
			.step(0.1F)
			.build();

	private NumberSetting<Float> ySetting = new NumberSettingBuilder<Float>()
			.name("Y")
			.value(1.0F)
			.min(0.0F)
			.max(1.0F)
			.step(0.1F)
			.build();
	
	public Velocity() {
		super("Velocity", FeatureCategory.COMBAT);
		setSettings(xzSetting, ySetting);
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
	public void setMotion(HandleSetEntityMotionEvent event) {
		if(mc.player == null) return;
		
		setSuffix(xzSetting.getValue() + ":" + ySetting.getValue());
		
		if(event.getId() != mc.player.getId()) return;
		
		event.setMovement(event.getMovement().multiply(xzSetting.getValue(), ySetting.getValue(), xzSetting.getValue()));
	}
}