package net.kryos.feature.impl;

import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.NumberSetting;

public class Killaura extends Feature {
	public Killaura() {
		super("Killaura", FeatureCategory.COMBAT);
		
		setSettings(new BooleanSetting("bool"), new NumberSetting<Float>("Angle", 1.0f, -5.0f, 5.0f, 0.1f), new ModeSetting("Swing", "Silent", "Silent", "Packet"), new BooleanSetting("bool1"), new NumberSetting<Float>("Angle1", 1.0f, -5.0f, 5.0f, 0.1f), new ModeSetting("Swing1", "Silent", "Silent", "Packet"));
	}

	@Override
	protected void onEnable() {
		
	}

	@Override
	protected void onDisable() {
		
	}
}