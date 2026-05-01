package net.kryos.gui.component.impl;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.kryos.feature.Feature;
import net.kryos.feature.Keybind;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.Setting;
import net.kryos.feature.setting.SplitterSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.kryos.render.KryosGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class FeaturePanel extends Component {
	private static final int BASE_WIDTH = 150;
	private static final int BASE_HEIGHT = 25;
	private final List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();
	private final Feature feature;
	public boolean expanded;
	
	private boolean binding;
	
	public FeaturePanel(Component parent, Feature feature) {
		super(parent);
		this.feature = feature;
		this.width = BASE_WIDTH;
		this.height = BASE_HEIGHT;
		
		for(Setting setting : feature.getSettings()) {
			if(setting instanceof BooleanSetting booleanSetting) {
				BooleanSettingPanel booleanSettingPanel = new BooleanSettingPanel(this, booleanSetting, width - 4);
				settingComponents.add(booleanSettingPanel);
				this.add(booleanSettingPanel);
			} else if(setting instanceof NumberSetting numberSetting) {
				NumberSettingPanel numberSettingPanel = new NumberSettingPanel(this, numberSetting, width - 4);
				settingComponents.add(numberSettingPanel);
				this.add(numberSettingPanel);
			} else if(setting instanceof ModeSetting modeSetting) {
				ModeSettingPanel modeSettingPanel = new ModeSettingPanel(this, modeSetting, width - 4);
				settingComponents.add(modeSettingPanel);
				this.add(modeSettingPanel);
			} else if(setting instanceof SplitterSetting splitterSetting) {
				SplitterSettingPanel splitterSettingPanel = new SplitterSettingPanel(this, splitterSetting, width - 4);
				settingComponents.add(splitterSettingPanel);
				this.add(splitterSettingPanel);
			}
		}
	}
	
	@Override
	public void extractRenderState(KryosGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
	    if (isHovered(mouseX, mouseY, getX(), getY(), width, BASE_HEIGHT)) {
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		}
		
		graphics.fill(getX(), getY(), getX() + BASE_WIDTH, getY() + BASE_HEIGHT, MainTheme.SECONDARY);
		
		if(feature.isEnabled())
			graphics.vLine(getX(), getY(), getY() + BASE_HEIGHT, 2, MainTheme.SELECTED);
		
		graphics.text(font, feature.name, getX() + 8, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, feature.isEnabled() ? MainTheme.TEXT : MainTheme.TEXT_DARK);
		
		String symbol = expanded ? "▴" : "▾";
		if(feature.hasSettings())
			graphics.text(font, symbol, getX() + width - font.width(symbol) - 8, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT);

		String key = binding ? "..." : (feature.getKey() == null ? "" : feature.getKey().getName());
		graphics.text(font, key, getX() + width - font.width(key) - 14, getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT);
		graphics.outline(getX(), getY(), getX() + width, getY() + BASE_HEIGHT, 0.5f, MainTheme.LINE);

		int yOffset = 2;
		
		if(expanded) {
			for(SettingComponent settingComponent : settingComponents) {
				settingComponent.setX(getX() + (width - settingComponent.width) / 2);
				settingComponent.setY(getY() + yOffset + BASE_HEIGHT);
				yOffset += settingComponent.height + 4;
			}
		}
		
		this.height = BASE_HEIGHT + yOffset;
		
		if(expanded) {
			super.extractRenderState(graphics, mouseX, mouseY, a);
		}
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		binding = false;
		if(super.mouseClicked(event, doubleClick) && expanded) return true;
		
		if(isHovered(event.x(), event.y(), getX(), getY(), width, BASE_HEIGHT)) {
			switch (event.button()) {
				case 0:
					feature.toggle();
					break;
				case 1:
					if(feature.hasSettings())
						expanded = !expanded;
					break;
				case 2:
					binding = true;
					break;
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyPressed(KeyEvent event) {
		if(super.keyPressed(event)) return true;
		
		
		if(binding) {
			if(event.input() == GLFW.GLFW_KEY_ESCAPE) {
				feature.setKey(null);
				binding = false;
				return true;
			}
			
			feature.setKey(new Keybind(event.input(), event.scancode()));
			binding = false;
			return true;
		}
		
		return false;
	}

	public Feature getFeature() {
		return feature;
	}
}