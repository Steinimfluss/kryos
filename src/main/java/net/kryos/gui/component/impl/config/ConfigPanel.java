package net.kryos.gui.component.impl.config;

import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class ConfigPanel extends Component {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;
	
	private ConfigTextInput textInput = new ConfigTextInput();
	private SaveConfigButton saveConfigButton = new SaveConfigButton();
	private LoadConfigButton loadConfigButton = new LoadConfigButton();
	
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;
	
	public ConfigPanel() {
		this.width = WIDTH;
		this.height = HEIGHT;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (dragging) {
            this.x = mouseX - dragOffsetX;
            this.y = mouseY - dragOffsetY;
        }
		
		saveConfigButton.text = textInput.getText();
		loadConfigButton.text = textInput.getText();
		
		textInput.x = this.x;
		textInput.y = this.y + textInput.height;
		
		saveConfigButton.x = this.x;
		saveConfigButton.y = this.y + textInput.height * 2;
		
		loadConfigButton.x = this.x;
		loadConfigButton.y = this.y + textInput.height * 3 + saveConfigButton.height;
		
		graphics.fill(x, y, x + width, y + height, MainTheme.SECONDARY);
		graphics.outline(x, y, width, height, MainTheme.TERTIARY);
		
		textInput.extractRenderState(graphics, mouseX, mouseY, a);
		saveConfigButton.extractRenderState(graphics, mouseX, mouseY, a);
		loadConfigButton.extractRenderState(graphics, mouseX, mouseY, a);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(saveConfigButton.mouseClicked(event, doubleClick))
			return true;
		
		if(loadConfigButton.mouseClicked(event, doubleClick))
			return true;
		
		if(textInput.mouseClicked(event, doubleClick))
			return true;
		
        if (isHovered(event.x(), event.y())) {
        	if(event.button() == 0) {
                dragging = true;
                dragOffsetX = (int)event.x() - x;
                dragOffsetY = (int)event.y() - y;
        	}
        	return true;
		}

		return super.mouseClicked(event, doubleClick);
	}
	
	@Override
	public void mouseReleased(MouseButtonEvent event) {
		dragging = false;
		textInput.mouseReleased(event);
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		textInput.keyPressed(event);
	}
	
	@Override
	public void charTyped(CharacterEvent event) {
		textInput.charTyped(event);
	}
}
