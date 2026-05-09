package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.entity.SetEntityMotionEvent;
import net.kryos.event.listener.impl.entity.SetEntityMotionListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.FloatSetting;
import net.minecraft.network.chat.Component;

public class Velocity extends Feature implements SetEntityMotionListener {
	private Setting<Float> y = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("y")
			.name("Y")
			.min(0)
			.max(2)
			.step(0.05F)
			.defaultValue(1.0F)
			.description(Component.literal("How much knockback the player takes in the Y axis"))
			.build());
	

	private Setting<Float> xz = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("xz")
			.name("XZ")
			.min(0)
			.max(2)
			.step(0.05F)
			.defaultValue(1.0F)
			.description(Component.literal("How much knockback the player takes in the X and Z axis"))
			.build());
	
	public Velocity() {
		super("velocity", "Velocity", FeatureCategory.COMBAT, Component.literal("Allows you to determine how much knockback the local player will take"));
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
		
		super.onEnable();
	}
	
	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		
		super.onDisable();
	}
	
	@Override
	public void setMotion(SetEntityMotionEvent event) {
		if(mc.player == null) return;
		
		if(event.getId() != mc.player.getId()) return;
		
		event.setMovement(event.getMovement().multiply(xz.getValue(), y.getValue(), xz.getValue()));
	}
}