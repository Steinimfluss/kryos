package net.kryos.feature;

public enum FeatureCategory {
	COMBAT("Combat"),
	RENDER("Render");
	
	public final String name;
	
	FeatureCategory(String name) {
		this.name = name;
	}
}
