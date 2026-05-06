package net.kryos.event.listener.impl.player;

import net.kryos.event.impl.player.PlayerTickEvent;
import net.kryos.event.listener.EventListener;

public interface PlayerTickListener extends EventListener {
    void onPre(PlayerTickEvent.Pre event);
    
    void onPost(PlayerTickEvent.Post event);
}