package net.kryos.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import net.kryos.setting.Setting;
import net.kryos.setting.impl.BooleanSetting;
import net.kryos.setting.impl.StringSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public abstract class Feature {
	protected static final Minecraft mc = Minecraft.getInstance();
	
	private String id;
	private String name;
	private Component description;
	private FeatureCategory category;
	
	private boolean enabled;
	private Optional<Integer> key;
	
	private List<Setting<?>> settings = new ArrayList<>();
	
	private Setting<Boolean> display;
	private Setting<String> displayName;
	
	public Feature(@NotNull String id, @NotNull String name, @NotNull FeatureCategory category, Component description) {
		if (id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
		if (name.isEmpty()) throw new IllegalArgumentException("name cannot be empty");
		 
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		this.key = Optional.empty();
		
		display = addSetting(new BooleanSetting.BooleanSettingBuilder()
				.id("display")
				.name("Display")
				.defaultValue(true)
				.build());
		
		displayName = addSetting(new StringSetting.StringSettingBuilder()
				.id("display_name")
				.name("Display name")
				.defaultValue(name)
				.build());
	}

	
	protected void onEnable() {
		
	}
	
	protected void onDisable() {
		
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if(enabled)
			onEnable();
		else
			onDisable();
	}
	
	public Optional<Setting<?>> getSettingById(String id) {
		for(Setting<?> setting : settings) {
			if(setting.getId().contentEquals(id))
				return Optional.of(setting);
		}
		
		return Optional.empty();
	}
	
	public void toggle() {
		setEnabled(!enabled);
	}

	public FeatureCategory getCategory() {
		return category;
	}

	public boolean hasSettings() {
		return settings.size() > 0;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public List<Setting<?>> getSettings() {
		return settings;
	}

	public String getId() {
		return id;
	}

	public Optional<Integer> getKey() {
		return key;
	}

	public void setKey(Optional<Integer> key) {
		this.key = key;
	}
	
	public <T> Setting<T> addSetting(Setting<T> setting) {
	    if (!getSettingById(setting.getId()).isEmpty())
	        throw new IllegalStateException("Duplicate setting id " + setting.getName());

	    settings.add(setting);
	    return setting;
	}

	public String getName() {
		return name;
	}

	public Component getDescription() {
		return description;
	}
	
	public boolean shouldDisplay() {
		return display.getValue();
	}
	
	public String getDisplayName() {
		return displayName.getValue();
	}
}