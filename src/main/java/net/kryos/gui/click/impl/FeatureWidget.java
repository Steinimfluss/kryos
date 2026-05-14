package net.kryos.gui.click.impl;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import net.kryos.Kryos;
import net.kryos.feature.Feature;
import net.kryos.gui.MainTheme;
import net.kryos.gui.widget.Widget;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.BooleanSetting;
import net.kryos.setting.impl.EnumSetting;
import net.kryos.setting.impl.FloatSetting;
import net.kryos.setting.impl.StringSetting;
import net.kryos.util.input.KeyboardUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class FeatureWidget extends Widget {
	private static final int WIDTH = 146;
	private static final int BASE_HEIGHT = 20;
	private Feature feature;
	private boolean expanded;
	private boolean binding;
	
	public FeatureWidget(Feature feature) {
		this.width = WIDTH;
		
		this.feature = feature;
		
		for(Setting<?> setting : feature.getSettings()) {
			createSetting(setting);
		}
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		alignChildren();
		updateSize();
		
		if(isHovered(mouseX, mouseY, x, y, width, BASE_HEIGHT)) {
			Kryos.tooltipManager.setTooltip(feature.getDescription());
		}
		
		// Tab
		graphics.fill(x, y, x + width, y + height, MainTheme.PRIMARY.getRGB());
		
		String name = feature.getName();
		graphics.text(font, name, x + width / 2 - font.width(name) / 2, y + BASE_HEIGHT / 2 - font.lineHeight / 2 + (isHovered(mouseX, mouseY, x, y, width, BASE_HEIGHT) ? -1 : 0), feature.isEnabled() ? MainTheme.TEXT.getRGB() : MainTheme.TEXT_DARK.getRGB());
		
		String key = binding ? "..." : KeyboardUtil.getName(feature.getKey().orElse(-1));
		graphics.text(font, key, x + 4, y + BASE_HEIGHT / 2 - font.lineHeight / 2, -1);
		
		String symbol = expanded ? "-" : "+";
		graphics.text(font, symbol, x + width - font.width(symbol) - 4, y + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT.getRGB());
		
		if(expanded) {
			super.extractRenderState(graphics, mouseX, mouseY, a);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(super.mouseClicked(event, doubleClick) && expanded) return true;
		
		if(binding) {
			binding = false;
			return true;
		}
		
		if(isHovered(event.x(), event.y(), x, y, width, BASE_HEIGHT)) {
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) feature.toggle();
			
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) expanded = !expanded;
			
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) binding = true;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyPressed(KeyEvent event) {
		if(super.keyPressed(event)) return true;
		
		if(binding) {
			if(event.input() == GLFW.GLFW_KEY_ESCAPE)
				feature.setKey(Optional.empty());
			else
				feature.setKey(Optional.of(event.input()));
			binding = false;
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