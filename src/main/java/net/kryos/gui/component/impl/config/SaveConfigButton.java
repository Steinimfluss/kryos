package net.kryos.gui.component.impl.config;

import net.kryos.Kryos;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class SaveConfigButton extends Component {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 20;
	public String text;
	
	public SaveConfigButton() {
		this.width = WIDTH;
		this.height = HEIGHT;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.fill(x, y, x + width, y + height, MainTheme.TERTIARY);
		graphics.centeredText(font, "Save", x + width / 2, y + height / 2 - font.lineHeight / 2, -1);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(isHovered(event.x(), event.y())) {
			if(event.button() == 0) {
				try {
					Kryos.configManager.save(Kryos.CONFIG_PATH.resolve(text + ".json"));
					Kryos.configManager.saveCurrent();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}
}
