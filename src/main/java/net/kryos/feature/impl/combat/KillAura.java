package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.NumberSettingBuilder;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.kryos.util.EntityUtil;
import net.kryos.util.RotationUtil;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class KillAura extends Feature implements Rotator, PlayerTickListener {
	private NumberSetting<Float> reach = new NumberSettingBuilder<Float>()
			.name("Reach")
			.value(8F)
			.min(0F)
			.max(10F)
			.step(0.1F)
			.build();
	
	public KillAura() {
		super("Killaura", FeatureCategory.COMBAT);
		addSettings(reach);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
		Kryos.rotationBus.unsubscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		Kryos.rotationBus.unsubscribe(this);
	}

	public LivingEntity getBestTarget() {
	    LivingEntity best = null;
	    float bestAngle = Float.MAX_VALUE;

	    Vec3 eyePos = mc.player.getEyePosition();
	    Vec3 lookVec = mc.player.getLookAngle();

	    for (Entity e : mc.level.entitiesForRendering()) {
	        if (!(e instanceof LivingEntity target)) continue;
	        if (target == mc.player) continue;
	        if (!target.isAlive()) continue;
	        if(EntityUtil.getClosestPoint(target).distanceTo(mc.player.getEyePosition()) > reach.getValue()) continue;

	        Vec3 toTarget = target.getEyePosition().subtract(eyePos).normalize();

	        double dot = lookVec.dot(toTarget);
	        dot = Mth.clamp(dot, -1.0, 1.0);
	        float angle = (float) Math.acos(dot);

	        if (angle < bestAngle) {
	            bestAngle = angle;
	            best = target;
	        }
	    }

	    return best;
	}

	@Override
	public void onPre(Pre event) {
		Kryos.rotationBus.unsubscribe(this);
		setSuffix(null);
		LivingEntity target = getBestTarget();
		
		if(target == null) return;
		
		float[] rot = RotationUtil.getRotationsTo(EntityUtil.getClosestPoint(target));
		
		Kryos.rotationBus.subscribe(this);
		if(Kryos.rotationBus.rotate(rot[0], rot[1], this)) {
			setSuffix(target.getPlainTextName());
			if(isCooldownReady()) {
				mc.gameMode.attack(mc.player, target);
				mc.player.swing(InteractionHand.MAIN_HAND);
			}
		}
	}
	
	public boolean isCooldownReady() {
	    return mc.player.getAttackStrengthScale(0) >= 1.0f;
	}

	@Override
	public void onPost(Post event) {
		
	}

	@Override
	public RotationPrivilege getRotationPrivilege() {
		return RotationPrivilege.MEDIUM;
	}
}