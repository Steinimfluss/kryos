package net.kryos.feature.impl.misc;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

public class FakePlayer extends Feature {
	private RemotePlayer fakePlayer;
	
	public FakePlayer() {
		super("FakePlayer", FeatureCategory.MISC);
	}

	@Override
	protected void onEnable() {
		if(mc.level != null) {
			fakePlayer = new RemotePlayer(mc.level, new GameProfile(UUID.randomUUID(), "FakePlayer"));
			fakePlayer.copyPosition(mc.player);
			mc.level.addEntity(fakePlayer);
		} else {
			setEnabled(false);
		}
	}

	@Override
	protected void onDisable() {
		if(mc.player != null) {
			fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
			fakePlayer.onClientRemoval();
			fakePlayer = null;
		}
	}
}
