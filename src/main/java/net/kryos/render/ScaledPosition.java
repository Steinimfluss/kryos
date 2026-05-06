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
	
	public void setX(int x) {
	    this.x = (float)x / mc.getWindow().getGuiScaledWidth();
	}

	public void setY(int y) {
	    this.y = (float)y / mc.getWindow().getGuiScaledHeight();
	}
	
	public int getX() {
	    return (int)(mc.getWindow().getGuiScaledWidth() * x);
	}

	public int getY() {
	    return (int)(mc.getWindow().getGuiScaledHeight() * y);
	}
}