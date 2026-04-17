package net.kryos.feature.impl.render;

import net.kryos.Kryos;
import net.kryos.event.impl.ModifyFieldOfViewEvent;
import net.kryos.event.listener.impl.ModifyFieldOfViewListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;

public class NoRender extends Feature implements ModifyFieldOfViewListener {
	private BooleanSetting noFovModify = new BooleanSetting("NoFovMod");
	
	public NoRender() {
		super("NoRender", FeatureCategory.RENDER);
		setSettings(noFovModify);
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
	public void modifyFOV(ModifyFieldOfViewEvent event) {
		if(noFovModify.enabled)
			event.setFov(1F);
	}
}