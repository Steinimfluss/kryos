package net.kryos.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EntityUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
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

	private static double clamp(double v, double min, double max) {
	    return Math.max(min, Math.min(max, v));
	}
}