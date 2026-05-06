package net.kryos.util.entity;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

public class FakePlayerEntity extends RemotePlayer {
	private boolean pushable;
	
	public FakePlayerEntity(LocalPlayer localPlayer, ClientLevel level, GameProfile gameProfile) {
		super(level, gameProfile);
		
		yHeadRot = localPlayer.yHeadRot;
	}
	
	@Override
	public void push(Entity entity) {
		if(pushable) {
			super.push(entity);
		}
	}
	
	@Override
	protected void pushEntities() {
		if(pushable) {
			super.pushEntities();
		}
	}
	
	public void setPushable(boolean pushable) {
		this.pushable = pushable;
	}
}