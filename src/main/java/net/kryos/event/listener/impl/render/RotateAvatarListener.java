package net.kryos.event.listener.impl.render;

import net.kryos.event.impl.render.RotateAvatarEvent;
import net.kryos.event.listener.EventListener;

public interface RotateAvatarListener extends EventListener {
	void rotateAvatar(RotateAvatarEvent event);
}