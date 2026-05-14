package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.player.PlayerTickListener;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.LockingFeature;
import net.kryos.lock.LockPrivilege;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;

public class Surround extends LockingFeature implements PlayerTickListener {

	public Surround() {
		super("surround", "Surround", FeatureCategory.COMBAT, Component.literal("Places blocks around your feet"), LockPrivilege.HIGHEST);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
		
		super.onEnable();
	}
	
	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		Kryos.lockManager.free(this);
		
		super.onDisable();
	}

	@Override
	public void onPre(Pre event) {
		AABB box = mc.player.getBoundingBox();
		
		double x0 = box.minX;
		double x1 = box.maxX;
		double y0 = box.minY;
		double y1 = box.maxY;
		double z0 = box.minZ;
		double z1 = box.maxZ;
	}

	@Override
	public void onPost(Post event) {
		
	}
}