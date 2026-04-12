package net.kryos.gui.component.impl;

import net.kryos.feature.setting.NumberSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class NumberPanel extends Component {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 20;
	
	private final NumberSetting<?> setting;
	private final NumberSlider slider;
	
	private final NumberButton incrementButton;
	private final NumberButton decrementButton;
	
	public NumberPanel(NumberSetting<?> setting) {
		this.setting = setting;
		this.width = WIDTH;
		this.height = HEIGHT * 2;
		this.baseHeight = HEIGHT;
		this.slider = new NumberSlider(setting);
		
		this.incrementButton = new NumberButton(setting, "+", setting.step.doubleValue());
		this.decrementButton = new NumberButton(setting, "-", -setting.step.doubleValue());
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(x, y, x + width, y + height, MainTheme.SECONDARY);
        
        graphics.text(font, setting.name, x + 4, y + baseHeight / 2 - font.lineHeight / 2, -1);
        graphics.text(font, setting.getValue().toString(), x + width - font.width(setting.getValue().toString()) - 4, y + baseHeight / 2 - font.lineHeight / 2, -1);
        
        slider.x = this.x;
        slider.y = this.y + baseHeight;
        
        slider.extractRenderState(graphics, mouseX, mouseY, a);
        
        decrementButton.x = this.x;
        incrementButton.x = this.x + width - incrementButton.width;
        
        decrementButton.y = slider.y + slider.height / 2 - decrementButton.height / 2;
        incrementButton.y = slider.y + slider.height / 2 - incrementButton.height / 2;
        
        decrementButton.extractRenderState(graphics, mouseX, mouseY, a);
        incrementButton.extractRenderState(graphics, mouseX, mouseY, a);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(decrementButton.mouseClicked(event, doubleClick)) return true;
		if(incrementButton.mouseClicked(event, doubleClick)) return true;
		
		if(slider.mouseClicked(event, doubleClick))
			return true;
		
		if(isHovered(event.x(), event.y()))
			return true;
		
		return super.mouseClicked(event, doubleClick);
	}
	
	@Override
	public void mouseReleased(MouseButtonEvent event) {
		slider.mouseReleased(event);
	}
}
