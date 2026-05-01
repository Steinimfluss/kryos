package net.kryos.feature.setting;

import java.util.ArrayList;
import java.util.List;

public class ModeSetting extends Setting {
	public List<ModeSettingValue> values = new ArrayList<>();
	private ModeSettingValue value;
	
	public void setValue(ModeSettingValue value) {
		this.value = value;
	}
	
	public void setValue(String name) {
		for(ModeSettingValue value : values) {
			if(value.getName().equalsIgnoreCase(name))
				this.value = value;
		}
	}
	
	public void addValue(ModeSettingValue value) {
		this.values.add(value);
	}
	
	public ModeSettingValue getValue() {
		return value;
	}
	
	public String getString() {
		return value.getName();
	}

	public List<ModeSettingValue> getValues() {
		return values;
	}
}