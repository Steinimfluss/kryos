package net.kryos.gui.click.impl;

import org.lwjgl.glfw.GLFW;

import net.kryos.gui.MainTheme;
import net.kryos.gui.widget.Widget;
import net.kryos.setting.impl.BooleanSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class BooleanSettingWidget extends Widget {
	private static final int WIDTH = 146;
	private static final int HEIGHT = 20;
	private BooleanSetting setting;
	
	public BooleanSettingWidget(BooleanSetting setting) {
		this.setting = setting;
		this.width = WIDTH;
		this.height = HEIGHT;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {		
		graphics.text(font, setting.getName(), x + 4, y + height / 2 - font.lineHeight / 2 + (isHovered(mouseX, mouseY) ? -1 : 0), MainTheme.TEXT.getRGB());
		
		String value = setting.getValueString();
		graphics.text(font, value, x + width - font.width(value) - 4, y + height / 2 - font.lineHeight / 2, MainTheme.TEXT.getRGB());
		
		super.extractRenderState(graphics, mouseX, mouseY, a);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(super.mouseClicked(event, doubleClick)) return true;
		
		if(isHovered(event.x(), event.y())) {
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) setting.toggle();
			
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) setting.reset();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean visible() {
		return setting.visible();
	}
}