package net.kryos.gui.component.impl.feature;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.kryos.Kryos;
import net.kryos.feature.FeatureCategory;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class CategoryPanel extends Component {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 15;
	
    private final FeatureCategory category;
    private final List<FeaturePanel> featureButtons = new ArrayList<FeaturePanel>();

    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public CategoryPanel(FeatureCategory category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = WIDTH;;
        this.height = HEIGHT;
        this.baseHeight = HEIGHT;
        
        Kryos.featureManager.getFeaturesByCategory(category).forEach(feature -> featureButtons.add(new FeaturePanel(feature)));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (dragging) {
            this.x = mouseX - dragOffsetX;
            this.y = mouseY - dragOffsetY;
        }

        graphics.fill(x, y, x + width, y + height, MainTheme.SELECTED);
        graphics.centeredText(font, category.name, x + width / 2, y + baseHeight / 2 - font.lineHeight / 2, -1);
       
        int yOffset = baseHeight;
    	for(FeaturePanel featureButton : featureButtons) {
    		featureButton.x = this.x;
    		featureButton.y = this.y + yOffset;
    		featureButton.extractRenderState(graphics, mouseX, mouseY, a);
    		yOffset += featureButton.height;
    	}
    	graphics.fillGradient(x, y + baseHeight, x + width, y + baseHeight + 10, new Color(0, 0, 0, 100).getRGB(), new Color(0, 0, 0, 0).getRGB());
        this.height = yOffset;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
    	for(FeaturePanel featureButton : featureButtons) {
    		if(featureButton.mouseClicked(event, doubleClick)) return true;
    	}
        
        if (isHovered(event.x(), event.y())) {
        	switch(event.button()) {
        	case 0:
                dragging = true;
                dragOffsetX = (int)event.x() - x;
                dragOffsetY = (int)event.y() - y;
                return true;
        	}
        }
        
        return false;
    }

    @Override
    public void mouseReleased(MouseButtonEvent event) {
    	featureButtons.forEach(featureButton -> featureButton.mouseReleased(event));
    	
        dragging = false;
    }
    
    @Override
    public void keyPressed(KeyEvent event) {
    	featureButtons.forEach(featureButton -> featureButton.keyPressed(event));
    }
}
