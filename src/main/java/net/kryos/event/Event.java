package net.kryos.event;

import net.kryos.event.listener.EventListener;

public abstract class Event<T extends EventListener> {
    public abstract void post(T listener);
    public abstract Class<T> getListenerType();
}