package net.kryos.feature.impl.combat;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.kryos.Kryos;
import net.kryos.event.impl.entity.RemoveEntityEvent;
import net.kryos.event.impl.entity.SpawnEntityEvent;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.entity.RemoveEntityListener;
import net.kryos.event.listener.impl.entity.SpawnEntityListener;
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
import net.kryos.util.math.Timer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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

public class CrystalAura extends LockingFeature implements PlayerTickListener, SpawnEntityListener, RemoveEntityListener {
	private Setting<Boolean> async = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("async")
			.name("Async")
			.description(Component.literal("Moves expensive computations onto a seperate thread for a performance improvement"))
			.build());
	
	private Setting<Boolean> precalc = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("precalc")
			.name("Pre calc")
			.description(Component.literal("Calculates positions for the next tick. Works great with early removal"))
			.build());
	
	private Setting<Boolean> place = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("place")
			.name("Place")
			.description(Component.literal("Places crystals"))
			.build());

	private Setting<InteractionHand> hand = addSetting(new EnumSetting.EnumSettingBuilder<InteractionHand>()
			.id("hand")
			.name("Hand")
			.defaultValue(InteractionHand.OFF_HAND)
			.requirement(() -> place.getValue())
			.description(Component.literal("The hand with which the crystal is placed"))
			.build());
	
	private Setting<Boolean> destroy = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("destroy")
			.name("Destroy")
			.description(Component.literal("Destroys crystals"))
			.build());
	
	private Setting<Boolean> destroyBlacklist = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("destroy_blacklist")
			.name("Destroy blacklist")
			.defaultValue(true)
			.description(Component.literal("Prevents crystals from being attacked multiple times"))
			.requirement(() -> destroy.getValue())
			.build());
	
	private Setting<Float> destroyBlacklistTimeout = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("destory_blacklist_timeout")
			.name("Destroy blacklist timeout")
			.min(10)
			.max(1000)
			.step(10)
			.defaultValue(500)
			.requirement(() -> destroy.getValue())
			.description(Component.literal("The time after which items are removed from the blacklist"))
			.build());
	
	private Setting<Boolean> earlyRemove = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("early_remove")
			.name("Early remove")
			.description(Component.literal("Removes attacked crystals earlier client side. Can cause desync on high ping but can also drastically improve crystal speed"))
			.build());

	private Setting<Float> reach = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("reach")
			.name("Reach")
			.min(1)
			.max(10)
			.step(0.05F)
			.requirement(() -> place.getValue() || destroy.getValue())
			.description(Component.literal("Maximum distance for placing/destroying actions"))
			.build());
	
	private Setting<Float> destroyDelay = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("destroy_delay")
			.name("Destroy delay")
			.min(10)
			.max(1000)
			.step(10)
			.requirement(() -> place.getValue() || destroy.getValue())
			.description(Component.literal("Delay for destroying actions"))
			.build());
	
	private Setting<Boolean> rotate = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("rotate")
			.name("Rotate")
			.description(Component.literal("Rotates for actions"))
			.build());

	private Setting<Boolean> antiSuicide = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("anti_suicide")
			.name("Anti suicide")
			.description(Component.literal("Prevents the player from being damaged too much or killed"))
			.build());

	private Setting<Float> minSuicideHealth = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("min_suicide_health")
			.name("Min health")
			.min(1.0F)
			.max(20.0F)
			.step(0.5F)
			.requirement(() -> antiSuicide.getValue())
			.description(Component.literal("Minimum damage the local player has to remain with after an explosion"))
			.build());

	private Setting<Float> maxSuicideDamage = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("max_suicide_damage")
			.name("Max suicide damage")
			.min(1.0F)
			.max(20.0F)
			.step(0.5F)
			.requirement(() -> antiSuicide.getValue())
			.description(Component.literal("Maximum damage allowed to be dealt to the local player"))
			.build());

	private Setting<Float> minDamage = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("min_damage")
			.name("Min damage")
			.min(1.0F)
			.max(20.0F)
			.step(0.5F)
			.requirement(() -> destroy.getValue())
			.description(Component.literal("Minimum damage required for a place/break"))
			.build());
	
	private Setting<Float> lethalMultiplier = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("lethal_multiplier")
			.name("Lethal multiplier")
			.min(1.0F)
			.max(20.0F)
			.step(1F)
			.requirement(() -> place.getValue() || destroy.getValue())
			.description(Component.literal("The amount of crystals until target death at which minimum damage is ignored"))
			.build());
	
	private Setting<Boolean> predict = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("predict")
			.name("Predict")
			.description(Component.literal("Uses movement predictions in order to determine a targets server side position"))
			.build());
	
	private Setting<Float> predictAmount = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("predict_amount")
			.name("Predict amount")
			.min(1.0F)
			.max(20.0F)
			.step(1F)
			.requirement(() -> predict.getValue())
			.description(Component.literal("The amount of by which movements are predicted into the future"))
			.build());
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private volatile Optional<Map.Entry<BlockPos, Float>> placePos = Optional.empty();
	private volatile Optional<Map.Entry<EndCrystal, Float>> destroyEntity = Optional.empty();
	private volatile boolean scanInProgress = false;
	
	private Int2LongMap destroyed = new Int2LongOpenHashMap();
	
	private Timer timer = new Timer();
	
	private BlockPos pos;
	
	public CrystalAura() {
		super("crystal_aura", "Crystal Aura", FeatureCategory.COMBAT, Component.literal("Uses crystals to kill enemies"), LockPrivilege.LOW);
	}
	
	@Override
	protected void onEnable() {
		placePos = Optional.empty();
		destroyEntity = Optional.empty();
		Kryos.eventBus.subscribe(this);
		super.onEnable();
	}
	
	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
    	Kryos.lockManager.free(this);
		pos = null;
		
		super.onDisable();
	}

	@Override
	public void onPre(Pre event) {
		Kryos.lockManager.free(this);
		pos = null;

		calc();

		if(destroy()) return;
		
		if(place()) return;
	}

	@Override
	public void spawn(SpawnEntityEvent event) {
        destroyEntity = getDestroy();

        destroy();
	}

	@Override
	public void remove(RemoveEntityEvent event) {
		// Calculate for the next tick
		if(precalc.getValue() && async.getValue()) {
			// Asynchronous
		    if (!scanInProgress) {
		        scanInProgress = true;
	
		        executor.submit(() -> {
		            try {
		                placePos = getPlace();
		                place();
		            } finally {
		                scanInProgress = false;
		            }
		        });
		    }
		}
	}
	
	public boolean destroy() {
		if(!timer.check(destroyDelay.getValue().longValue())) return false;
		
		timer.reset();
		
		long now = System.currentTimeMillis();
		long timeout = destroyBlacklistTimeout.getValue().longValue();

		IntArrayList toRemove = new IntArrayList();

		destroyed.forEach((id, time) -> {
		    if (now - time >= timeout) {
		        toRemove.add(id);
		    }
		});

		for (int id : toRemove) {
		    destroyed.remove(id);
		}

		if (destroy.getValue() && destroyEntity.isPresent()) {
			if(!Kryos.lockManager.acquire(this)) return false;
			
		    var destroy = destroyEntity.get();
		    
		    if(destroyBlacklist.getValue() && destroyed.containsKey(destroy.getKey().getId())) {
		    	System.out.println("Dupe");
		    	return false;
		    }
		    
		    destroyed.put(destroy.getKey().getId(), System.currentTimeMillis());
		    
		    mc.gameMode.attack(mc.player, destroy.getKey());
		    mc.player.swing(InteractionHand.MAIN_HAND);
		    
		    // Early removal
		    if(earlyRemove.getValue()) {
		    	mc.level.removeEntity(destroy.getKey().getId(), RemovalReason.KILLED);
		    }
		    
		    if(pos != null && pos.equals(destroy.getKey().blockPosition())) return false;

		    if(rotate.getValue()) {
				float[] rot = RotationUtil.getRotationsTo(destroy.getKey().position());
				Kryos.rotationManager.rotate(rot[0], rot[1]);
				pos = destroy.getKey().blockPosition();
		    }
		    
			return true;
		}
		
		return false;
	}

	public boolean place() {
		if (place.getValue() && placePos.isPresent()) {
			if(!Kryos.lockManager.acquire(this)) return false;
		    var place = placePos.get();
		    
			InteractionHand hand = this.hand.getValue();
			
			if(!InventoryUtil.hasItemIn(Items.END_CRYSTAL, hand))
				return false;
			
			if(destroyBlacklist.getValue() && pos != null && pos.equals(place.getKey())) return false;
			
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
		    
		    pos = place.getKey();
		    
		    return true;
	    }
		
		return false;
	}
	
	private void calc() {
		if(async.getValue()) {
			// Asynchronous
		    if (!scanInProgress) {
		        scanInProgress = true;
	
		        executor.submit(() -> {
		            try {
		                placePos = getPlace();
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

						float d = DamageUtil.getCrystalDamage(living, getPosition(living), above.getBottomCenter());
						
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

	            float d = DamageUtil.getCrystalDamage(living, getPosition(living), crystal.position());

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

	private Vec3 getPosition(Entity entity) {
		Vec3 pos = entity.position();
		
		if(predict.getValue()) {
			Vec3 vel = entity.getDeltaMovement();
			float predict = predictAmount();
			vel.multiply(new Vec3(predict, predict, predict));
			pos.add(vel);
		}
		
		return pos;
	}
	
	private float predictAmount() {
		return predict.getValue() ? predictAmount.getValue() : 0;
	}

	@Override
	public void onPost(Post event) {
		
	}
	
	static enum Action {
		PLACE,
		DESTROY;
	}
}