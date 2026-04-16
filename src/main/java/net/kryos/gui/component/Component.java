package net.kryos.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class Component {
	protected static final Minecraft mc = Minecraft.getInstance();
	protected static final Font font = Minecraft.getInstance().font;
	public int x, y, width, height, baseHeight;

	public Component() {}
	
	public Component(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Component(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {}
	
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		return false;
	}
	
    public void charTyped(CharacterEvent event) {
    	
    }
	
	public void mouseReleased(MouseButtonEvent event) {
		
	}
	
    public void mouseScrolled(double x, double y, double scrollX, double scrollY) {
    	
    }

    public void keyPressed(KeyEvent event) {
    	
    }
	
	public boolean isHovered(double mouseX, double mouseY) {
	    int left   = Math.min(x, x + width);
	    int right  = Math.max(x, x + width);
	    int top    = Math.min(y, y + height);
	    int bottom = Math.max(y, y + height);

	    return mouseX >= left && mouseX <= right &&
	           mouseY >= top  && mouseY <= bottom;
	}
}