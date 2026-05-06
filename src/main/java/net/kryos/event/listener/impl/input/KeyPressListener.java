package net.kryos.event.listener.impl.input;

import net.kryos.event.impl.input.KeyPressEvent;
import net.kryos.event.listener.EventListener;

public interface KeyPressListener extends EventListener {
    void onKeyPress(KeyPressEvent event);
}