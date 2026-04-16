package net.kryos.event.listener.impl;

import net.kryos.event.impl.RenderArmWithItemEvent;
import net.kryos.event.listener.EventListener;

public interface RenderArmWithItemListener extends EventListener {
    void onArmRender(RenderArmWithItemEvent event);
}