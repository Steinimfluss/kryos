package net.kryos.gui.component.impl;

import java.util.ArrayList;
import java.util.List;

import net.kryos.gui.ClickGui;
import net.kryos.gui.ClickGuiScreen;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class ScreenPanel extends Component {
	private static final int HEIGHT = 20;
	public ClickGui clickGui;
	public List<ScreenButton> screenButtons = new ArrayList<ScreenButton>();
	
	public ScreenPanel(ClickGui clickGui) {
		this.height = HEIGHT;
		this.clickGui = clickGui;
		
		for(ClickGuiScreen clickGuiScreen : ClickGuiScreen.values()) {
			screenButtons.add(new ScreenButton(clickGui, clickGuiScreen));
		}
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.fill(x, y, x + width, y + height, MainTheme.SECONDARY);
		graphics.outline(x, y, width, height, MainTheme.TERTIARY);
		
		int xOffset = 5;
		
		for(ScreenButton screenButton : screenButtons) {
			screenButton.x = this.x + xOffset;
			screenButton.y = this.y;
			
			screenButton.extractRenderState(graphics, x, y, a);

			xOffset += screenButton.width + 5;
		}
		
		this.width = xOffset;
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		for(ScreenButton screenButton : screenButtons) {
			if(screenButton.mouseClicked(event, doubleClick)) return true;
		}
		
		return super.mouseClicked(event, doubleClick);
	}
}
