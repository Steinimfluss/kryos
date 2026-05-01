package net.kryos.gui.component.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.kryos.Kryos;
import net.kryos.feature.FeatureCategory;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.kryos.render.KryosGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class CategoryPanel extends Component {
	private static final int BASE_WIDTH = 150;
	private static final int BASE_HEIGHT = 25;
	private final FeatureCategory category;
	private List<FeaturePanel> featurePanels = new ArrayList<>();
	
	private double startDragX;
	private double startDragY;
	private boolean dragging;
	
	public CategoryPanel(FeatureCategory category) {
		super();
		this.category = category;
		this.width = BASE_WIDTH;
		this.height = BASE_HEIGHT;
		
		Kryos.featureManager.getFeaturesByCategory(category).forEach(feature -> {
			FeaturePanel featurePanel = new FeaturePanel(this, feature);
			featurePanels.add(featurePanel);
			this.add(featurePanel);
		});
	}
	
	@Override
	public void extractRenderState(KryosGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if(dragging) {
			graphics.requestCursor(CursorTypes.RESIZE_ALL);
		}
		
		graphics.fill(getX(), getY(), getX() + width, getY() + height, MainTheme.PRIMARY);
	    
		graphics.fill(getX(), getY(), getX() + width, getY() + BASE_HEIGHT, MainTheme.SELECTED);
		graphics.hLine(getX(), getY() + BASE_HEIGHT, getX() + width, 0.5f, MainTheme.LINE);
		graphics.text(font, category.name, this.getX() + 4, this.getY() + BASE_HEIGHT / 2 - font.lineHeight / 2, MainTheme.TEXT);
	    
		int yOffset = 0;
		
		for(FeaturePanel featurePanel : featurePanels) {
			featurePanel.setX(getX() + (width - featurePanel.width) / 2);
			featurePanel.setY(getY() + yOffset + BASE_HEIGHT);
			yOffset += featurePanel.height;
		}
		
		this.height = BASE_HEIGHT + yOffset;
		
	    super.extractRenderState(graphics, mouseX, mouseY, a);
	    
	    if (dragging) {
	        float newX = (float)(mouseX - startDragX);
	        float newY = (float)(mouseY - startDragY);
	        graphics.outline(newX, newY, newX + this.width, newY + this.height, 0.5f, Color.white);
	    }
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
	    if (super.mouseClicked(event, doubleClick)) return true;

	    if (isHovered(event.x(), event.y(), getX(), getY(), width, BASE_HEIGHT)) {
	    	switch (event.button()) {
				case 0:
		            startDragX = event.x() - getX();
		            startDragY = event.y() - getY();
		            dragging = true;
					break;
			}
	    	
	        return true;
	    }

	    return false;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
	    if (super.mouseReleased(event)) return true;

	    if (dragging) {
	        setX((int) (event.x() - startDragX));
	        setY((int) (event.y() - startDragY));
	        dragging = false;
	        return true;
	    }

	    return false;
	}

	public List<FeaturePanel> getFeaturePanels() {
		return featurePanels;
	}

	public FeatureCategory getCategory() {
		return category;
	}
	
	public FeaturePanel getFeaturePanelByName(String name) {
		for(FeaturePanel panel : featurePanels) {
			if(panel.getFeature().name.contentEquals(name)) {
				return panel;
			}
		}
		return null;
	}
}