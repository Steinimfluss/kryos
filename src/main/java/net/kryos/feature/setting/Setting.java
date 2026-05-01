package net.kryos.feature.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Setting {
	private String name;
	private List<Setting> settings = new ArrayList<Setting>();

	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(Setting... settings) {
		this.settings = Arrays.asList(settings);
	}
	
	public void addSettings(Setting... settings) {
		Arrays.asList(settings).forEach(this.settings::add);
	}
	
	public boolean hasSettings() {
		return settings.size() > 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}