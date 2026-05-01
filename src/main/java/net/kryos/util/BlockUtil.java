package net.kryos.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
	public static double getBreakDelta(ItemStack stack, BlockState state, BlockPos pos) {
		float hardness = state.getDestroySpeed(mc.level, pos);
        return getBlockBreakingSpeed(stack, state) / hardness / (stack.canDestroyBlock(state, mc.level, pos, mc.player) ? 30 : 100);
    }
	
	public static float getDestroySpeed(final ItemStack itemStack, final BlockState state) {
		Tool tool = itemStack.get(DataComponents.TOOL);
		return (float) (tool != null ? getBlockBreakingSpeed(itemStack, state) : 1.0F);
	}
	
	private static double getBlockBreakingSpeed(ItemStack stack, BlockState block) {
        double speed = stack.getDestroySpeed(block);

        if (speed > 1) {
        	int efficiency = EnchantmentHelper.getItemEnchantmentLevel(
        		    mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
        		        .getOrThrow(Enchantments.EFFICIENCY),
        		    stack
        		);

            if (efficiency > 0 && !stack.isEmpty()) speed += efficiency * efficiency + 1;
        }

        if (mc.player.hasEffect(MobEffects.HASTE)) {
            speed *= 1 + (mc.player.getEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.hasEffect(MobEffects.MINING_FATIGUE)) {
            float k = switch (mc.player.getEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };

            speed *= k;
        }

        if (mc.player.isInWater()) {
            speed *= mc.player.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
        }

        if (!mc.player.onGround()) {
            speed /= 5.0F;
        }

        return speed;
    }
	
	public static Vec3 getClosestPointOnFace(Vec3 p, BlockPos pos, Direction face) {
	    if (mc.player == null) return Vec3.ZERO;

	    double minX = pos.getX();
	    double minY = pos.getY();
	    double minZ = pos.getZ();
	    double maxX = minX + 1;
	    double maxY = minY + 1;
	    double maxZ = minZ + 1;

	    double x = clamp(p.x, minX, maxX);
	    double y = clamp(p.y, minY, maxY);
	    double z = clamp(p.z, minZ, maxZ);

	    switch (face) {
	        case DOWN -> y = minY;
	        case UP   -> y = maxY;
	        case NORTH -> z = minZ;
	        case SOUTH -> z = maxZ;
	        case WEST  -> x = minX;
	        case EAST  -> x = maxX;
	    }

	    return new Vec3(x, y, z);
	}
	
	public static Vec3 getClosestPointOnFace(BlockPos pos, Direction face) {
	    if (mc.player == null) return Vec3.ZERO;

	    return getClosestPointOnFace(mc.player.position(), pos, face);
	}

	private static double clamp(double v, double min, double max) {
	    return Math.max(min, Math.min(max, v));
	}
}