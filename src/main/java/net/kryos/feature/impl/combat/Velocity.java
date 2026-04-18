package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.HandleSetEntityMotionEvent;
import net.kryos.event.listener.impl.HandleSetEntityMotionListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;

public class Velocity extends Feature implements HandleSetEntityMotionListener {
	private NumberSetting<Float> xzSetting = new NumberSetting<Float>("XZ", 1.0F, 0.0F, 1.0F, 0.1F);
	private NumberSetting<Float> ySetting = new NumberSetting<Float>("Y", 1.0F, 0.0F, 1.0F, 0.1F);
	
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
		
		if(event.getId() != mc.player.getId()) return;
		
		event.setMovement(event.getMovement().multiply(xzSetting.getValue(), ySetting.getValue(), xzSetting.getValue()));
	}
}