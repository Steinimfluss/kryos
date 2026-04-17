package net.kryos.event;

import java.util.ArrayList;
import java.util.List;

import net.kryos.event.listener.EventListener;

public class EventBus {
    private final List<EventListener> listeners = new ArrayList<>();

    public <T extends EventListener> void post(Event<T> event) {
        for (EventListener listener : listeners) {
            if (event.getListenerType().isInstance(listener)) {
                event.post(event.getListenerType().cast(listener));
            }
        }
    }

    public void subscribe(EventListener listener) {
    	if(!listeners.contains(listener))
    		listeners.add(listener);
    }
    
    public void unsubscribe(EventListener listener) {
    	if(listeners.contains(listener))
    		listeners.remove(listener);
    }
}