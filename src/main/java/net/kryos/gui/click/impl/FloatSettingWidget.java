package net.kryos.gui.click.impl;

import org.lwjgl.glfw.GLFW;

import net.kryos.gui.MainTheme;
import net.kryos.gui.widget.Widget;
import net.kryos.setting.impl.FloatSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class FloatSettingWidget extends Widget {
	private static final int WIDTH = 146;
	private static final int HEIGHT = 20;
	private FloatSetting setting;
	private boolean dragging;
	
	public FloatSettingWidget(FloatSetting setting) {
		this.setting = setting;
		this.width = WIDTH;
		this.height = HEIGHT;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		updateDrag(mouseX);

		float min = setting.getMin();
	    float max = setting.getMax();
	    float value = setting.getValue();

	    float percent = (value - min) / (max - min);
	    percent = Math.max(0f, Math.min(1f, percent));

	    int fillWidth = (int) (width * percent);

	    graphics.fill(x, y, x + fillWidth, y + height, MainTheme.SELECTED.getRGB());
		
		graphics.text(font, setting.getName(), x + 4, y + height / 2 - font.lineHeight / 2 + (isHovered(mouseX, mouseY) ? -1 : 0), -1);
		graphics.text(font, setting.getValueString(), x + width - font.width(setting.getValueString()) - 4, y + height / 2 - font.lineHeight / 2, -1);
		
		super.extractRenderState(graphics, mouseX, mouseY, a);
	}
	
	public void updateDrag(int mouseX) {
		if(!dragging) return;
		
		float percent = (mouseX - x) / (float)width;
		float min = setting.getMin();
		float max = setting.getMax();
		float value = min + (max - min) * percent;
		
		setting.setValue(value);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(super.mouseClicked(event, doubleClick)) return true;
		
		if(isHovered(event.x(), event.y())) {
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) dragging = true;
			
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) setting.reset();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if(super.mouseReleased(event)) return true;
		
		if(dragging) {
			dragging = false;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		if(super.mouseScrolled(x, y, scrollX, scrollY)) return true;
		
		if(isHovered(x, y)) {
			if(scrollY > 0) setting.increment();
			
			if(scrollY < 0) setting.decrement();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean visible() {
		return setting.visible();
	}
}