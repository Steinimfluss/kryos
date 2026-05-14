package net.kryos.event.impl.entity;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.entity.SpawnEntityListener;
import net.minecraft.world.entity.Entity;

public class SpawnEntityEvent extends Event<SpawnEntityListener> {
	private Entity entity;

	public SpawnEntityEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public void post(SpawnEntityListener listener) {
		listener.spawn(this);
	}

	@Override
	public Class<SpawnEntityListener> getListenerType() {
		return SpawnEntityListener.class;
	}
}