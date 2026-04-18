package net.kryos.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

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
}