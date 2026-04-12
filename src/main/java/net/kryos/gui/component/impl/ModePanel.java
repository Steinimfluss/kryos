package net.kryos.gui.component.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.kryos.feature.setting.ModeSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class ModePanel extends Component {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 20;
	
	private List<ModeButton> modeButtons = new ArrayList<ModeButton>();
	
	private boolean expanded;
	
	private final ModeSetting setting;
	
	public ModePanel(ModeSetting setting) {
		this.setting = setting;
		this.width = WIDTH;
		this.height = HEIGHT;
		this.baseHeight = HEIGHT;
		
		for(String value : setting.values) {
			modeButtons.add(new ModeButton(value, setting));
		}
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(x, y, x + width, y + height, MainTheme.SECONDARY);
        
        graphics.text(font, setting.name, x + 6, y + baseHeight / 2 - font.lineHeight / 2, -1);
        graphics.text(font, setting.getValue(), x + width - font.width(setting.getValue()) - 4, y + baseHeight / 2 - font.lineHeight / 2, -1);
        
        int yOffset = baseHeight;
        if(expanded) {
        	for(Component modeButton : modeButtons) {
        		modeButton.x = this.x;
        		modeButton.y = this.y + yOffset;
        		modeButton.extractRenderState(graphics, mouseX, mouseY, a);
        		yOffset += modeButton.height;
        	}
        }
        this.height = yOffset;

        if(expanded) {
        	graphics.fillGradient(x, y + baseHeight, x + width, y + baseHeight + 10, new Color(0, 0, 0, 100).getRGB(), new Color(0, 0, 0, 0).getRGB());
        	graphics.fill(x + 2, y, x + 3, y + yOffset - 1, -1);
        }
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(expanded) {
			for(ModeButton modeButton : modeButtons) {
				if(modeButton.mouseClicked(event, doubleClick))
					return true;
			}
		}
		
		if(isHovered(event.x(), event.y())) {
			switch (event.button()) {
			case 1:
				expanded = !expanded;
			}
			return true;
		}
		
		return super.mouseClicked(event, doubleClick);
	}
}
