package net.kryos.gui;

import java.util.ArrayList;
import java.util.List;

import net.kryos.feature.FeatureCategory;
import net.kryos.gui.component.impl.CategoryPanel;
import net.kryos.render.KryosGraphicsExtractor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ClickGui extends Screen {
	private List<CategoryPanel> panels = new ArrayList<>();
	
    public ClickGui() {
        super(Component.literal("Click GUI"));
        
        int i = 1;
        for(FeatureCategory category : FeatureCategory.values()) {
        	CategoryPanel panel = new CategoryPanel(category);
        	panel.setRelX((1F / (float)(FeatureCategory.values().length + 1)) * i);
        	panel.setRelY(0.1F);
        	panels.add(panel);
        	i++;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        
        panels.reversed().forEach(panel -> panel.extractRenderState(new KryosGraphicsExtractor(graphics), mouseX, mouseY, delta));
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (CategoryPanel panel : panels) {
            if (panel.mouseClicked(event, doubleClick)) {
                panels.remove(panel);
                panels.addFirst(panel);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        for (CategoryPanel panel : panels) {
            if (panel.keyPressed(event)) {
                panels.remove(panel);
                panels.addFirst(panel);
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        for (CategoryPanel panel : panels) {
            if (panel.mouseReleased(event)) {
                panels.remove(panel);
                panels.addFirst(panel);
                return true;
            }
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        for (CategoryPanel panel : panels) {
            if (panel.charTyped(event)) {
                panels.remove(panel);
                panels.addFirst(panel);
                return true;
            }
        }
        return super.charTyped(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        for (CategoryPanel panel : panels) {
            if (panel.mouseScrolled(x, y, scrollX, scrollY)) {
                panels.remove(panel);
                panels.addFirst(panel);
                return true;
            }
        }
        for (CategoryPanel panel : panels) {
        	panel.setY((float) (panel.getY() + scrollY * 16));
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
    
    public List<CategoryPanel> getPanels() {
        return panels;
    }
    
    public CategoryPanel getPanelByCategory(FeatureCategory c) {
    	for(CategoryPanel p : panels) {
    		if(p.getCategory() == c) {
    			return p;
    		}
    	}
    	
    	return null;
    }
}