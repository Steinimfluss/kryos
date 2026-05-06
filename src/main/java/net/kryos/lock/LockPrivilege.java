package net.kryos.lock;

public enum LockPrivilege {
	HIGHEST(200),
	HIGH(100),
	MEDIUM(0),
	LOW(-200),
	LOWEST(-300);
	
	public final int value;
	
	LockPrivilege(int value) {
		this.value = value;
	}
}