package net.kryos.util.math;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class RotationUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    public static float[] getRotationsTo(Entity target) {
        Vec3 eyes = mc.player.getEyePosition();
        Vec3 center = target.getBoundingBox().getCenter();
        return getRotationsTo(eyes, center);
    }

    public static float[] getRotationsTo(Vec3 target) {
        Vec3 eyes = mc.player.getEyePosition();
        return getRotationsTo(eyes, target);
    }
    
    public static float[] getRotationsTo(BlockPos pos, Direction dir) {
        Vec3 eyes = mc.player.getEyePosition();

        Vec3 target = pos.getCenter().add(
            dir.getStepX() * 0.5,
            dir.getStepY() * 0.5,
            dir.getStepZ() * 0.5
        );

        return getRotationsTo(eyes, target);
    }

    public static float[] getRotationsTo(Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;

        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        yaw = mc.player.getYRot() + wrapDegrees(yaw - mc.player.getYRot());
        pitch = mc.player.getXRot() + wrapDegrees(pitch - mc.player.getXRot());

        return new float[]{yaw, pitch};
    }

    private static float wrapDegrees(float value) {
        value %= 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }
}