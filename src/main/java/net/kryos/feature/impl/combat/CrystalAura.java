package net.kryos.feature.impl.combat;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.kryos.Kryos;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.player.PlayerTickListener;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.LockingFeature;
import net.kryos.lock.LockPrivilege;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.BooleanSetting;
import net.kryos.setting.impl.EnumSetting;
import net.kryos.setting.impl.FloatSetting;
import net.kryos.util.entity.CrystalUtil;
import net.kryos.util.entity.DamageUtil;
import net.kryos.util.entity.EntityUtil;
import net.kryos.util.item.InventoryUtil;
import net.kryos.util.level.BlockUtil;
import net.kryos.util.math.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrystalAura extends LockingFeature implements PlayerTickListener {
	// Moves the position scan to a different thread if enabled
	private Setting<Boolean> async = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("async")
			.name("Async")
			.build());
	
	// Places crystals if enabled
	private Setting<Boolean> place = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("place")
			.name("Place")
			.build());

	// The hand with which the crystal is placed
	private Setting<InteractionHand> hand = addSetting(new EnumSetting.EnumSettingBuilder<InteractionHand>()
			.id("hand")
			.name("Hand")
			.defaultValue(InteractionHand.OFF_HAND)
			.requirement(() -> place.getValue())
			.build());
	
	// Destroys crystals if enabled
	private Setting<Boolean> destroy = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("destroy")
			.name("Destroy")
			.build());
	
	// Removes attacked crystals earlier client side. Can cause desync on high ping but can also double crystal speed
	private Setting<Boolean> earlyRemove = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("early_remove")
			.name("Early remove")
			.build());

	// Maximum distance for placing/destroying actions
	private Setting<Float> reach = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("reach")
			.name("Reach")
			.min(1)
			.max(10)
			.step(0.05F)
			.requirement(() -> place.getValue() || destroy.getValue())
			.build());
	
	// Rotates for actions placing/destroying if enabled
	private Setting<Boolean> rotate = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("rotate")
			.name("Rotate")
			.build());

	// Prevents crystals from killing the local player
	private Setting<Boolean> antiSuicide = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("anti_suicide")
			.name("Anti suicide")
			.build());

	// Minimum damage the local player has to remain with after an explosion
	private Setting<Float> minSuicideHealth = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("min_suicide_health")
			.name("Min health")
			.min(1.0F)
			.max(20.0F)
			.step(0.5F)
			.requirement(() -> antiSuicide.getValue())
			.build());

	// Maximum damage allowed to be dealt to the local player
	private Setting<Float> maxSuicideDamage = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("max_suicide_damage")
			.name("Max suicide damage")
			.min(1.0F)
			.max(20.0F)
			.step(0.5F)
			.requirement(() -> antiSuicide.getValue())
			.build());

	// Minimum damage required for a place/break 
	private Setting<Float> minDamage = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("min_damage")
			.name("Min damage")
			.min(1.0F)
			.max(20.0F)
			.step(0.5F)
			.requirement(() -> destroy.getValue())
			.build());
	
	// The amount of crystals until death at which minimum damage is ignored
	private Setting<Float> lethalMultiplier = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("lethal_multiplier")
			.name("Lethal multiplier")
			.min(1.0F)
			.max(20.0F)
			.step(1F)
			.requirement(() -> place.getValue() || destroy.getValue())
			.build());
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private volatile Optional<Map.Entry<BlockPos, Float>> placePos = Optional.empty();
	private volatile Optional<Map.Entry<EndCrystal, Float>> destroyEntity = Optional.empty();
	private volatile boolean scanInProgress = false;
	
	public CrystalAura() {
		super("crystal_aura", "Crystal Aura", FeatureCategory.COMBAT, Optional.empty(), LockPrivilege.HIGH);
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
	public void onPre(Pre event) {
		if(async.getValue()) {
			// Asynchronous
		    if (!scanInProgress) {
		        scanInProgress = true;
	
		        executor.submit(() -> {
		            try {
		                placePos = getPlace();
		                destroyEntity = getDestroy();
		            } finally {
		                scanInProgress = false;
		            }
		        });
		    }
		} else {
			// Synchronized
            placePos = getPlace();
		}
		
        destroyEntity = getDestroy();
        
		// Destroy
		if (destroy.getValue() && destroyEntity.isPresent()) {
		    var destroy = destroyEntity.get();
		    mc.gameMode.attack(mc.player, destroy.getKey());
		    mc.player.swing(InteractionHand.MAIN_HAND);
		    
		    // Early removal
		    if(earlyRemove.getValue()) {
		    	mc.level.removeEntity(destroy.getKey().getId(), RemovalReason.KILLED);
		    }
		    
			float[] rot = RotationUtil.getRotationsTo(destroy.getKey().position());
			Kryos.rotationManager.rotate(rot[0], rot[1]);
		}
		
		// Place
		if (place.getValue() && placePos.isPresent()) {
		    var place = placePos.get();
			InteractionHand hand = this.hand.getValue();
			
			if(!InventoryUtil.hasItemIn(Items.END_CRYSTAL, hand))
				return;
			
			if(rotate.getValue()) {
				float[] rot = RotationUtil.getRotationsTo(place.getKey(), Direction.UP);
				Kryos.rotationManager.rotate(rot[0], rot[1]);
			}
			
	        mc.gameMode.useItemOn(
	                mc.player,
	                hand,
	                new BlockHitResult(
	                        BlockUtil.getClosestPointOnFace(place.getKey(), Direction.UP),
	                        Direction.UP,
	                        place.getKey(),
	                        false
	                )
	        );
		    mc.player.swing(hand);
	    }
	}


	@Override
	public void onPost(Post event) {
		
	}
	
	private Optional<Map.Entry<BlockPos, Float>> getPlace() {
		int reach = (int) (this.reach.getValue() + 1);
		
		BlockPos bestPos = null;
	    float bestDamage = 0f;
	    
		for(int i = -reach; i < reach; i++) {
			for(int j = -reach; j < reach; j++) {
				for(int k = -reach; k < reach; k++) {
					// Place availability
					BlockPos pos = mc.player.blockPosition().offset(i, j, k);
					Block block = mc.level.getBlockState(pos).getBlock();
					
					if(block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) continue;
					
					BlockPos above = pos.above();
					Block aboveBlock = mc.level.getBlockState(above).getBlock();

					if(aboveBlock != Blocks.AIR) continue;

					if(CrystalUtil.placeIntercect(above)) continue;

					// In range
					Vec3 point = BlockUtil.getClosestPointOnFace(pos, Direction.UP);
					if(point.distanceTo(mc.player.getEyePosition()) > this.reach.getValue()) continue;

					float damage = 0;
					
					float self = DamageUtil.getCrystalDamage(mc.player, mc.player.position(), above.getBottomCenter());

					for(Entity entity : mc.level.entitiesForRendering()) {
						if(entity == mc.player) continue;
						
						if(entity.distanceTo(mc.player) > 16) continue;
						
						if(!(entity instanceof LivingEntity living)) continue;

						float d = DamageUtil.getCrystalDamage(living, living.position(), above.getBottomCenter());
						
						// Minimum damage. It gets ignore if the crystal count is lethal
						boolean lethal = living.getHealth() - (d * lethalMultiplier.getValue()) <= 0f;
						if(d < minDamage.getValue() && !lethal) continue;

						if(antiSuicide.getValue()) {
							if(self > maxSuicideDamage.getValue()) continue;
							
							// Ignores the position if it would bring the player below minimum health
							if(mc.player.getHealth() - self < minSuicideHealth.getValue()) continue;
						}
						
						if(d > damage) {
							damage = d;
						}
					}
					
					if (damage > bestDamage) {
	                    bestDamage = damage;
	                    bestPos = pos;
	                }
				}
			}
		}

	    if (bestPos == null) return Optional.empty();

	    return Optional.of(Map.entry(bestPos, bestDamage));
	}
	

	private Optional<Map.Entry<EndCrystal, Float>> getDestroy() {
	    EndCrystal bestCrystal = null;
	    float bestDamage = 0f;

	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (!(entity instanceof EndCrystal crystal)) continue;

	        // Reach check
	        if (EntityUtil.getClosestPoint(crystal).distanceTo(mc.player.getEyePosition()) > this.reach.getValue())
	            continue;

	        float self = DamageUtil.getCrystalDamage(mc.player, mc.player.position(), crystal.position());
	        float damage = 0f;

	        for (Entity target : mc.level.entitiesForRendering()) {
	            if (target == mc.player) continue;
	            if (target.distanceTo(mc.player) > 16) continue;

	            if (!(target instanceof LivingEntity living)) continue;

	            float d = DamageUtil.getCrystalDamage(living, living.position(), crystal.position());

				// Minimum damage. It gets ignore if the crystal count is lethal
	            boolean lethal = living.getHealth() - (d * lethalMultiplier.getValue()) <= 0f;
	            if (d < minDamage.getValue() && !lethal) continue;

	            if (antiSuicide.getValue()) {
	                if (self > maxSuicideDamage.getValue()) continue;

					// Ignores the position if it would bring the player below minimum health
	                if (mc.player.getHealth() - self < minSuicideHealth.getValue()) continue;
	            }

	            if (d > damage) {
	                damage = d;
	            }
	        }

	        if (damage > bestDamage) {
	            bestDamage = damage;
	            bestCrystal = crystal;
	        }
	    }

	    if (bestCrystal == null) return Optional.empty();

	    return Optional.of(Map.entry(bestCrystal, bestDamage));
	}
}