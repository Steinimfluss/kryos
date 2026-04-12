package net.kryos.feature.impl;

import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;

public class TestFeature extends Feature {
	public TestFeature() {
		super("TestFeature", FeatureCategory.COMBAT);
	}

	@Override
	protected void onEnable() {
		
	}

	@Override
	protected void onDisable() {
		
	}
}