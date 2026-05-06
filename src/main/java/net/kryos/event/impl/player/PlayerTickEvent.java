package net.kryos.event.impl.player;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.player.PlayerTickListener;

public final class PlayerTickEvent {
    public static final class Pre extends Event<PlayerTickListener> {
        @Override
        public void post(PlayerTickListener listener) {
            listener.onPre(this);
        }

        @Override
        public Class<PlayerTickListener> getListenerType() {
            return PlayerTickListener.class;
        }
    }

    public static final class Post extends Event<PlayerTickListener> {
        @Override
        public void post(PlayerTickListener listener) {
            listener.onPost(this);
        }

        @Override
        public Class<PlayerTickListener> getListenerType() {
            return PlayerTickListener.class;
        }
    }
}
