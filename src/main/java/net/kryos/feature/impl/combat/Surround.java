package net.kryos.feature.impl.combat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import net.kryos.notification.Notification;
import net.kryos.notification.NotificationType;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.kryos.util.RotationUtil;
import net.kryos.util.Timer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Surround extends Feature implements PlayerTickListener, Rotator {

    private final NumberSetting<Long> placeDelay = new NumberSettingBuilder<Long>()
    		.name("Place Delay")
    		.value(100L)
    		.min(0L)
    		.max(1000L)
    		.step(10L)
    		.build();
    
    private final BooleanSetting silentSwap = new BooleanSettingBuilder()
    		.name("Silent Swap")
    		.value(true)
    		.build();

    private final Timer placeTimer = new Timer();

    public Surround() {
        super("Surround", FeatureCategory.COMBAT);
        setSettings(placeDelay, silentSwap);
    }

    @Override
    protected void onEnable() {
        Kryos.eventBus.subscribe(this);
        placeTimer.reset();
    }

    @Override
    protected void onDisable() {
        Kryos.eventBus.unsubscribe(this);
		Kryos.rotationBus.unsubscribe(this);
    }

    @Override
    public void onPre(Pre event) {
    	if(mc.screen != null) return;
    	
    	if(mc.player == null) return;
    	
    	Kryos.rotationBus.unsubscribe(this);
        if (mc.player == null || mc.level == null) return;

        int slot = findBlockInHotbar();
        if (slot == -1) return;

        List<BlockPos> targets = getSurroundPositions();

        for (BlockPos pos : targets) {
            if (!mc.level.getBlockState(pos).isAir()) continue;

            if (entityIntersects(pos)) continue;

            if (playerIntersects(pos)) continue;

            if (tryPlaceWithNeighbors(pos, slot)) {
                placeTimer.reset();
                return;
            }
        }
    }

    @Override
    public void onPost(Post event) {
    	
    }

    private int findBlockInHotbar() {
        int obsidian = -1;
        int bedrock = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.getItem() instanceof BlockItem blockItem) {
                var block = blockItem.getBlock();
                if (block == Blocks.OBSIDIAN) obsidian = i;
                if (block == Blocks.BEDROCK) bedrock = i;
            }
        }

        return obsidian != -1 ? obsidian : bedrock;
    }

    private Set<BlockPos> getBlocksAroundPlayer() {
        Set<BlockPos> result = new HashSet<>();

        var box = mc.player.getBoundingBox();

        double epsylon = 0.000001;
        double minX = box.minX;
        double maxX = box.maxX - epsylon;
        double minZ = box.minZ;
        double maxZ = box.maxZ - epsylon;
        double y = Math.floor(box.minY);

        double[][] corners = {
                {minX, minZ},
                {minX, maxZ},
                {maxX, minZ},
                {maxX, maxZ}
        };

        for (double[] c : corners) {
            int bx = (int)Math.floor(c[0]);
            int bz = (int)Math.floor(c[1]);
            int by = (int)y;

            result.add(new BlockPos(bx, by, bz));
        }

        return result;
    }

    private static final int[][] OFFSETS = {
            {1, 0, 0},
            {-1, 0, 0},
            {0, 0, 1},
            {0, 0, -1},
            {1, -1, 0},
            {-1, -1, 0},
            {0, -1, 1},
            {0, -1, -1}
    };
    
    private boolean playerIntersects(BlockPos pos) {
        var box = new net.minecraft.world.phys.AABB(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
        );

        return mc.player.getBoundingBox().intersects(box);
    }

    private List<BlockPos> getSurroundPositions() {
        Set<BlockPos> under = getBlocksAroundPlayer();
        Set<BlockPos> surround = new HashSet<>();

        for (BlockPos base : under) {
            for (int[] off : OFFSETS) {
                surround.add(base.offset(off[0], off[1], off[2]));
            }
        }

        return new ArrayList<>(surround);
    }
    

    @SuppressWarnings("deprecation")
	private boolean tryPlaceWithNeighbors(BlockPos pos, int slot) {
        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};

        for (Direction dir : dirs) {
            BlockPos neighbor = pos.relative(dir);

            if (!mc.level.getBlockState(neighbor).isSolid()) continue;

            Vec3 hit = new Vec3(
                    neighbor.getX() + 0.5 - dir.getStepX() * 0.5,
                    neighbor.getY() + 0.5 - dir.getStepY() * 0.5,
                    neighbor.getZ() + 0.5 - dir.getStepZ() * 0.5
            );

            float[] rot = RotationUtil.getRotationsTo(hit);
            
            Kryos.rotationBus.subscribe(this);
            if(Kryos.rotationBus.rotate(rot[0], rot[1], this)) {
	            int prev = mc.player.getInventory().getSelectedSlot();
	            mc.player.getInventory().setSelectedSlot(slot);
	
	            BlockHitResult bhr = new BlockHitResult(
	                    hit,
	                    dir.getOpposite(),
	                    neighbor,
	                    false
	            );

	            boolean success = false;
	            if (placeTimer.check(placeDelay.getValue())) {
		            placeTimer.reset();
		            success = mc.gameMode.useItemOn(
		                    mc.player,
		                    InteractionHand.MAIN_HAND,
		                    bhr
		            ).consumesAction();
	            }

	            if(silentSwap.enabled)
	            	mc.player.getInventory().setSelectedSlot(prev);
	            if (success) return true;
            }
        }

        return false;
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
		return RotationPrivilege.HIGHEST;
	}
}