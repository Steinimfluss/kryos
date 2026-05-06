package net.kryos.event.impl.render;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.render.GuiExtractionListener;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class GuiExtractionEvent extends Event<GuiExtractionListener> {
	private GuiGraphicsExtractor graphics;
    private DeltaTracker deltaTracker;
    
	public GuiExtractionEvent(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		this.graphics = graphics;
		this.deltaTracker = deltaTracker;
	}

	@Override
	public void post(GuiExtractionListener listener) {
		listener.onExtraction(this);
	}

	@Override
	public Class<GuiExtractionListener> getListenerType() {
		return GuiExtractionListener.class;
	}

	public GuiGraphicsExtractor getGraphics() {
		return graphics;
	}

	public DeltaTracker getDeltaTracker() {
		return deltaTracker;
	}
}