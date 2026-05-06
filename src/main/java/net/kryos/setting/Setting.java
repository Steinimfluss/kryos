package net.kryos.setting;

import java.util.Optional;

public abstract class Setting<T> {
	private Optional<Requirement> requirement;
	
	private String id;
	private String name;
	
	private T value;
	private T defaultValue;
	
	public Setting(String id, String name, T defaultValue) {
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.requirement = Optional.empty();
	}
	
	public void requires(Requirement requirement) {
		this.requirement = Optional.of(requirement);
	}
	
	public boolean visible() {
		if(requirement.isEmpty()) return true;
		
		return requirement.get().requires();
	}

	public T getValue() {
		return value;
	}
	
	public String getValueString() {
		throw new IllegalStateException("Not implemented");
	}

	public void setValueString(String value) {
		throw new IllegalStateException("Not implemented");
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public void reset() {
		this.value = defaultValue;
	}

	public String getId() {
		return id;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public String getName() {
		return name;
	}

	public void setRequirement(Requirement requirement) {
		this.requirement = Optional.of(requirement);
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}
}