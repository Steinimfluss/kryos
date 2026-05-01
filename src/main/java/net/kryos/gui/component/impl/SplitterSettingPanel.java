package net.kryos.gui.component.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.Setting;
import net.kryos.feature.setting.SplitterSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.kryos.render.KryosGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class SplitterSettingPanel extends SettingComponent {
	private static final int BASE_HEIGHT = 10;
	
	private boolean expanded;
	
	private final SplitterSetting setting;
	private final List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();
	
	public SplitterSettingPanel(Component parent, SplitterSetting setting, float width) {
		super(parent);
		this.width = width;
		this.height = BASE_HEIGHT;
		
		this.setting = setting;
		
		for(Setting s : setting.getSettings()) {
			if(s instanceof BooleanSetting booleanSetting) {
				BooleanSettingPanel booleanSettingPanel = new BooleanSettingPanel(this, booleanSetting, width - 4);
				settingComponents.add(booleanSettingPanel);
				this.add(booleanSettingPanel);
			} else if(s instanceof NumberSetting numberSetting) {
				NumberSettingPanel numberSettingPanel = new NumberSettingPanel(this, numberSetting, width - 4);
				settingComponents.add(numberSettingPanel);
				this.add(numberSettingPanel);
			} else if(s instanceof ModeSetting modeSetting) {
				ModeSettingPanel modeSettingPanel = new ModeSettingPanel(this, modeSetting, width - 4);
				settingComponents.add(modeSettingPanel);
				this.add(modeSettingPanel);
			} else if(s instanceof SplitterSetting splitterSetting) {
				SplitterSettingPanel splitterSettingPanel = new SplitterSettingPanel(this, splitterSetting, width - 4);
				settingComponents.add(splitterSettingPanel);
				this.add(splitterSettingPanel);
			}
		}
	}
	
	@Override
	public void extractRenderState(KryosGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.hLine(getX(), getY() + BASE_HEIGHT / 2, getX() + width, 0.5f, MainTheme.LINE);
		graphics.centeredText(font, setting.getName(), getX() + width / 2 - 2, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2 + 1, MainTheme.TEXT);

		String symbol = expanded ? "▴" : "▾";
		if(setting.hasSettings())
			graphics.text(font, symbol, getX() + width - font.width(symbol) - 8, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT);
		
		int yOffset = 2;
		if(expanded) {
			for(SettingComponent settingComponent : settingComponents) {
				settingComponent.setX(getX() + (width - settingComponent.width) / 2);
				settingComponent.setY(getY() + yOffset + BASE_HEIGHT);
				yOffset += settingComponent.height + 4;
			}
			graphics.vLine(getX(), getY(), getY() + height, 0.5f, Color.white);
		}
		height = yOffset + BASE_HEIGHT;
		
		if(expanded)
			super.extractRenderState(graphics, mouseX, mouseY, a);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(expanded)
			if(super.mouseClicked(event, doubleClick)) return true;

		if(isHovered(event.x(), event.y(), getX(), getY(), width, BASE_HEIGHT)) {
			switch (event.button()) {
				case 1:
					if(setting.hasSettings())
						expanded = !expanded;
					break;
			}
		}
		
		return false;
	}
}
