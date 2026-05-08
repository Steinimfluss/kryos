package net.kryos.util.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class CrystalUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
	private static final AABB CRYSTAL_BOX = new AABB(
	        -0.5, 0, -0.5,
	         0.5, 2,  0.5
	);
	
	public static boolean placeIntercect(BlockPos pos) {
	    AABB box = getCrystalPlacementBox(pos);

	    for (Entity e : mc.level.entitiesForRendering()) {
	        if (e.getBoundingBox().intersects(box)) {
	            return true;
	        }
	    }
	    return false;
	}
	
    public static AABB getCrystalPlacementBox(BlockPos pos) {
        return CRYSTAL_BOX.move(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
}
