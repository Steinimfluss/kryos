package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.KeyPressListener;
import net.minecraft.client.input.KeyEvent;

public class KeyPressEvent extends Event<KeyPressListener> {
    private final long handle;
    private final int action;
    private final KeyEvent event;

    public KeyPressEvent(long handle, int action, KeyEvent event) {
        this.handle = handle;
        this.action = action;
        this.event = event;
    }

    @Override
    public Class<KeyPressListener> getListenerType() {
        return KeyPressListener.class;
    }

    @Override
    public void post(KeyPressListener listener) {
        listener.onKeyPress(this);
    }

    public long getHandle() { return handle; }
    public int getAction() { return action; }
    public KeyEvent getEvent() { return event; }
}