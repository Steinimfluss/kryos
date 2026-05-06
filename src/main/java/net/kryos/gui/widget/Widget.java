package net.kryos.gui.widget;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class Widget {
	protected static final Minecraft mc = Minecraft.getInstance();
	protected static final Font font = Minecraft.getInstance().font;
	public int x, y;
	public int width, height;
	protected final ArrayList<Widget> children = new ArrayList<>();
	protected final Widget parent;
	
	public Widget(Widget parent) {
		this.parent = parent;
	}
	
	public Widget() {
		this.parent = null;
		super();
	}

	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (!visible()) return;

        this.children.reversed().forEach(child -> {
            if (child.visible()) {
                child.extractRenderState(graphics, mouseX, mouseY, a);
            }
        });
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!visible()) return false;

        for (Widget child : this.children) {
            if (child.visible() && child.mouseClicked(event, doubleClick)) return true;
        }
        return false;
    }

    public boolean charTyped(CharacterEvent event) {
        if (!visible()) return false;

        for (Widget child : this.children) {
            if (child.visible() && child.charTyped(event)) return true;
        }
        return false;
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        if (!visible()) return false;

        for (Widget child : this.children) {
            if (child.visible() && child.mouseReleased(event)) return true;
        }
        return false;
    }

    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (!visible()) return false;

        for (Widget child : this.children) {
            if (child.visible() && child.mouseScrolled(x, y, scrollX, scrollY)) return true;
        }
        return false;
    }

    public boolean keyPressed(KeyEvent event) {
        if (!visible()) return false;

        for (Widget child : this.children) {
            if (child.visible() && child.keyPressed(event)) return true;
        }
        return false;
    }

    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (!visible()) return false;

        for (Widget child : this.children) {
            if (child.visible() && child.mouseDragged(event, dx, dy)) return true;
        }
        return false;
    }

    public void resize(int width, int height) {
        if (!visible()) return;
        
        for (Widget child : this.children) {
        	if(!child.visible()) continue;
        	
        	child.resize(width, height);
        }
    }
	
	public boolean isHovered(double mouseX, double mouseY) {
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
	
	public void add(Widget child) {
		children.add(child);
	}
	
	public boolean visible() {
		return true;
	}
}