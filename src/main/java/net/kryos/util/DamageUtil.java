package net.kryos.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DamageUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
    public static float getCrystalDamage(LivingEntity target, Vec3 targetPos, Vec3 explosionPos) {
        return explosionDamage(target, targetPos, explosionPos, 12f);
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
    
    public static float calculateReductions(float damage, Entity entity, DamageSource damageSource) {
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

        var registryHolder = player.level().registryAccess()
                .get(Registries.ENCHANTMENT)
                .orElse(null);

        if (registryHolder == null) {
            return damage;
        }

        Registry<Enchantment> registry = registryHolder.value();

        Holder<Enchantment> prot  = registry.get(Enchantments.PROTECTION).orElse(null);
        Holder<Enchantment> fire  = registry.get(Enchantments.FIRE_PROTECTION).orElse(null);
        Holder<Enchantment> blast = registry.get(Enchantments.BLAST_PROTECTION).orElse(null);
        Holder<Enchantment> proj  = registry.get(Enchantments.PROJECTILE_PROTECTION).orElse(null);
        Holder<Enchantment> fall  = registry.get(Enchantments.FEATHER_FALLING).orElse(null);

        for (EquipmentSlot slot : armorSlots) {
            ItemInstance stack = player.getItemBySlot(slot);

            if (prot != null) {
                protection += EnchantmentHelper.getItemEnchantmentLevel(prot, stack);
            }

            if (source.is(DamageTypeTags.IS_FIRE) && fire != null) {
                protection += 2 * EnchantmentHelper.getItemEnchantmentLevel(fire, stack);
            }

            if (source.is(DamageTypeTags.IS_EXPLOSION) && blast != null) {
                protection += 2 * EnchantmentHelper.getItemEnchantmentLevel(blast, stack);
            }

            if (source.is(DamageTypeTags.IS_PROJECTILE) && proj != null) {
                protection += 2 * EnchantmentHelper.getItemEnchantmentLevel(proj, stack);
            }

            if (source.is(DamageTypeTags.IS_FALL) && fall != null) {
                protection += 3 * EnchantmentHelper.getItemEnchantmentLevel(fall, stack);
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
}
