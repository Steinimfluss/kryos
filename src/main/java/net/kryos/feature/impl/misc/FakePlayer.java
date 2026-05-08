package net.kryos.feature.impl.misc;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.kryos.Kryos;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.player.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.BooleanSetting;
import net.kryos.setting.impl.StringSetting;
import net.kryos.util.entity.FakePlayerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class FakePlayer extends Feature implements PlayerTickListener {
	public Setting<Boolean> copyInventory = addSetting(
			new BooleanSetting.BooleanSettingBuilder()
				.id("copy_inventory")
				.name("Copy inventory")
				.defaultValue(true)
				.description(Component.literal("Copies the local players inventory"))
				.build()
			);
	
	public Setting<Boolean> pushable = addSetting(
			new BooleanSetting.BooleanSettingBuilder()
				.id("pushable")
				.name("Pushable")
				.defaultValue(false)
				.build()
			);
	
	public Setting<String> name = addSetting(
			new StringSetting.StringSettingBuilder()
				.id("name")
				.name("Name")
				.defaultValue("Fake Player")
				.description(Component.literal("The username the fake player has"))
				.build()
			);
	
	public FakePlayer() {
		super("fake_player", "Fake Player", FeatureCategory.MISC, Component.literal("A fake player for testing features on servers"));
	}

	private FakePlayerEntity fakePlayer;

	@Override
	protected void onEnable() {
		if(mc.level == null) return;
		Kryos.eventBus.subscribe(this);
		
		fakePlayer = new FakePlayerEntity(mc.player, mc.level, new GameProfile(UUID.randomUUID(), name.getValue()));
		
		if(copyInventory.getValue())
			fakePlayer.getInventory().replaceWith(mc.player.getInventory());
		
		fakePlayer.copyPosition(mc.player);
		
		mc.level.addEntity(fakePlayer);
	}

	@Override
	protected void onDisable() {
		if(mc.level == null || fakePlayer == null) return;
		
		Kryos.eventBus.unsubscribe(this);
		
		fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
		fakePlayer.onClientRemoval();
		
	}

	@Override
	public void onPre(Pre event) {
		fakePlayer.setPushable(pushable.getValue());
	}

	@Override
	public void onPost(Post event) {
		
	}
}