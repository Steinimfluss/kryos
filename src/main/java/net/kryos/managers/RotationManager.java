package net.kryos.managers;

import net.kryos.event.impl.player.SendPositionEvent;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.impl.render.RotateAvatarEvent;
import net.kryos.event.listener.impl.player.PlayerTickListener;
import net.kryos.event.listener.impl.player.SendRotationListener;
import net.kryos.event.listener.impl.render.RotateAvatarListener;
import net.minecraft.client.Minecraft;

public class RotationManager implements PlayerTickListener, SendRotationListener, RotateAvatarListener {
	private static final Minecraft mc = Minecraft.getInstance();
	
	private boolean rotating;
	private boolean lastRotating;
	
	private float yaw;
	private float pitch;
	private float serverYaw;
	private float serverPitch;
	
	@Override
	public void onPre(Pre event) {
		lastRotating = rotating;
		rotating = false;
	}

	@Override
	public void onPost(Post event) {
		if(lastRotating || rotating) {
			mc.player.setYHeadRot(yaw);
			mc.player.setYBodyRot(yaw);
		}
	}
	
	public void rotate(float yaw, float pitch) {
	    this.yaw = yaw;
	    this.pitch = pitch;

	    rotating = true;
	}

	@Override
	public void sendRotation(SendPositionEvent event) {
		lastRotating = rotating;
		if(rotating) {
			event.setYaw(yaw);
			event.setPitch(pitch);
			
		    rotating = false;
		}
		serverYaw = event.getYaw();
		serverPitch = event.getPitch();
	}
	
	@Override
	public void rotateAvatar(RotateAvatarEvent event) {
		if(event.getState().id == mc.player.getId() && (rotating || lastRotating))
			event.getState().xRot = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getServerYaw() {
		return serverYaw;
	}

	public float getServerPitch() {
		return serverPitch;
	}
}