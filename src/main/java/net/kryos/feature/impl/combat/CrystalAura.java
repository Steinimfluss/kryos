package net.kryos.feature.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.kryos.util.DamageUtil;
import net.kryos.util.RotationUtil;
import net.kryos.util.Timer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrystalAura extends Feature implements PlayerTickListener, Rotator {
	private ModeSetting sortMode = new ModeSetting("Sort", "Distance", "Distance", "Health");
	
	private NumberSetting<Integer> reach = new NumberSetting<Integer>("Reach", 10, 1, 20, 1);
	private NumberSetting<Long> delaySetting = new NumberSetting<Long>("Delay", 50L, 0L, 1000L, 10L); 
	private NumberSetting<Float> minDamage = new NumberSetting<Float>("Min Target Damage", 10.0F, 0F, 100.0F, 0.5F); 
	private NumberSetting<Float> maxDamage = new NumberSetting<Float>("Max Self Damage", 10.0F, 0F, 100.0F, 0.5F);
	private NumberSetting<Float> rapidThreshold = new NumberSetting<Float>("Rapid threshold", 10.0F, 0F, 100.0F, 0.5F); 
	
	private Timer delay = new Timer();
	
	public CrystalAura() {
		super("CrystalAura", FeatureCategory.COMBAT);
		setSettings(sortMode, reach, minDamage, maxDamage, delaySetting, rapidThreshold);
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
		Kryos.rotationBus.unsubscribe(this);
		if(mc.screen != null || mc.player == null) return;
		
		Scored<BlockPos> place = getBestPos();
		Scored<EndCrystal> destroy = getBestDestroy();
		
		if(!delay.check(delaySetting.getValue()) && ((place != null && place.score < rapidThreshold.getValue()) && (destroy != null && destroy.score < rapidThreshold.getValue()))) {
			return;
		}
		delay.reset();
		
		InteractionHand crystalHand = null;

		if(mc.player.getMainHandItem() != null && mc.player.getMainHandItem().getItem() == Items.END_CRYSTAL) crystalHand = InteractionHand.MAIN_HAND;
		if(mc.player.getOffhandItem() != null && mc.player.getOffhandItem().getItem() == Items.END_CRYSTAL) crystalHand = InteractionHand.OFF_HAND;
		
		if(place != null && (destroy == null || place.score() > destroy.score()) && crystalHand != null) {
			Kryos.rotationBus.subscribe(this);
	        Vec3 hit = place.object().above().getBottomCenter();
			float[] rot = RotationUtil.getRotationsTo(hit);
			if(Kryos.rotationBus.rotate(rot[0], rot[1], this)) {
		        BlockHitResult bhr = new BlockHitResult(
		                hit,
		                Direction.UP,
		                place.object(),
		                false
		        );
	
		        mc.gameMode.useItemOn(
		                mc.player,
		                crystalHand,
		                bhr
		        ).consumesAction();
		        mc.player.swing(crystalHand);
			}
		} else if(destroy != null) {
			Kryos.rotationBus.subscribe(this);
			float[] rot = RotationUtil.getRotationsTo(destroy.object().position());
			if(Kryos.rotationBus.rotate(rot[0], rot[1], this)) {
				mc.gameMode.attack(mc.player, destroy.object());
				mc.player.swing(InteractionHand.MAIN_HAND);
			}
		}
	}

	@Override
	public void onPost(Post event) {

	}
	
	public Scored<EndCrystal> getBestDestroy() {
	    List<EndCrystal> crystals = new ArrayList<>();
	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (entity instanceof EndCrystal crystal) crystals.add(crystal);
	    }

	    List<LivingEntity> targets = new ArrayList<>();
	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (entity instanceof LivingEntity living && living != mc.player && living.isAlive()) {
	            targets.add(living);
	        }
	    }

	    return crystals.stream()
	            .flatMap(crystal -> targets.stream()
	                    .map(t -> {
	                        float targetDamage = DamageUtil.getCrystalDamage(
	                                t,
	                                new Vec3(t.getX(), t.getY(), t.getZ()),
	                                crystal.position()
	                        );

	                        float selfDamage = DamageUtil.getCrystalDamage(
	                                mc.player,
	                                new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
	                                crystal.position()
	                        );
	                        
	                        if(mc.player.distanceTo(crystal) > reach.getValue()) return null;

	                        if (targetDamage < minDamage.getValue()) return null;
	                        if (selfDamage > maxDamage.getValue()) return null;

	                        return new Scored<>(crystal, targetDamage);
	                    })
	            )
	            .filter(x -> x != null)
	            .max(Comparator.comparingDouble(Scored::score))
	            .orElse(null);
	}

	public Scored<BlockPos> getBestPos() {
	    List<BlockPos> positions = getPlaceableBlocks(mc.player.blockPosition());

	    List<LivingEntity> targets = new ArrayList<>();
	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (entity instanceof LivingEntity living && living != mc.player && living.isAlive()) {
	            targets.add(living);
	        }
	    }

	    return positions.stream()
	            .map(pos -> {
	                Vec3 crystalPos = pos.above().getBottomCenter();

	                float bestTargetDamage = (float) targets.stream()
	                        .mapToDouble(t -> DamageUtil.getCrystalDamage(
	                                t,
	                                new Vec3(t.getX(), t.getY(), t.getZ()),
	                                crystalPos
	                        ))
	                        .max()
	                        .orElse(0);

	                float selfDamage = DamageUtil.getCrystalDamage(
	                        mc.player,
	                        new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
	                        crystalPos
	                );

	                if (bestTargetDamage < minDamage.getValue()) return null;
	                if (selfDamage > maxDamage.getValue()) return null;

	                return new Scored<>(pos, bestTargetDamage);
	            })
	            .filter(x -> x != null)
	            .max(Comparator.comparingDouble(Scored::score))
	            .orElse(null);
	}

	public List<BlockPos> getPlaceableBlocks(BlockPos player) {
		List<BlockPos> positions = new ArrayList<BlockPos>();
		
		for(int i = -reach.getValue(); i < reach.getValue(); i++) {
			for(int j = -reach.getValue(); j < reach.getValue(); j++) {
				for(int k = -reach.getValue(); k < reach.getValue(); k++) {
					BlockPos pos = player.offset(i, j, k);
					BlockPos above = player.offset(i, j + 1, k);
					
					if(above.getBottomCenter().distanceTo(mc.player.getEyePosition()) > reach.getValue()) continue;
					
					Block posBlock = mc.level.getBlockState(pos).getBlock();
					Block aboveBlock = mc.level.getBlockState(above).getBlock();
					
					if(posBlock != Blocks.OBSIDIAN && posBlock != Blocks.BEDROCK)
						continue;
					if(aboveBlock != Blocks.AIR)
						continue;
					if(playerIntersects(above))
						continue;
					if(entityIntersects(above))
						continue;
					
					positions.add(pos);
				}
			}
		}
		return positions;
	}

    private boolean playerIntersects(BlockPos pos) {
        var box = new net.minecraft.world.phys.AABB(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
        );

        return mc.player.getBoundingBox().intersects(box);
    }

    private boolean entityIntersects(BlockPos pos) {
        var box = new net.minecraft.world.phys.AABB(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
        );

        for (var entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) continue;

            if (entity.getBoundingBox().intersects(box)) {
                return true;
            }
        }

        return false;
    }
	
	public LivingEntity getBestTarget() {
	    LivingEntity best = null;

	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (!(entity instanceof LivingEntity)) continue;
	        if(entity == mc.player) continue;
	        LivingEntity e = (LivingEntity) entity;

	        if (e == mc.player) continue;
	        if (e.isDeadOrDying()) continue;

	        if(!(e instanceof Player))
	        	continue;

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
	public RotationPrivilege getRotationPrivilege() {
		return RotationPrivilege.LOW;
	}
	
	public record Scored<T>(T object, float score) {}
}