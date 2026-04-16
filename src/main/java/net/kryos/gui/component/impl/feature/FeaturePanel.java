package net.kryos.gui.component.impl.feature;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.kryos.feature.Feature;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.Setting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.animation.Animation;
import net.kryos.gui.animation.EaseInAnimation;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class FeaturePanel extends Component {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 15;
	
	private final List<Component> settingComponents = new ArrayList<Component>();
	
	private Animation expandAnimation;
	
	private Feature feature;
	private boolean expanded;
	private boolean binding;
	
	public FeaturePanel(Feature feature) {
		this.feature = feature;
		
		this.width = WIDTH;
		this.height = HEIGHT;
		this.baseHeight = HEIGHT;
		
		this.expandAnimation = new EaseInAnimation(0.5);
		
		for(Setting setting : feature.getSettings()) {
			if(setting instanceof BooleanSetting booleanSetting)
				settingComponents.add(new BooleanButton(booleanSetting));
			if(setting instanceof ModeSetting modeSetting)
				settingComponents.add(new ModePanel(modeSetting));
			if(setting instanceof NumberSetting<?> numberSetting)
				settingComponents.add(new NumberPanel(numberSetting));
		}
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.enableScissor(x, y, x + width, y + height);
		
        graphics.fill(x + 1, y, x + width - 1, y + height, feature.isEnabled() ? MainTheme.SELECTED : MainTheme.PRIMARY);
        graphics.centeredText(font, feature.name, x + width / 2, y + baseHeight / 2 - font.lineHeight / 2, -1);
        
        String bindText = "[" + (binding ? ".." : String.valueOf(feature.getKey())) + "]";
        if(feature.getKey() != -1 || binding)
        	graphics.text(font, net.minecraft.network.chat.Component.keybind(bindText), x + width - font.width(bindText) - 4, y + baseHeight / 2 - font.lineHeight / 2, -1);
        
        int yOffset = 0;
        
    	for(Component settingComponent : settingComponents) {
    		settingComponent.x = this.x;
    		settingComponent.y = this.y + yOffset + baseHeight;
    		settingComponent.extractRenderState(graphics, mouseX, mouseY, a);
    		yOffset += settingComponent.height;
    	}

    	if(expanded) {
    		expandAnimation.setGoal(yOffset);
    	} else {
    		expandAnimation.setGoal(0);
    	}
    	
    	expandAnimation.update(a);
        this.height = baseHeight + (int) expandAnimation.getValue();
        
    	graphics.fillGradient(x, y + baseHeight, x + width, y + baseHeight + 10, new Color(0, 0, 0, 100).getRGB(), new Color(0, 0, 0, 0).getRGB());
        
        graphics.disableScissor();
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		binding = false;
		if(expanded) {
			for(Component settingComponent : settingComponents) {
				if(settingComponent.mouseClicked(event, doubleClick)) {
					return true;
				}
			}
		}
		
		if(isHovered(event.x(), event.y())) {
			switch (event.button()) {
			case 0:
				feature.toggle();
				return true;
			case 1:
				if(settingComponents.size() > 0) {
					expanded = !expanded;
				}
				return true;
			case 2:
				if(event.hasControlDown()) {
					feature.setKey(-1);
				} else {
					binding = true;
				}
				return true;
			}
		}
		
		return super.mouseClicked(event, doubleClick);
	}
	
	@Override
	public void mouseReleased(MouseButtonEvent event) {
		if(event.button() != 2)
			binding = false;
		
		settingComponents.forEach(settingComponent -> settingComponent.mouseReleased(event));
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(binding) {
			feature.setKey(event.key());
			binding = false;
		}
	}
}
