package net.kryos.feature.impl.movement;

import org.joml.Vector3f;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.BooleanSettingBuilder;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.NumberSettingBuilder;
import net.minecraft.world.phys.Vec3;

public class Speed extends Feature implements PlayerTickListener {
	private NumberSetting<Float> speed = new NumberSettingBuilder<Float>()
			.name("Speed")
			.value(2F)
			.min(0.1F)
			.max(10F)
			.step(0.05F)
			.build();
	
	private BooleanSetting jump = new BooleanSettingBuilder()
			.name("Jump")
			.value(true)
			.build();
	
	public Speed() {
		super("Speed", FeatureCategory.MOVEMENT);
		setSettings(speed, jump);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
	}

	@Override
	public void onPre(Pre event) {
		setSuffix(speed.getValue().toString());
	    float yawDeg = mc.player.getYRot();
	    float yawRad = (float) Math.toRadians(-yawDeg);

	    Vector3f vel = new Vector3f();

	    if (mc.options.keyUp.isDown()) {
	        vel.add(0.0f, 0.0f, 1.0f);
	    }
	    if (mc.options.keyDown.isDown()) {
	        vel.add(0.0f, 0.0f, -1.0f);
	    }
	    if (mc.options.keyLeft.isDown()) {
	        vel.add(1.0f, 0.0f, 0.0f);
	    }
	    if (mc.options.keyRight.isDown()) {
	        vel.add(-1.0f, 0.0f, 0.0f);
	    }

    	if(jump.enabled && vel.length() >= 1) {
    		if(mc.player.onGround()) mc.player.jumpFromGround();
    	}
    	
	    if (vel.lengthSquared() == 0) {
	        return;
	    }
	    
	    vel.normalize();
	    vel.rotateY(yawRad);
	    vel.mul(speed.getValue() / 5);

	    Vec3 current = mc.player.getDeltaMovement();
	    mc.player.setDeltaMovement(vel.x, current.y, vel.z);
	}


	@Override
	public void onPost(Post event) {
		
	}
}