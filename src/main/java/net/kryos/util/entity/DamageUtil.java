package net.kryos.util.entity;

import java.util.Set;

import net.kryos.util.item.EnchantUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DamageUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
    public static float getCrystalDamage(LivingEntity target, Vec3 targetPos, Vec3 explosionPos) {
        return explosionDamage(target, targetPos, explosionPos, 12f);
    }
    
    public static float getCrystalDamage(LivingEntity target, Vec3 targetPos, Vec3 explosionPos, Set<BlockPos> ignoredBlocks) {
        return explosionDamage(target, targetPos, explosionPos, 12f, ignoredBlocks);
    }

    public static float getDamageAfterAbsorb(
    		final Player victim, final float damage, final float totalArmor, final float armorToughness
	) {
		float toughness = 2.0F + armorToughness / 4.0F;
		float realArmor = Mth.clamp(totalArmor - damage / toughness, totalArmor * 0.2F, 20.0F);
		float armorFraction = realArmor / 25.0F;
		
		float damageMultiplier = 1.0F - armorFraction;
		return damage * damageMultiplier;
	}
    
    public static float explosionDamage(LivingEntity target, Vec3 targetPos, Vec3 explosionPos, float power) {
        double modDistance = targetPos.distanceTo(explosionPos);
        if (modDistance > power) return 0f;

        double exposure = getSeenPercent(explosionPos, target);
        double impact = (1 - (modDistance / power)) * exposure;
        float damage = (int) ((impact * impact + impact) / 2 * 7 * 12 + 1);

        return calculateReductions(damage, target, mc.level.damageSources().explosion(null));
    }
    
    public static float explosionDamage(LivingEntity target, Vec3 targetPos, Vec3 explosionPos, float power, Set<BlockPos> ignoredBlocks) {
        double modDistance = targetPos.distanceTo(explosionPos);
        if (modDistance > power) return 0f;

        double exposure = getSeenPercent(explosionPos, target, ignoredBlocks);
        double impact = (1 - (modDistance / power)) * exposure;
        float damage = (int) ((impact * impact + impact) / 2 * 7 * 12 + 1);

        return calculateReductions(damage, target, mc.level.damageSources().explosion(null));
    }

    public static float calculateReductions(float damage, Entity entity, DamageSource damageSource) {
    	if(entity.isInvulnerable()) {
        	return 0F;
        }
    	if(entity instanceof Player player && (player.gameMode() == GameType.CREATIVE || player.gameMode() == GameType.SPECTATOR)) {
    		return 0F;
    	};
    	
        if (damageSource.scalesWithDifficulty()) {
            switch (mc.level.getDifficulty()) {
                case EASY     -> damage = Math.min(damage / 2 + 1, damage);
                case HARD     -> damage *= 1.5f;
                default -> {break;}
            }
        }

        if (entity instanceof LivingEntity livingEntity) {
        	damage = CombatRules.getDamageAfterAbsorb(livingEntity, damage, damageSource, getArmor(livingEntity), (float) livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        	
            damage = resistanceReduction(livingEntity, damage);

            damage = protectionReduction(livingEntity, damage, damageSource);
        }

        return Math.max(damage, 0);
    }
    
    private static float protectionReduction(LivingEntity player, float damage, DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return damage;
        }

        int protection = 0;

        EquipmentSlot[] armorSlots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };


        for (EquipmentSlot slot : armorSlots) {
            ItemInstance stack = player.getItemBySlot(slot);
            
            protection += EnchantUtil.getEnchantmentLevel(stack, Enchantments.PROTECTION);

            if (source.is(DamageTypeTags.IS_FIRE)) {
                protection += 2 * EnchantUtil.getEnchantmentLevel(stack, Enchantments.FIRE_PROTECTION);
            }

            if (source.is(DamageTypeTags.IS_EXPLOSION)) {
                protection += 2 * EnchantUtil.getEnchantmentLevel(stack, Enchantments.BLAST_PROTECTION);
            }

            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                protection += 2 * EnchantUtil.getEnchantmentLevel(stack, Enchantments.PROJECTILE_PROTECTION);
            }

            if (source.is(DamageTypeTags.IS_FALL)) {
                protection += 2 * EnchantUtil.getEnchantmentLevel(stack, Enchantments.FEATHER_FALLING);
            }
        }

        return CombatRules.getDamageAfterMagicAbsorb(damage, protection);
    }

    private static float resistanceReduction(LivingEntity player, float damage) {
        MobEffectInstance resistance = player.getEffect(MobEffects.RESISTANCE);
        if (resistance != null) {
            int lvl = resistance.getAmplifier() + 1;
            damage *= (1 - (lvl * 0.2f));
        }

        return Math.max(damage, 0);
    }
    
    private static float getArmor(LivingEntity entity) {
        return (float) Math.floor(entity.getAttributeValue(Attributes.ARMOR));
    }

    public static float getSeenPercent(Vec3 explosionPos, Entity entity) {
        AABB bb = entity.getBoundingBox();

        double stepX = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double stepY = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double stepZ = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);

        if (stepX < 0 || stepY < 0 || stepZ < 0) return 0.0F;

        int hits = 0;
        int total = 0;

        for (double x = 0; x <= 1; x += stepX) {
            for (double y = 0; y <= 1; y += stepY) {
                for (double z = 0; z <= 1; z += stepZ) {

                    double px = Mth.lerp(x, bb.minX, bb.maxX);
                    double py = Mth.lerp(y, bb.minY, bb.maxY);
                    double pz = Mth.lerp(z, bb.minZ, bb.maxZ);

                    Vec3 sample = new Vec3(px, py, pz);

                    HitResult hit = entity.level().clip(new ClipContext(
                            sample,
                            explosionPos,
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            entity
                    ));

                    if (hit.getType() == HitResult.Type.MISS) hits++;
                    total++;
                }
            }
        }

        return total == 0 ? 0 : (float)hits / total;
    }
    
    public static float getSeenPercent(Vec3 explosionPos, Entity entity, Set<BlockPos> ignoredBlocks) {
        AABB bb = entity.getBoundingBox();

        double stepX = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double stepY = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double stepZ = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);

        if (stepX < 0 || stepY < 0 || stepZ < 0) return 0.0F;

        int hits = 0;
        int total = 0;

        for (double x = 0; x <= 1; x += stepX) {
            for (double y = 0; y <= 1; y += stepY) {
                for (double z = 0; z <= 1; z += stepZ) {

                    double px = Mth.lerp(x, bb.minX, bb.maxX);
                    double py = Mth.lerp(y, bb.minY, bb.maxY);
                    double pz = Mth.lerp(z, bb.minZ, bb.maxZ);

                    Vec3 sample = new Vec3(px, py, pz);

                    HitResult hit = entity.level().clip(new ClipContext(
                            sample,
                            explosionPos,
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            entity
                    ));

                    boolean blocked = false;

                    if (hit.getType() == HitResult.Type.BLOCK) {
                        BlockPos hitPos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();

                        if (!ignoredBlocks.contains(hitPos)) {
                            blocked = true;
                        }
                    }

                    if (!blocked) hits++;
                    total++;
                }
            }
        }

        return total == 0 ? 0 : (float) hits / total;
    }
}
