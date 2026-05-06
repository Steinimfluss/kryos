package net.kryos.gui.click.impl;

import org.lwjgl.glfw.GLFW;

import net.kryos.feature.Feature;
import net.kryos.gui.MainTheme;
import net.kryos.gui.widget.Widget;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.BooleanSetting;
import net.kryos.setting.impl.EnumSetting;
import net.kryos.setting.impl.FloatSetting;
import net.kryos.setting.impl.StringSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class FeatureWidget extends Widget {
	private static final int WIDTH = 146;
	private static final int BASE_HEIGHT = 20;
	private String text;
	private Feature feature;
	private boolean expanded;
	
	public FeatureWidget(Feature feature) {
		this.width = WIDTH;
		
		this.feature = feature;
		this.text = feature.getName();
		
		for(Setting<?> setting : feature.getSettings()) {
			createSetting(setting);
		}
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		alignChildren();
		updateSize();
		
		// Tab
		graphics.fill(x, y, x + width, y + height, MainTheme.PRIMARY.getRGB());
		graphics.text(font, text, x + width / 2 - font.width(text) / 2, y + BASE_HEIGHT / 2 - font.lineHeight / 2 + (isHovered(mouseX, mouseY, x, y, width, BASE_HEIGHT) ? -1 : 0), feature.isEnabled() ? MainTheme.TEXT.getRGB() : MainTheme.TEXT_DARK.getRGB());
		
		String symbol = expanded ? "-" : "+";
		graphics.text(font, symbol, x + width - font.width(symbol) - 4, y + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT.getRGB());
		
		if(expanded) {
			super.extractRenderState(graphics, mouseX, mouseY, a);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(super.mouseClicked(event, doubleClick) && expanded) return true;
		
		if(isHovered(event.x(), event.y(), x, y, width, BASE_HEIGHT)) {
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) feature.toggle();
			
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) expanded = !expanded;
			
			return true;
		}
		
		return false;
	}
	
	public void createSetting(Setting<?> setting) {
		if(setting instanceof EnumSetting<?> enumSetting) {
			this.add(new EnumSettingWidget(enumSetting));
		} else if(setting instanceof FloatSetting floatSetting) {
			this.add(new FloatSettingWidget(floatSetting));
		} else if(setting instanceof BooleanSetting booleanSetting) {
			this.add(new BooleanSettingWidget(booleanSetting));
		} else if(setting instanceof StringSetting stringSetting) {
			this.add(new StringSettingWidget(stringSetting));
		}
	}
	
	public void alignChildren() {
		int offset = BASE_HEIGHT;
		
		if(expanded)
		for(Widget child : this.children) {
			if(!child.visible()) continue;
			
			child.x = x + (width - child.width) / 2;
			child.y = y + offset;
			offset += child.height;
		}
	}
	
	public void updateSize() {
		int height = BASE_HEIGHT;

		if(expanded)
		for(Widget child : this.children) {
			if(!child.visible()) continue;
			
			height += child.height;
		}
		
		this.height = height;
	}
}