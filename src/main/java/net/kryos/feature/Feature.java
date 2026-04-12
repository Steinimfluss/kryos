package net.kryos.feature;

import net.kryos.feature.setting.Setting;

public abstract class Feature {	
	public final String name;
	private boolean enabled;
	private Setting[] settings;
	public final FeatureCategory category;
	
	public Feature(String name, FeatureCategory category) {
		this.name = name;
		this.category = category;
	}
	
	protected abstract void onEnable();
	protected abstract void onDisable();
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if(enabled)
			onEnable();
		else
			onDisable();
	}
	
	public void toggle() {
		setEnabled(!enabled);
	}

	public Setting[] getSettings() {
		return settings;
	}

	public void setSettings(Setting... settings) {
		this.settings = settings;
	}

	public boolean isEnabled() {
		return enabled;
	}
}