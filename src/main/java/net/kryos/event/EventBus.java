package net.kryos.event;

import java.util.ArrayList;
import java.util.List;

import net.kryos.event.listener.EventListener;

public class EventBus {
	private final List<EventListener> listeners = new ArrayList<>();
	
	public void post(Event event) {
		listeners.forEach(listener -> {
			if(event.getListenerType().isInstance(listener)) {
				event.post(listener);
			}
		});
	}
	
	public void subscribe(EventListener listener) {
		listeners.add(listener);
	}
}