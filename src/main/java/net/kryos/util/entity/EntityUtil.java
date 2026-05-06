package net.kryos.util.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
	private static final AABB CRYSTAL_BOX = new AABB(
	        -0.5, 0, -0.5,
	         0.5, 2,  0.5
	);
	
	public static boolean isInRange(LivingEntity e, double range) {
	    if (mc.player == null) return false;

	    double px = mc.player.getX();
	    double py = mc.player.getEyeY();
	    double pz = mc.player.getZ();

	    double ex = e.getX();
	    double ey = e.getEyeY();
	    double ez = e.getZ();

	    double dx = px - ex;
	    double dy = py - ey;
	    double dz = pz - ez;

	    return (dx * dx + dy * dy + dz * dz) <= (range * range);
	}
	
	public static Vec3 getClosestPoint(Entity entity) {
	    if (mc.player == null) return Vec3.ZERO;

	    Vec3 playerPos = mc.player.getEyePosition();
	    var box = entity.getBoundingBox();

	    double x = clamp(playerPos.x, box.minX, box.maxX);
	    double y = clamp(playerPos.y, box.minY, box.maxY);
	    double z = clamp(playerPos.z, box.minZ, box.maxZ);

	    return new Vec3(x, y, z);
	}

	public static boolean crystalIntercects(BlockPos pos) {
	    AABB box = getCrystalPlacementBox(pos);

	    for (Entity e : mc.level.entitiesForRendering()) {
	        if (e == mc.player) continue;

	        if (e.getBoundingBox().intersects(box)) {
	            return true;
	        }
	    }
	    return false;
	}

	private static double clamp(double v, double min, double max) {
	    return Math.max(min, Math.min(max, v));
	}
	
    public static AABB getCrystalPlacementBox(BlockPos pos) {
        return CRYSTAL_BOX.move(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
}