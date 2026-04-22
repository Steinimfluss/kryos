package net.kryos.event.listener.impl;

import net.kryos.event.impl.StartDestroyEvent;
import net.kryos.event.listener.EventListener;

public interface StartDestroyListener extends EventListener {
    void startDestroy(StartDestroyEvent event);
}