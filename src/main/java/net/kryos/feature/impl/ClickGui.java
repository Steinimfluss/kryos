package net.kryos.feature.impl;

import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;

public class ClickGui extends Feature {
	private net.kryos.gui.ClickGui clickGui;
	
	public ClickGui() {
		super("ClickGUI", FeatureCategory.RENDER);
	}

	@Override
	protected void onEnable() {
		if(clickGui == null)
			clickGui = new net.kryos.gui.ClickGui();
		
		mc.setScreen(clickGui);
		setEnabled(false);
	}

	@Override
	protected void onDisable() {
		
	}
}