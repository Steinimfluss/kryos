package net.kryos.feature.impl.combat;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.impl.RenderLevelEvent;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.event.listener.impl.RenderLevelListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.BooleanSettingBuilder;
import net.kryos.feature.setting.ModeSetting;
import net.kryos.feature.setting.ModeSettingBuilder;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.NumberSettingBuilder;
import net.kryos.feature.setting.SplitterSetting;
import net.kryos.feature.setting.SplitterSettingBuilder;
import net.kryos.gui.MainTheme;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.kryos.util.BlockUtil;
import net.kryos.util.DamageUtil;
import net.kryos.util.EntityUtil;
import net.kryos.util.InventoryUtil;
import net.kryos.util.LevelRenderUtil;
import net.kryos.util.RotationUtil;
import net.kryos.util.Timer;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrystalAura extends Feature implements PlayerTickListener, RenderLevelListener, Rotator {
	private static final AABB CRYSTAL_BOX = new AABB(
	        -0.5, 0, -0.5,
	         0.5, 2,  0.5
	);
	
	private ModeSetting hand = new ModeSettingBuilder()
			.name("Hand")
			.mode("Main Hand")
			.mode("Off Hand")
			.value("Main Hand")
			.build();
	
	private NumberSetting<Float> destroyReach = new NumberSettingBuilder<Float>()
			.name("Destroy Reach")
			.value(7F)
			.min(1.0F)
			.max(8F)
			.step(0.1F)
			.build();
	
	private BooleanSetting destroy = new BooleanSettingBuilder()
			.name("Destroy")
			.value(true)
			.setting(destroyReach)
			.build();
	
	private NumberSetting<Float> placeReach = new NumberSettingBuilder<Float>()
			.name("Place Reach")
			.value(7F)
			.min(1.0F)
			.max(8F)
			.step(0.1F)
			.build();
	
	private BooleanSetting place = new BooleanSettingBuilder()
			.name("Place")
			.value(true)
			.setting(placeReach)
			.build();
	
	private NumberSetting<Long> delay = new NumberSettingBuilder<Long>()
			.name("Action Delay")
			.value(50L)
			.min(0L)
			.max(1000L)
			.step(10L)
			.build();
	
	private NumberSetting<Float> minDamage = new NumberSettingBuilder<Float>()
			.name("Min Target Damage")
			.value(10F)
			.min(1F)
			.max(100F)
			.step(0.5F)
			.build();
	
	private NumberSetting<Float> maxDamage = new NumberSettingBuilder<Float>()
			.name("Max Self Damage")
			.value(10F)
			.min(1F)
			.max(100F)
			.step(0.5F)
			.build();
	
	private SplitterSetting damageSplitter = new SplitterSettingBuilder()
			.name("Damage")
			.setting(minDamage)
			.setting(maxDamage)
			.build();
	
	private NumberSetting<Float> minHealth = new NumberSettingBuilder<Float>()
			.name("Min Health")
			.value(10F)
			.min(1F)
			.max(30F)
			.step(0.5F)
			.build();
	
	private BooleanSetting antiSuicide = new BooleanSettingBuilder()
			.name("Anti Suicide")
			.value(true)
			.setting(minHealth)
			.build();
	
	private Timer interactionTimer = new Timer();
	private BlockPos placePos;
	private Entity target;
	
	public CrystalAura() {
		super("CrystalAura", FeatureCategory.COMBAT);
		addSettings(hand, destroy, place, delay, damageSplitter, antiSuicide);
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
		placePos = null;
		target = null;
		Kryos.rotationBus.unsubscribe(this);
		
		if(!interactionTimer.check(delay.getValue()))
			return;
		else
			interactionTimer.reset();
		
		Scored<BlockPos> place = getBestPlace();
		Scored<EndCrystal> destroy = getBestDestroy();
		
		Action action = getBetterAction(place, destroy);
		
		if(action == Action.PLACE) {
			InteractionHand crystalHand = getDesiredCrystalHand();
			
			if(InventoryUtil.hasItemIn(Items.END_CRYSTAL, crystalHand)) {
		        Vec3 hit = BlockUtil.getClosestPointOnFace(place.object(), Direction.UP);
		        placePos = place.object();
		        
				float[] rot = RotationUtil.getRotationsTo(hit);
	
				Kryos.rotationBus.subscribe(this);
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
			}
		} 
		
		if (action == Action.DESTROY) {
		    Kryos.rotationBus.subscribe(this);
		    float[] rot = RotationUtil.getRotationsTo(EntityUtil.getClosestPoint(destroy.object()));

		    if (Kryos.rotationBus.rotate(rot[0], rot[1], this)) {
		        target = destroy.object();
		        mc.gameMode.attack(mc.player, target);
		        mc.player.swing(InteractionHand.MAIN_HAND);
		    }
		}
	}

	@Override
	public void onPost(Post event) {

	}
	
	public Action getBetterAction(Scored<BlockPos> place, Scored<EndCrystal> destroy) {
		if(destroy != null && score(destroy) >= score(place) && this.destroy.enabled)
			return Action.DESTROY;
		if(place != null && (score(place) > score(destroy)  && this.place.enabled))
			return Action.PLACE;
		return Action.NONE;
	}
	
	public float score(Scored<?> scored) {
		if(scored == null)
			return 0F;
		
		return scored.score();
	}
	
	public Scored<EndCrystal> getBestDestroy() {
	    List<EndCrystal> crystals = new ArrayList<>();
	    List<LivingEntity> targets = new ArrayList<>();

	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (entity instanceof EndCrystal crystal) {
	            crystals.add(crystal);
	        } else if (entity instanceof LivingEntity living
	                && living != mc.player
	                && living.isAlive()
	                && living.distanceTo(mc.player) < 16) {
	            targets.add(living);
	        }
	    }

	    Scored<EndCrystal> best = null;
	    float bestScore = 0f;

	    for (EndCrystal crystal : crystals) {

	        if (EntityUtil.getClosestPoint(crystal)
	                .distanceTo(mc.player.getEyePosition()) > destroyReach.getValue()) {
	            continue;
	        }

	        for (LivingEntity t : targets) {
	            float targetDamage = DamageUtil.getCrystalDamage(
	                    t,
	                    new Vec3(t.getX(), t.getY(), t.getZ()),
	                    crystal.position()
	            );

	            if (targetDamage < minDamage.getValue()) continue;

	            float selfDamage = DamageUtil.getCrystalDamage(
	                    mc.player,
	                    new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
	                    crystal.position()
	            );

	            if (selfDamage > maxDamage.getValue()) continue;
	            if (antiSuicide.enabled && mc.player.getHealth() - selfDamage < minHealth.getValue()) continue;

	            if (targetDamage > bestScore) {
	                bestScore = targetDamage;
	                best = new Scored<>(crystal, targetDamage);
	            }
	        }
	    }
	    return best;
	}

	public Scored<BlockPos> getBestPlace() {
	    List<BlockPos> positions = getPlaceableBlocks(mc.player.blockPosition());
	    List<LivingEntity> targets = new ArrayList<>();

	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (entity instanceof LivingEntity living
	                && living != mc.player
	                && living.isAlive()
	                && living.distanceTo(mc.player) < 16) {
	            targets.add(living);
	        }
	    }

	    Scored<BlockPos> best = null;
	    float bestScore = 0f;

	    for (BlockPos pos : positions) {
	        Vec3 crystalPos = pos.above().getBottomCenter();

	        float maxTargetDamage = 0f;

	        for (LivingEntity t : targets) {
	            float dmg = DamageUtil.getCrystalDamage(
	                    t,
	                    new Vec3(t.getX(), t.getY(), t.getZ()),
	                    crystalPos
	            );
	            if (dmg > maxTargetDamage) {
	                maxTargetDamage = dmg;
	            }
	        }

	        if (maxTargetDamage < minDamage.getValue()) continue;

	        float selfDamage = DamageUtil.getCrystalDamage(
	                mc.player,
	                new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
	                crystalPos
	        );

	        if (selfDamage > maxDamage.getValue()) continue;
	        if (antiSuicide.enabled && mc.player.getHealth() - selfDamage < minHealth.getValue()) continue;

	        if (BlockUtil.getClosestPointOnFace(pos, Direction.UP)
	                .distanceTo(mc.player.getEyePosition()) > placeReach.getValue()) {
	            continue;
	        }

	        if (maxTargetDamage > bestScore) {
	            bestScore = maxTargetDamage;
	            best = new Scored<>(pos, maxTargetDamage);
	        }
	    }

	    return best;
	}
	
	private InteractionHand getDesiredCrystalHand() {
		return hand.getString().equalsIgnoreCase("Main Hand") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}

	public List<BlockPos> getPlaceableBlocks(BlockPos player) {
		List<BlockPos> positions = new ArrayList<BlockPos>();
		int reach = Math.round(placeReach.getValue()) + 1;
		
		for(int i = -reach; i < reach; i++) {
			for(int j = -reach; j < reach; j++) {
				for(int k = -reach; k < reach; k++) {
					BlockPos pos = player.offset(i, j, k);
					BlockPos above = player.offset(i, j + 1, k);
					
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

	private boolean entityIntersects(BlockPos pos) {
	    AABB box = getCrystalPlacementBox(pos);

	    for (Entity e : mc.level.entitiesForRendering()) {
	        if (e == mc.player) continue;
	        if (!e.isAlive()) continue;

	        if (e.getBoundingBox().intersects(box)) {
	            return true;
	        }
	    }
	    return false;
	}

	private boolean playerIntersects(BlockPos pos) {
	    AABB box = getCrystalPlacementBox(pos);
	    return mc.player.getBoundingBox().intersects(box);
	}

    public AABB getCrystalPlacementBox(BlockPos pos) {
        return CRYSTAL_BOX.move(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

	@Override
	public RotationPrivilege getRotationPrivilege() {
		return RotationPrivilege.LOW;
	}
	
	public record Scored<T>(T object, float score) {}
	
	static enum Action {
		PLACE,
		DESTROY,
		NONE;
	}

	@Override
	public void renderLevel(RenderLevelEvent event) {
        Camera camera = mc.gameRenderer.getMainCamera();
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().mul(event.getModelViewMatrix());
        poseStack.translate(
            -camera.position().x,
            -camera.position().y,
            -camera.position().z
        );
        
        poseStack.pushPose();
        if(target != null)
        	LevelRenderUtil.drawFilledBox(poseStack, new AABB(target.blockPosition().below()), MainTheme.SELECTED);
        if(placePos != null)
        	LevelRenderUtil.drawFilledBox(poseStack, new AABB(placePos), MainTheme.SELECTED);
		poseStack.popPose();
	}
}