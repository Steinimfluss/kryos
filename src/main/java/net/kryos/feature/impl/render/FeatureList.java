package net.kryos.feature.impl.render;

import java.util.List;

import net.kryos.Kryos;
import net.kryos.event.impl.GuiExtractionEvent;
import net.kryos.event.listener.impl.GuiExtractionListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FeatureList extends Feature implements GuiExtractionListener {
	public FeatureList() {
		super("ArrayList", FeatureCategory.RENDER);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
	}

	@Override
	public void onExtraction(GuiExtractionEvent event) {
	    GuiGraphicsExtractor graphics = event.getGraphics();

	    int yOffset = 0;

	    List<Feature> sorted = Kryos.featureManager.getFeatures().stream()
	            .sorted((a, b) -> Integer.compare(mc.font.width(b.name + (b.getSuffix() != null ? " [" + b.getSuffix() + "]" : "")), mc.font.width((a.name + (a.getSuffix() != null ? " [" + a.getSuffix() + "]" : "")))))
	            .toList();

	    for (Feature feature : sorted) {
	    	if(!feature.isEnabled())
	    		continue;
	    	
	        int height = mc.font.lineHeight + 4;

	        String text = feature.name + (feature.getSuffix() != null ? " [" + feature.getSuffix() + "]" : "");  
	        graphics.text(
	                mc.font,
	                text,
	                mc.getWindow().getGuiScaledWidth() - mc.font.width(text) - 2,
	                yOffset + 2,
	                -1
	        );

	        yOffset += height;
	    }
	}
}