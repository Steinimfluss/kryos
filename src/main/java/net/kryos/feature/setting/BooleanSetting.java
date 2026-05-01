package net.kryos.feature.setting;

public class BooleanSetting extends Setting {
	public boolean enabled;
	
	public BooleanSetting() {
		super();
	}
	
	public void toggle() {
		this.enabled = !this.enabled;
	}
}
