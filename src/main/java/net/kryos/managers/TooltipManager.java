package net.kryos.managers;

import net.minecraft.network.chat.Component;

public class TooltipManager {
	private Component tooltip = Component.empty();

	public Component getTooltip() {
		return tooltip;
	}

	public void setTooltip(Component tooltip) {
		this.tooltip = tooltip;
	}
	
	public void clearTooltip() {
		this.tooltip = Component.empty();
	}
}