package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.kryos.util.EntityUtil;
import net.kryos.util.RotationUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class Killaura extends Feature implements PlayerTickListener, Rotator {
	private ModeSetting swingMode = new ModeSetting("Swing", "Client", "Client", "None");
	private ModeSetting sortMode = new ModeSetting("Sort", "Distance", "Distance", "Health");

	private BooleanSetting keepRotation = new BooleanSetting("Keep Rotation");
	
	private BooleanSetting animalSetting = new BooleanSetting("Animals");
	private BooleanSetting mobSetting = new BooleanSetting("Mobs");
	private BooleanSetting playerSetting = new BooleanSetting("Players");
	
	
	public Killaura() {
		super("Killaura", FeatureCategory.COMBAT);
		setSettings(swingMode, sortMode, animalSetting, mobSetting, playerSetting, keepRotation);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		Kryos.rotationBus.unsubscribe(this);
	}

	@Override
	public void onPre(Pre event) {
		if(mc.player == null || mc.screen != null) return;
		if(mc.player.isUsingItem()) return;
		
		LivingEntity target = getBestTarget();
		
		if(target != null && EntityUtil.isInRange(target, mc.player.entityInteractionRange())) {
			if(isAttackReady() || keepRotation.enabled) {
				Kryos.rotationBus.subscribe(this);
				float[] rotations = RotationUtil.getRotationsTo(target);
			
				if(Kryos.rotationBus.rotate(rotations[0], rotations[1], this)) {
					if(isAttackReady()) {
						mc.gameMode.attack(mc.player, target);
						if(swingMode.getValue().equalsIgnoreCase("Client"))
							mc.player.swing(InteractionHand.MAIN_HAND);
					}
				}
			}
		}
		Kryos.rotationBus.unsubscribe(this);
	}
	
	public boolean isAttackReady() {
	    return mc.player.getAttackStrengthScale(0) >= 1F;
	}
	
	public LivingEntity getBestTarget() {
	    LivingEntity best = null;

	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (!(entity instanceof LivingEntity)) continue;
	        LivingEntity e = (LivingEntity) entity;

	        if (e == mc.player) continue;
	        if (e.isDeadOrDying()) continue;

	        switch (e.getType().getCategory()) {
	            case CREATURE:
	            case WATER_CREATURE:
	            case AMBIENT:
	                if (!animalSetting.enabled) continue;
	                break;

	            case MONSTER:
	                if (!mobSetting.enabled) continue;
	                break;

	            case MISC:
	                if (!playerSetting.enabled) continue;
	                break;

	            default:
	                continue;
	        }

	        if (best == null) {
	            best = e;
	            continue;
	        }

	        if (sortMode.getValue().equalsIgnoreCase("Distance")) {
	            double d1 = mc.player.distanceToSqr(e);
	            double d2 = mc.player.distanceToSqr(best);
	            if (d1 < d2) best = e;
	        } else {
	            float h1 = e.getHealth();
	            float h2 = best.getHealth();
	            if (h1 < h2) best = e;
	        }
	    }

	    return best;
	}

	@Override
	public void onPost(Post event) {
		
	}

	@Override
	public RotationPrivilege getRotationPrivilege() {
		return RotationPrivilege.LOWEST;
	}
}