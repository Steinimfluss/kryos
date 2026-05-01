package net.kryos.feature.impl.world;

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
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.kryos.util.BlockUtil;
import net.kryos.util.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Scaffold extends Feature implements Rotator, PlayerTickListener {
	private NumberSetting<Float> reach = new NumberSettingBuilder<Float>()
			.name("Reach")
			.value(8F)
			.min(1F)
			.max(10F)
			.step(0.1F)
			.build();

	private NumberSetting<Float> towerSpeed = new NumberSettingBuilder<Float>()
			.name("Speed")
			.value(0.1F)
			.min(0F)
			.max(1F)
			.step(0.01F)
			.build();
	
	private BooleanSetting tower = new BooleanSettingBuilder()
			.name("Tower")
			.value(true)
			.setting(towerSpeed)
			.build();
	
	public Scaffold() {
		super("Scaffold", FeatureCategory.WORLD);
		addSettings(reach, tower);
	}

	@Override
	protected void onEnable() {
		Kryos.rotationBus.unsubscribe(this);
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.rotationBus.unsubscribe(this);
		Kryos.eventBus.unsubscribe(this);
	}

	@Override
	public void onPre(Pre event) {
		if(mc.level.getBlockState(mc.player.blockPosition().offset(0, -1, 0)).getBlock() != Blocks.AIR) {
			if(mc.options.keyJump.isDown() && tower.enabled) {
				Vec3 delta = mc.player.getDeltaMovement();
				Vec3 newDelta = new Vec3(delta.x, towerSpeed.getValue(), delta.z);
				
				mc.player.setDeltaMovement(newDelta);
			}
			return;
		}
		
		int oldSlot = mc.player.getInventory().getSelectedSlot();
		int blockSlot = findBlockSlot();
		
		if(blockSlot == -1) return;
		
		BlockPos pos = getClosestUnderPlayerPos();
		if(pos == null) return;
		
		Direction dir = getClosestFace(pos);
		if(dir == null) return;
		
		Vec3 point = BlockUtil.getClosestPointOnFace(pos, dir);
		
		float[] rot = RotationUtil.getRotationsTo(point);
		
		Kryos.rotationBus.subscribe(this);
		if(Kryos.rotationBus.rotate(rot[0], rot[1], this)) {
			
			mc.player.getInventory().setSelectedSlot(blockSlot);
			BlockHitResult bhr = new BlockHitResult(
					point,
	                dir,
	                pos,
	                false
	        );
	
	        mc.gameMode.useItemOn(
	                mc.player,
	                InteractionHand.MAIN_HAND,
	                bhr
	        ).consumesAction();
	        
	        mc.player.swing(InteractionHand.MAIN_HAND);
			mc.player.getInventory().setSelectedSlot(oldSlot);
		}
	}

	@Override
	public void onPost(Post event) {
		
	}
	
	private int findBlockSlot() {
	    for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
	        if (!Inventory.isHotbarSlot(i)) continue;

	        ItemStack stack = mc.player.getInventory().getItem(i);
	        if (stack.isEmpty()) continue;

	        if (isValidScaffoldBlock(stack)) {
	            return i;
	        }
	    }
	    return -1;
	}
	
	@SuppressWarnings("deprecation")
	private boolean isValidScaffoldBlock(ItemStack stack) {
	    if (!(stack.getItem() instanceof BlockItem blockItem)) return false;

	    Block block = blockItem.getBlock();
	    BlockState state = block.defaultBlockState();

	    return state.isSolid() && state.isCollisionShapeFullBlock(mc.level, BlockPos.ZERO);
	}

	@SuppressWarnings("deprecation")
	public BlockPos getClosestUnderPlayerPos() {
	    if (mc.player == null) return null;

	    BlockPos blockBelow = mc.player.blockPosition().below();
	    Vec3 eye = mc.player.getEyePosition();

	    BlockPos bestPos = null;
	    double bestDist = Double.MAX_VALUE;

	    int reach = (int) (this.reach.getValue() + 1);

	    for (int i = -reach; i < reach; i++) {
	        for (int j = -reach; j < 1; j++) {
	            for (int k = -reach; k < reach; k++) {
	                BlockPos pos = blockBelow.offset(i, j, k);
	                if(!mc.level.getBlockState(pos).isSolid()) continue;

	                double dist = eye.distanceTo(Vec3.atCenterOf(pos));

	                if (dist < bestDist) {
	                    bestDist = dist;
	                    bestPos = pos;
	                }
	            }
	        }
	    }

	    return bestPos;
	}
	
	public Direction getClosestFace(BlockPos pos) {
	    Vec3 feet = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());

	    double bestDist = Double.MAX_VALUE;
	    Direction bestFace = null;

	    for (Direction face : Direction.values()) {
	        if (entityIntersects(pos)) continue;

	        Vec3 point = switch (face) {
	            case UP -> new Vec3(
	                pos.getX() + 0.5,
	                pos.getY() + 1.0,
	                pos.getZ() + 0.5
	            );
	            case DOWN -> new Vec3(
	                pos.getX() + 0.5,
	                pos.getY(),
	                pos.getZ() + 0.5
	            );
	            case NORTH -> new Vec3(
	                pos.getX() + 0.5,
	                pos.getY() + 0.5,
	                pos.getZ()
	            );
	            case SOUTH -> new Vec3(
	                pos.getX() + 0.5,
	                pos.getY() + 0.5,
	                pos.getZ() + 1.0
	            );
	            case WEST -> new Vec3(
	                pos.getX(),
	                pos.getY() + 0.5,
	                pos.getZ() + 0.5
	            );
	            case EAST -> new Vec3(
	                pos.getX() + 1.0,
	                pos.getY() + 0.5,
	                pos.getZ() + 0.5
	            );
	        };

	        double dist = point.distanceTo(feet);

	        if (dist <= bestDist) {
	            bestDist = dist;
	            bestFace = face;
	        }
	    }

	    return bestFace;
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

	@Override
	public RotationPrivilege getRotationPrivilege() {
		return RotationPrivilege.LOWEST;
	}
}