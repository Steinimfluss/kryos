package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;

public class Killaura extends Feature implements PlayerTickListener, Rotator {
	public Killaura() {
		super("Killaura", FeatureCategory.COMBAT);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
		Kryos.rotationBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		Kryos.rotationBus.unsubscribe(this);
	}

	@Override
	public void onPre(Pre event) {
		Kryos.rotationBus.rotate(0, 0, this);
	}

	@Override
	public void onPost(Post event) {
		
	}

	@Override
	public RotationPrivilege getRotationPrivilege() {
		return RotationPrivilege.MEDIUM;
	}
}