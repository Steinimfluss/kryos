package net.kryos.gui.click.impl;

import org.lwjgl.glfw.GLFW;

import net.kryos.feature.FeatureCategory;
import net.kryos.gui.MainTheme;
import net.kryos.gui.widget.Widget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class CategoryWidget extends Widget {
	private static final int WIDTH = 150;
	private static final int TAB_SIZE = 20;
	private static final int SPACING = 2;
	
	private String tabText;
	private int tabSize;
	private int spacing;
	private boolean dragging;
	
	private int grabX;
    private int grabY;
    
    public CategoryWidget(FeatureCategory category) {
    	this.tabText = category.getName();
    	this.width = WIDTH;
    	this.tabSize = TAB_SIZE;
    	this.spacing = SPACING;
    }
    
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		updateDrag(mouseX, mouseY);
		alignChildren();
		updateSize();
		
		// Background
		graphics.fill(x, y, x + width, y + height, MainTheme.PRIMARY.getRGB());
		
		// Tab
		graphics.fill(x, y, x + width, y + tabSize, MainTheme.SELECTED.getRGB());
		graphics.text(font, tabText, x + width / 2 - font.width(tabText) / 2, y + tabSize / 2 - font.lineHeight / 2, -1);
		
		super.extractRenderState(graphics, mouseX, mouseY, a);
	}
	
	public void alignChildren() {
		int offset = spacing;
		
		for(Widget child : children) {
			// Center the child
			child.x = x + (width - child.width) / 2;
			
			child.y = y + tabSize + offset;
			
			offset += child.height + spacing;
		}
	}
	
	public void updateSize() {
		int height = tabSize + spacing;
	
		for(Widget child : children) {
			height += child.height + spacing;
		}
		
		this.height = height;
	}
	
	private void updateDrag(int mouseX, int mouseY) {
        if (!dragging) return;

        this.x = Math.min(mc.getWindow().getGuiScaledWidth() - width, Math.max(0, mouseX - grabX));
        this.y = Math.max(0, mouseY - grabY);
    }
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(super.mouseClicked(event, doubleClick)) return true;
		
		if(isHovered(event.x(), event.y(), x, y, width, tabSize)) {
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				dragging = true;
				grabX = (int) (event.x() - x);
                grabY = (int) (event.y() - y);
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		dragging = false;
		
		return super.mouseReleased(event);
	}
}