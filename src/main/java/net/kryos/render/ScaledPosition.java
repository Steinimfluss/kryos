package net.kryos.render;

import net.minecraft.client.Minecraft;

public class ScaledPosition {
	private static final Minecraft mc = Minecraft.getInstance();
	private float x, y;
	
	public void setRelX(float x) {
		this.x = x;
	}
	
	public void setRelY(float y) {
		this.y = y;
	}
	
	public float getRelX() {
		return x;
	}
	
	public float getRelY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x / mc.getWindow().getGuiScaledWidth();
	}
	
	public void setY(float y) {
		this.y = y / mc.getWindow().getGuiScaledHeight();
	}
	
	public float getX() {
		return mc.getWindow().getGuiScaledWidth() * x;
	}
	
	public float getY() {
		return mc.getWindow().getGuiScaledHeight() * y;
	}
}