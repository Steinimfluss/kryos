package net.kryos.feature.impl.render;

import java.util.List;
import java.util.Optional;

import net.kryos.Kryos;
import net.kryos.event.impl.render.GuiExtractionEvent;
import net.kryos.event.listener.impl.render.GuiExtractionListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FeatureList extends Feature implements GuiExtractionListener {
	
	public FeatureList() {
		super("feature_list", "FeatureList", FeatureCategory.RENDER, Optional.empty());
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
	            .sorted((a, b) -> Integer.compare(mc.font.width(b.getDisplayName()), mc.font.width(a.getDisplayName())))
	            .toList();

	    for (Feature feature : sorted) {
	    	if(!feature.isEnabled())
	    		continue;
	    	
	    	if(!feature.shouldDisplay())
	    		continue;
	    	
	        int height = mc.font.lineHeight + 4;

	        graphics.text(
	                mc.font,
	                feature.getDisplayName(),
	                mc.getWindow().getGuiScaledWidth() - mc.font.width(feature.getDisplayName()) - 2,
	                yOffset + 2,
	                -1
	        );

	        yOffset += height;
	    }
	}
}