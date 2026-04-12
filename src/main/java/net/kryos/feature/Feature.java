package net.kryos.feature;

import net.kryos.feature.setting.Setting;

public abstract class Feature {	
	public final String name;
	private boolean enabled;
	private Setting[] settings = new Setting[0];
	public final FeatureCategory category;
	private int key = -1;
	
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
	
	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}
}