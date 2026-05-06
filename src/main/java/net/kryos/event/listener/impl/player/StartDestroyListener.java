package net.kryos.event.listener.impl.player;

import net.kryos.event.impl.player.StartDestroyEvent;
import net.kryos.event.listener.EventListener;

public interface StartDestroyListener extends EventListener {
    void startDestroy(StartDestroyEvent event);
}