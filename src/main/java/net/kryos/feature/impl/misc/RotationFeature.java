package net.kryos.feature.impl.misc;

import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.ModeSettingBuilder;

public class RotationFeature extends Feature {
	public ModeSetting movementCorrection = new ModeSettingBuilder()
			.name("Movement")
			.mode("None")
			.mode("Silent")
			.mode("Strict")
			.value("None")
			.build();
	
	public ModeSetting rotationCorrection = new ModeSettingBuilder()
			.name("Rotation")
			.mode("None")
			.mode("Silent")
			.mode("Strict")
			.value("None")
			.build();
	
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