package net.kryos.gui.component.impl;

import net.kryos.feature.setting.NumberSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.kryos.render.KryosGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class NumberSettingPanel extends SettingComponent {
	private static final int BASE_HEIGHT = 25;
	
	private final NumberSetting<?> setting;
	private final NumberSettingSlider slider;
	
	private boolean dragging;
	
	public NumberSettingPanel(Component parent, NumberSetting<?> setting, float width) {
		super(parent);
		this.width = width;
		this.height = BASE_HEIGHT;
		
		this.setting = setting;
		slider = new NumberSettingSlider(parent, setting, width);
		this.add(slider);
	}
	
	@Override
	public void extractRenderState(KryosGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.text(font, setting.getName(), getX() + 8, getY() + height / 2 - font.lineHeight / 2, MainTheme.TEXT);
		graphics.text(font, setting.toString(), getX() + width - font.width(setting.toString()) - 12, getY() + height / 2 - font.lineHeight / 2, MainTheme.TEXT);
		
		slider.setX(getX() + (width - slider.width) / 2);
		slider.setY(getY() + height - slider.height);
		
		super.extractRenderState(graphics, mouseX, mouseY, a);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(super.mouseClicked(event, doubleClick)) return true;
		
		return false;
	}
	
	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if(super.mouseReleased(event)) return true;
		
		if(dragging == true) {
			dragging = false;
			return true;
		}
		return false;
	}
}
