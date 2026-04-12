package net.kryos.feature.setting;

public class BooleanSetting extends Setting {
	public boolean enabled;
	
	public BooleanSetting(String name) {
		super(name);
	}
	
	public void toggle() {
		this.enabled = !this.enabled;
	}
}
