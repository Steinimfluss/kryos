package net.kryos.rotation;

public enum RotationPrivilege {
	HIGHEST(200),
	HIGH(100),
	MEDIUM(0),
	LOW(-200),
	LOWEST(-300);
	
	public final int value;
	
	RotationPrivilege(int value) {
		this.value = value;
	}
}