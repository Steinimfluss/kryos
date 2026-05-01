package net.kryos.gui.component;

import java.util.ArrayList;

import net.kryos.render.KryosGraphicsExtractor;
import net.kryos.render.ScaledPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class Component {
	protected static final Minecraft mc = Minecraft.getInstance();
	protected static final Font font = Minecraft.getInstance().font;
	private ScaledPosition pos = new ScaledPosition();
	public float width, height;
	private final ArrayList<Component> children = new ArrayList<>();
	protected final Component parent;
	
	public Component(Component parent) {
		this.parent = parent;
	}
	
	public Component() {
		this.parent = null;
		super();
	}

	public void extractRenderState(KryosGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		this.children.forEach(child -> child.extractRenderState(graphics, mouseX, mouseY, a));
	}
	
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		boolean clicked = false;
		for(Component child : this.children) {
			if(child.mouseClicked(event, doubleClick)) clicked = true;
		}
		return clicked;
	}
	
	public boolean charTyped(CharacterEvent event) {
		for(Component child : this.children) {
			if(child.charTyped(event)) return true;
		}
		return false;
    }
	
	public boolean mouseReleased(MouseButtonEvent event) {
		for(Component child : this.children) {
			if(child.mouseReleased(event)) return true;
		}
		return false;
	}
	
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		for(Component child : this.children) {
			if(child.mouseScrolled(x, y, scrollX, scrollY)) return true;
		}
		return false;
    }

	public boolean keyPressed(KeyEvent event) {
		for(Component child : this.children) {
			if(child.keyPressed(event)) return true;
		}
		return false;
    }
	
	public boolean isHovered(double mouseX, double mouseY) {
		float x = getX();
		float y = getY();
		
		float left   = Math.min(x, x + width);
		float right  = Math.max(x, x + width);
		float top    = Math.min(y, y + height);
		float bottom = Math.max(y, y + height);

	    return mouseX >= left && mouseX <= right &&
	           mouseY >= top  && mouseY <= bottom;
	}
	
	public boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
		float left   = Math.min(x, x + width);
		float right  = Math.max(x, x + width);
		float top    = Math.min(y, y + height);
		float bottom = Math.max(y, y + height);

	    return mouseX >= left && mouseX <= right &&
	           mouseY >= top  && mouseY <= bottom;
	}
	
	public void add(Component child) {
		children.add(child);
	}
	
	public void setX(float x) {
		pos.setX(x);
	}
	
	public void setY(float y) {
		pos.setY(y);
	}
	
	public float getX() {
		return pos.getX();
	}
	
	public float getY() {
		return pos.getY();
	}
	
	public void setRelX(float x) {
		pos.setRelX(x);
	}
	
	public void setRelY(float y) {
		pos.setRelY(y);
	}
}