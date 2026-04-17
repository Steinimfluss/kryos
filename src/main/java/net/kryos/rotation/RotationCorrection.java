package net.kryos.rotation;

public enum RotationCorrection {
	NONE, STRICT;
	
	public static RotationCorrection getFromString(String value) {
		for(RotationCorrection correction : values()) {
			if(correction.name().equalsIgnoreCase(value))
				return correction;
		}
		return NONE;
	}
}