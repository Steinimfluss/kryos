package net.kryos.feature.impl.movement;

import org.joml.Vector3f;

import net.kryos.Kryos;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.player.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.BooleanSetting;
import net.kryos.setting.impl.FloatSetting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class Speed extends Feature implements PlayerTickListener {
	private Setting<Float> strafe = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("strafe")
			.name("Strafe")
			.min(0)
			.max(10)
			.step(0.05f)
			.defaultValue(2)
			.build());
	
	private Setting<Float> speed = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("speed")
			.name("Speed")
			.min(0)
			.max(10)
			.step(0.05f)
			.defaultValue(2)
			.build());
	
	private Setting<Boolean> jump = new BooleanSetting.BooleanSettingBuilder()
			.id("jump")
			.name("Jump")
			.defaultValue(false)
			.build();
	
	public Speed() {
		super("speed", "Speed", FeatureCategory.MOVEMENT, Component.literal("Allows you to step up blocks"));
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
		if(mc.player == null) return;
		
		super.onEnable();
	}
	
	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		if(mc.player == null) return;
		
		super.onDisable();
	}

	@Override
	public void onPre(Pre event) {
		float yawDeg = mc.player.getYRot();
	    float yawRad = (float) Math.toRadians(-yawDeg);

	    Vector3f vel = new Vector3f();

	    float speed = this.speed.getValue() / 10;
	    float strafe = this.strafe.getValue() / 10;
	    
	    if (mc.options.keyUp.isDown()) {
	        vel.add(0.0f, 0.0f, speed);
	    }
	    if (mc.options.keyDown.isDown()) {
	        vel.add(0.0f, 0.0f, -strafe);
	    }
	    if (mc.options.keyLeft.isDown()) {
	        vel.add(strafe, 0.0f, 0.0f);
	    }
	    if (mc.options.keyRight.isDown()) {
	        vel.add(-strafe, 0.0f, 0.0f);
	    }

    	if(jump.getValue() && vel.length() >= 1) {
    		if(mc.player.onGround()) mc.player.jumpFromGround();
    	}
    	
	    if (vel.lengthSquared() == 0) {
	        return;
	    }
	    
	    vel.rotateY(yawRad);

	    Vec3 current = mc.player.getDeltaMovement();
	    mc.player.setDeltaMovement(vel.x, current.y, vel.z);
		
	}

	@Override
	public void onPost(Post event) {
		
	}
}