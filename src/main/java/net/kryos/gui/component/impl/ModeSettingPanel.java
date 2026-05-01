package net.kryos.gui.component.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.ModeSettingValue;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.kryos.render.KryosGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class ModeSettingPanel extends SettingComponent {
	private static final int BASE_HEIGHT = 25;
	
	private List<ModeValuePanel> modeValuePanels = new ArrayList<>();
	private final ModeSetting setting;
	
	private boolean expanded;
	
	public ModeSettingPanel(Component parent, ModeSetting setting, float width) {
		super(parent);
		this.width = width;
		this.height = BASE_HEIGHT;
		
		this.setting = setting;
		
		for(ModeSettingValue value : setting.values) {
			ModeValuePanel modeValuePanel = new ModeValuePanel(this, setting, value, width - 4);
			modeValuePanels.add(modeValuePanel);
			this.add(modeValuePanel);
		}
	}
	
	@Override
	public void extractRenderState(KryosGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.text(font, setting.getName(), getX() + 8, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT);
		
		graphics.text(font, setting.getValue().getName(), getX() + width - font.width(setting.getValue().getName()) - 16, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT);

		String symbol = expanded ? "▴" : "▾";
		graphics.text(font, symbol, getX() + width - font.width(symbol) - 8, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT);
		
		int yOffset = 0;
		
		if(expanded) {
			for(ModeValuePanel modeValuePanel : modeValuePanels) {
				modeValuePanel.setX(getX());
				modeValuePanel.setY(getY() + BASE_HEIGHT + yOffset);
				yOffset += modeValuePanel.height;
			}
			
			super.extractRenderState(graphics, mouseX, mouseY, a);
		}
		
		this.height = BASE_HEIGHT + yOffset;
		
		if(expanded)
			graphics.vLine(getX(), getY(), getY() + height, 0.5f, Color.white);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(expanded) {
			if(super.mouseClicked(event, doubleClick)) return true;
		}

		if(isHovered(event.x(), event.y(), getX(), getY(), width, BASE_HEIGHT)) {
			switch (event.button()) {
				case 1:
					expanded = !expanded;
					break;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if(super.mouseReleased(event)) return true;
		
		return false;
	}
}
