package net.kryos.feature;

import java.util.Optional;

public enum FeatureCategory {
	COMBAT("Combat"),
	RENDER("Render"),
	WORLD("World"),
	MISC("Misc"),
	MOVEMENT("Movement");
	
	private final String name;
	
	FeatureCategory(String name) {
		this.name = name;
	}
	
	public static Optional<FeatureCategory> getByName(String name) {
		for(FeatureCategory c : FeatureCategory.values()) {
			if(c.name.equalsIgnoreCase(name)) {
				return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	public String getName() {
		return name;
	}
}