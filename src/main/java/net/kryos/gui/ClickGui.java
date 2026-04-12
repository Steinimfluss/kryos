package net.kryos.gui;

import java.util.ArrayList;
import java.util.List;

import net.kryos.feature.FeatureCategory;
import net.kryos.gui.component.impl.CategoryPanel;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ClickGui extends Screen {
	private final List<CategoryPanel> categoryPanels = new ArrayList<CategoryPanel	>();
	
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
        categoryPanels.reversed().forEach(panel -> {
        	panel.extractRenderState(graphics, mouseX, mouseY, delta);
        });
        
        graphics.text(font, "Left click to select/enable", 2, 2, -1);
        graphics.text(font, "Right click to expand", 2, 2 + font.lineHeight, -1);
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
    	for(CategoryPanel panel : new ArrayList<CategoryPanel>(categoryPanels)) {
        	if(panel.mouseClicked(event, doubleClick)) {
        		categoryPanels.remove(panel);
        		categoryPanels.addFirst(panel);
        		break;
        	}
    	}
    	
    	return super.mouseClicked(event, doubleClick);
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
    	categoryPanels.forEach(panel -> panel.mouseReleased(event));
    	return super.mouseReleased(event);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
    	return super.mouseScrolled(x, y, scrollX, scrollY);
    }
}