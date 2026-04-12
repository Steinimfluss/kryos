package net.kryos.gui.component.impl;

import net.kryos.feature.setting.ModeSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class ModeButton extends Component {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 20;
	
	private String value;
	private ModeSetting modeSetting;
	
	public ModeButton(String value, ModeSetting modeSetting) {
		this.value = value;
		this.modeSetting = modeSetting;
		this.width = WIDTH;
		this.height = HEIGHT;
		this.baseHeight = HEIGHT;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(x, y, x + width, y + height, MainTheme.TERTIARY);
        graphics.centeredText(font, value, x + width / 2, y + baseHeight - font.lineHeight - 6, modeSetting.getValue().contentEquals(value) ? MainTheme.PRIMARY : -1);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(isHovered(event.x(), event.y())) {
			if(event.button() == 0)
				modeSetting.setValue(value);
			return true;
		}
		
		return super.mouseClicked(event, doubleClick);
	}
}