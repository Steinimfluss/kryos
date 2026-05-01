package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.AttackListener;

public class AttackEvent extends Event<AttackListener> {
	@Override
	public void post(AttackListener listener) {
		listener.attack(this);
	}

	@Override
	public Class<AttackListener> getListenerType() {
		return AttackListener.class;
	}	
}