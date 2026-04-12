package net.kryos.feature;

public enum FeatureCategory {
	COMBAT("Combat"),
	MOVEMENT("Movement"),
	MISC("Misc"),
	TEST("Test"),
	TESTING("Testing");
	
	public final String name;
	
	FeatureCategory(String name) {
		this.name = name;
	}
}
