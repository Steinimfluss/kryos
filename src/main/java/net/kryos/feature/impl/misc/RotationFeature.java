package net.kryos.feature.impl.misc;

import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.ModeSetting;

public class RotationFeature extends Feature {
	public ModeSetting movementCorrection = new ModeSetting("Movement", "None", "None", "Silent", "Strict");
	public ModeSetting rotationCorrection = new ModeSetting("Rotation", "None", "None", "Strict");
	
	public RotationFeature() {
		super("Rotations", FeatureCategory.MISC);
		setSettings(movementCorrection, rotationCorrection);
	}

	@Override
	protected void onEnable() {
		setEnabled(false);
	}

	@Override
	protected void onDisable() {
		
	}
}	