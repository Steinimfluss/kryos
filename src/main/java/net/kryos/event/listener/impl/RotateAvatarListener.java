package net.kryos.event.listener.impl;

import net.kryos.event.impl.RotateAvatarEvent;
import net.kryos.event.listener.EventListener;

public interface RotateAvatarListener extends EventListener {
	void rotateAvatar(RotateAvatarEvent event);
}