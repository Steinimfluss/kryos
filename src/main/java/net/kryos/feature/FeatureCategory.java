package net.kryos.feature;

public enum FeatureCategory {
	COMBAT("Combat"),
	RENDER("Render"),
	WORLD("World"),
	MISC("Misc"),
	MOVEMENT("Movement");
	
	public final String name;
	
	FeatureCategory(String name) {
		this.name = name;
	}
	
	public static FeatureCategory getByName(String name) {
		for(FeatureCategory c : FeatureCategory.values()) {
			if(c.name.equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}
}