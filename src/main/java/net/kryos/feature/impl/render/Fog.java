package net.kryos.feature.impl.render;

import org.joml.Vector4f;

import net.kryos.Kryos;
import net.kryos.event.impl.ComputeFogColorEvent;
import net.kryos.event.listener.impl.ComputeFogColorListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;

public class Fog extends Feature implements ComputeFogColorListener {
	private NumberSetting<Float> redFactor = new NumberSetting<Float>("Red Factor", 1F, 0F, 1F, 0.01F);
	private NumberSetting<Float> blueFactor = new NumberSetting<Float>("Blue Factor", 1F, 0F, 1F, 0.01F);
	private NumberSetting<Float> greenFactor = new NumberSetting<Float>("Green Factor", 1F, 0F, 1F, 0.01F);
	
	public Fog() {
		super("Fog", FeatureCategory.RENDER);
		setSettings(redFactor, blueFactor, greenFactor);
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
	public void computeFogColor(ComputeFogColorEvent event) {
		Vector4f dest = event.getDest();
		dest.set(dest.x() * redFactor.getValue(), dest.y() * greenFactor.getValue(), dest.z() * blueFactor.getValue());
		event.setDest(dest);
	}
}