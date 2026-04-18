package net.kryos.rotation;

public enum MovementCorrection {
	NONE, SILENT, STRICT;
	
	public static MovementCorrection getFromString(String value) {
		for(MovementCorrection correction : values()) {
			if(correction.name().equalsIgnoreCase(value))
				return correction;
		}
		return NONE;
	}
}
