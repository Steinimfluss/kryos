package net.kryos.event.listener.impl;

import net.kryos.event.impl.AttackEvent;
import net.kryos.event.listener.EventListener;

public interface AttackListener extends EventListener {
	void attack(AttackEvent event);
}