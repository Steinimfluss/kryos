package net.kryos.gui.component.impl;

import net.kryos.gui.ClickGui;
import net.kryos.gui.ClickGuiScreen;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class ScreenButton extends Component {
	private static final int HEIGHT = 20;
	private ClickGuiScreen guiScreen;
	private ClickGui clickGui;
	
	public ScreenButton(ClickGui clickGui, ClickGuiScreen guiScreen) {
		this.height = HEIGHT;
		this.clickGui = clickGui;
		this.guiScreen = guiScreen;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		this.width = font.width(guiScreen.name());
		
		graphics.centeredText(font, guiScreen.name, x + width / 2, y + height / 2 - font.lineHeight / 2, clickGui.currentScreen == guiScreen ? MainTheme.SELECTED : -1);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(isHovered(event.x(), event.y())) {
			if(event.button() == 0) 
				clickGui.currentScreen = guiScreen;
			
			return true;
		}
		
		return super.mouseClicked(event, doubleClick);
	}
}