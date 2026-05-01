package net.kryos.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kryos.feature.setting.Setting;
import net.minecraft.client.Minecraft;

public abstract class Feature {
	protected static final Minecraft mc = Minecraft.getInstance();
	public final String name;
	private boolean enabled;
	private List<Setting> settings = new ArrayList<Setting>();
	public final FeatureCategory category;
	private Keybind key;
	private String suffix;
	
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
	
	public boolean hasSettings() {
		return settings.size() > 0;
	}
	
	public void toggle() {
		setEnabled(!enabled);
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(Setting... settings) {
		this.settings = Arrays.asList(settings);
	}
	
	public void addSettings(Setting... settings) {
		Arrays.asList(settings).forEach(this.settings::add);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Keybind getKey() {
		return key;
	}

	public void setKey(Keybind key) {
		this.key = key;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}