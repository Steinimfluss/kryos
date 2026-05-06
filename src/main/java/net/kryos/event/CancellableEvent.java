package net.kryos.event;

import net.kryos.event.listener.EventListener;

public abstract class CancellableEvent<T extends EventListener> extends Event<T> {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}