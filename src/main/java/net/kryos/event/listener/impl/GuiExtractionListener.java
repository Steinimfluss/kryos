package net.kryos.event.listener.impl;

import net.kryos.event.impl.GuiExtractionEvent;
import net.kryos.event.listener.EventListener;

public interface GuiExtractionListener extends EventListener {
    void onExtraction(GuiExtractionEvent event);
}