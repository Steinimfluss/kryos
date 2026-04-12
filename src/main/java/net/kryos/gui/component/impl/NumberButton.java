package net.kryos.gui.component.impl;

import net.kryos.feature.setting.NumberSetting;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class NumberButton extends Component {
	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;
	
	private final String symbol;
	private final double increment;
	
	private NumberSetting<?> numberSetting;
	
	public NumberButton(NumberSetting<?> numberSetting, String symbol, double increment) {
		this.numberSetting = numberSetting;
		this.symbol = symbol;
		this.increment = increment;
		
		this.width = WIDTH;
		this.height = HEIGHT;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.centeredText(font, symbol, x + width / 2, y + height / 2 - font.lineHeight / 2, -1);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(isHovered(event.x(), event.y())) {
			numberSetting.setValueFromDouble(numberSetting.getValue().doubleValue() + increment);
			return true;
		}
		
		return super.mouseClicked(event, doubleClick);
	}
}