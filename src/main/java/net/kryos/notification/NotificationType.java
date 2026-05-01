package net.kryos.notification;

import java.awt.Color;

public enum NotificationType {
	SUCCESS(Color.green),
	ERROR(Color.red),
	WARNING(Color.yellow),
	INFO(Color.blue);
	
	private Color color;
	
	NotificationType(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}