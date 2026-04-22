package net.kryos.rotation;

import java.util.ArrayList;
import java.util.List;

import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.impl.RotateAvatarEvent;
import net.kryos.event.impl.SendRotationEvent;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.event.listener.impl.RotateAvatarListener;
import net.kryos.event.listener.impl.SendRotationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RotationBus implements SendRotationListener, RotateAvatarListener, PlayerTickListener {
	private static final Minecraft mc = Minecraft.getInstance();
	private final List<Rotator> rotators = new ArrayList<>();
	public boolean rotating;
	public boolean lastRotating;
	public float yaw;
	public float pitch;
	
	public void subscribe(Rotator rotating) {
		if(!rotators.contains(rotating))
			rotators.add(rotating);
	}
	
	public void unsubscribe(Rotator rotating) {
		if(rotators.contains(rotating))
			rotators.remove(rotating);
	}
	
	public boolean rotate(float yaw, float pitch, Rotator rotator) {
	    if (!rotators.contains(rotator))
	        return false;

	    for (Rotator r : rotators) {
	        if (r.getRotationPrivilege().value > rotator.getRotationPrivilege().value) {
	            return false;
	        }
	    }

	    if (rotating)
	        return false;

	    rotating = true;
	    this.yaw = yaw;
	    this.pitch = pitch;

	    return true;
	}

	@Override
	public void sendRotation(SendRotationEvent event) {
		lastRotating = rotating;
		if(rotating) {
			event.setYaw(yaw);
			event.setPitch(pitch);
			rotating = false;
		}
	}
	
	public boolean midRotation() {
		return rotating || lastRotating;
	}

	@Override
	public void rotateAvatar(RotateAvatarEvent event) {
		if(midRotation() && event.getState().id == mc.player.getId()) {
			mc.player.setYBodyRot(yaw);
			mc.player.setYHeadRot(yaw);
			event.getState().xRot = pitch;
		}
	}

	@Override
	public void onPre(Pre event) {
		
	}

	@Override
	public void onPost(Post event) {
		
	}
	
	public Vec3 getRotationVector() {
	    float yawRad = (float) Math.toRadians(this.yaw);
	    float pitchRad = (float) Math.toRadians(this.pitch);

	    float x = -Mth.sin(yawRad) * Mth.cos(pitchRad);
	    float y = -Mth.sin(pitchRad);
	    float z =  Mth.cos(yawRad) * Mth.cos(pitchRad);

	    return new Vec3(x, y, z);
	}

}