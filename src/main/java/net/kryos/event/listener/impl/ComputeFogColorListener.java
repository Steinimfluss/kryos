package net.kryos.event.listener.impl;

import net.kryos.event.impl.ComputeFogColorEvent;
import net.kryos.event.listener.EventListener;

public interface ComputeFogColorListener extends EventListener {
    void computeFogColor(ComputeFogColorEvent event);
}
