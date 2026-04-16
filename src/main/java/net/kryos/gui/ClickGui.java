package net.kryos.gui;

import java.util.ArrayList;
import java.util.List;

import net.kryos.feature.FeatureCategory;
import net.kryos.gui.component.impl.ScreenPanel;
import net.kryos.gui.component.impl.config.ConfigPanel;
import net.kryos.gui.component.impl.feature.CategoryPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ClickGui extends Screen {
	private static final Minecraft mc = Minecraft.getInstance();
	
	public ClickGuiScreen currentScreen = ClickGuiScreen.FEATURES;
	
	private final List<CategoryPanel> categoryPanels = new ArrayList<CategoryPanel>();
	private ConfigPanel configPanel = new ConfigPanel();
	private ScreenPanel screenPanel = new ScreenPanel(this);
	
    public ClickGui() {
        super(Component.literal("Click GUI"));
        
        int x = 20;
        for(FeatureCategory category : FeatureCategory.values()) {
        	categoryPanels.add(new CategoryPanel(category, x, 20));
        	x += 200;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        
        if(currentScreen == ClickGuiScreen.FEATURES) {
	        categoryPanels.reversed().forEach(panel -> {
	        	panel.extractRenderState(graphics, mouseX, mouseY, delta);
	        });
        } else if(currentScreen == ClickGuiScreen.CONFIG) {
        	configPanel.extractRenderState(graphics, mouseX, mouseY, delta);
        }
        
        screenPanel.x = mc.getWindow().getGuiScaledWidth() / 2 - screenPanel.width / 2;
        screenPanel.extractRenderState(graphics, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(screenPanel.mouseClicked(event, doubleClick)) {
        	return true;
        }
        
        if(currentScreen == ClickGuiScreen.FEATURES) {
	    	for(CategoryPanel panel : new ArrayList<CategoryPanel>(categoryPanels)) {
	        	if(panel.mouseClicked(event, doubleClick)) {
	        		categoryPanels.remove(panel);
	        		categoryPanels.addFirst(panel);
	        		break;
	        	}
	    	}
        } else if(currentScreen == ClickGuiScreen.CONFIG) {
        	configPanel.mouseClicked(event, doubleClick);
        }
    	
    	return super.mouseClicked(event, doubleClick);
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        if(currentScreen == ClickGuiScreen.FEATURES) {
        	categoryPanels.forEach(panel -> panel.keyPressed(event));
        } else if(currentScreen == ClickGuiScreen.CONFIG) {
        	configPanel.keyPressed(event);
        }
        
    	return super.keyPressed(event);
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if(currentScreen == ClickGuiScreen.FEATURES) {
        	categoryPanels.forEach(panel -> panel.mouseReleased(event));
        } else if(currentScreen == ClickGuiScreen.CONFIG) {
        	configPanel.mouseReleased(event);
        }
        
    	return super.mouseReleased(event);
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
    	configPanel.charTyped(event);
    	return super.charTyped(event);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
    	return super.mouseScrolled(x, y, scrollX, scrollY);
    }
}