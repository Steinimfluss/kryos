package net.kryos.gui.component.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.Setting;
import net.kryos.feature.setting.SplitterSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.kryos.render.KryosGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class BooleanSettingPanel extends SettingComponent {
	private static final int BASE_HEIGHT = 25;
	
	private boolean expanded;
	
	private final BooleanSetting setting;
	private final List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();
	
	public BooleanSettingPanel(Component parent, BooleanSetting setting, float width) {
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
		if(isHovered(mouseX, mouseY))
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		
		int xOffset = setting.hasSettings() ? -8 : 0;
		
		graphics.text(font, setting.getName(), getX() + 8, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, setting.enabled ? MainTheme.TEXT : MainTheme.TEXT_DARK);
		
		graphics.outline(getX() + width - 19 + xOffset, getY() + BASE_HEIGHT / 2 - 5, getX() + width - 9 + xOffset, getY() + BASE_HEIGHT / 2 + 5, 1, MainTheme.SELECTED);
		
		if(setting.enabled)
			graphics.fill(getX() + width - 8 - 9 + xOffset, getY() + BASE_HEIGHT / 2 - 3, getX() + width - 11 + xOffset, getY() + BASE_HEIGHT / 2 + 3, MainTheme.SELECTED);

		int yOffset = 2;
		if(expanded) {
			for(SettingComponent settingComponent : settingComponents) {
				settingComponent.setX(getX() + (width - settingComponent.width) / 2);
				settingComponent.setY(getY() + yOffset + BASE_HEIGHT);
				yOffset += settingComponent.height + 4;
			}
			graphics.vLine(getX(), getY(), getY() + height, 0.5f, Color.white);
		}

		this.height = BASE_HEIGHT + yOffset;
		
		String symbol = expanded ? "▴" : "▾";
		if(setting.hasSettings())
			graphics.text(font, symbol, getX() + width - font.width(symbol) - 8, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT);
		
		if(expanded)
			super.extractRenderState(graphics, mouseX, mouseY, a);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if(expanded)
			if(super.mouseClicked(event, doubleClick)) return true;
		
		if(isHovered(event.x(), event.y(), getX(), getY(), width, BASE_HEIGHT)) {
			switch (event.button()) {
				case 0:
					setting.toggle();
					break;
				case 1:
					if(setting.hasSettings())
						expanded = !expanded;
					break;
			}
			return true;
		}
		
		return false;
	}
}
