package net.kryos.gui.component.impl;

import net.kryos.feature.setting.BooleanSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class BooleanButton extends Component {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 20;
	
	private BooleanSetting setting;
	
	public BooleanButton(BooleanSetting setting) {
		this.setting = setting;
		this.width = WIDTH;
		this.height = HEIGHT;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(x, y, x + width, y + height, MainTheme.SECONDARY);
        
        graphics.centeredText(font, setting.name, x + width / 2, y + height / 2 - font.lineHeight / 2, -1);
        graphics.text(font, setting.enabled ? "☑" : "☐", x + width - font.width("☐") - 4, y + height / 2 - font.lineHeight / 2, -1);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(isHovered(event.x(), event.y())) {
			if(event.button() == 0)
				setting.toggle();
			return true;
		}
		
		return super.mouseClicked(event, doubleClick);
	}
}
