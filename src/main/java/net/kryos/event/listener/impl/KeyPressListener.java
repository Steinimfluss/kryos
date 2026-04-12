package net.kryos.event.listener.impl;

import net.kryos.event.impl.KeyPressEvent;
import net.kryos.event.listener.EventListener;

public interface KeyPressListener extends EventListener {
    void onKeyPress(KeyPressEvent event);
}