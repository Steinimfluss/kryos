package net.kryos.event.listener.impl.render;

import net.kryos.event.impl.render.GuiExtractionEvent;
import net.kryos.event.listener.EventListener;

public interface GuiExtractionListener extends EventListener {
    void onExtraction(GuiExtractionEvent event);
}