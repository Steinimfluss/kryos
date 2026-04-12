package net.kryos.feature.setting;

public class ModeSetting extends Setting {
	public final String[] values;
	private String value;
	
	public ModeSetting(String name, String value, String... values) {
		super(name);
		this.values = values;
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
