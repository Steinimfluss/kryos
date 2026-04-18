package net.kryos.feature.impl.render;

import java.util.List;

import net.kryos.Kryos;
import net.kryos.event.impl.GuiExtractionEvent;
import net.kryos.event.listener.impl.GuiExtractionListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.gui.MainTheme;
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
	            .sorted((a, b) -> Integer.compare(mc.font.width(b.name), mc.font.width(a.name)))
	            .toList();

	    for (Feature feature : sorted) {
	    	if(!feature.isEnabled())
	    		continue;
	    	
	        int height = mc.font.lineHeight + 4;

	        graphics.fill(mc.getWindow().getGuiScaledWidth() - mc.font.width(feature.name) - 4, yOffset,  mc.getWindow().getGuiScaledWidth(), yOffset + height, MainTheme.PRIMARY);
	        
	        graphics.text(
	                mc.font,
	                feature.name,
	                mc.getWindow().getGuiScaledWidth() - mc.font.width(feature.name) - 2,
	                yOffset + 2,
	                -1
	        );

	        yOffset += height;
	    }
	}
}